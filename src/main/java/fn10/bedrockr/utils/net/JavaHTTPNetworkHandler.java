package fn10.bedrockr.utils.net;

import fn10.bedrockr.utils.RLogUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static fn10.bedrockr.utils.RFileOperations.LOG;

public class JavaHTTPNetworkHandler extends NetworkHandler {
    
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    
    private HttpRequest makeGet(URI url) {
        return HttpRequest.newBuilder(url).GET().build();
    }

    private <T> Pair<Integer, T> get(URI url, HttpResponse.BodyHandler<T> handler) throws IOException, InterruptedException {
        HttpResponse<T> respo = client.send(makeGet(url), handler);
        return Pair.of(respo.statusCode(), respo.body());
    }

    private <T> CompletableFuture<Pair<Integer, T>> getAsync(URI url, HttpResponse.BodyHandler<T> handler) throws IOException, InterruptedException {
        CompletableFuture<HttpResponse<T>> respo = client.sendAsync(makeGet(url), handler);
        return respo.thenApply(v -> Pair.of(v.statusCode(), v.body()));
    }
    
    @Override
    public boolean isOnline() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 4000);
            return true;
        } catch (IOException e) {
            RLogUtils.warnException("Failed to connect to the internet", e);
            return false;
        }
    }

    @Override
    public Pair<Integer, String> postJSON(URI uri, Object obj) throws Exception {
        return null;
    }

    @Override
    public Pair<Integer, String> getJSON(URI url) throws Exception {
        return get(url, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public CompletableFuture<Pair<Integer, String>> getJSONAsync(URI url) throws Exception {
        return getAsync(url, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public Pair<Integer, byte[]> getBytes(URI url) throws Exception {
        return get(url, HttpResponse.BodyHandlers.ofByteArray());
    }

    @Override
    public CompletableFuture<Pair<Integer, byte[]>> getBytesAsync(URI url) throws Exception {
        return getAsync(url, HttpResponse.BodyHandlers.ofByteArray());
    }

    @Override
    public Triple<Integer, Integer, InputStream> getStream(URI url) throws Exception {
        HttpRequest req = makeGet(url);
        HttpResponse<InputStream> resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
        Optional<String> lengthStr = resp.headers().firstValue("Content-Length");
        int length;
        if (lengthStr.isEmpty()) length = 0;
        else length = Integer.valueOf(lengthStr.get());
        return Triple.of(resp.statusCode(), length, resp.body());
    }
}
