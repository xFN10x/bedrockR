package fn10.bedrockr.utils.net;

import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class JavaHTTPNetworkHandler extends NetworkHandler {
    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public Pair<Integer, String> postJSON(URI uri, Object obj) throws Exception {
        return null;
    }

    @Override
    public Pair<Integer, String> getJSON(URI url) throws Exception {
        return null;
    }

    @Override
    public CompletableFuture<Pair<Integer, String>> getJSONAsync(URI url) throws Exception {
        return null;
    }

    @Override
    public Pair<Integer, byte[]> getBytes(URI url) throws Exception {
        return null;
    }

    @Override
    public CompletableFuture<Pair<Integer, byte[]>> getBytesAsync(URI url) throws Exception {
        return null;
    }
}
