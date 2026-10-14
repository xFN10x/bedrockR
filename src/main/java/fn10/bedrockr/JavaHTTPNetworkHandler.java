package fn10.bedrockr;

import fn10.bedrockr.utils.net.NetworkHandler;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class JavaHTTPNetworkHandler extends NetworkHandler {

    @Override
    public String getJSON(URI url) throws Exception {
        return "";
    }

    @Override
    public CompletableFuture<String> getJSONAsync(URI url) throws Exception {
        return null;
    }

    @Override
    public byte[] getBytes(URI url) throws Exception {
        return new byte[0];
    }

    @Override
    public CompletableFuture<byte[]> getBytesAsync(URI url) throws Exception {
        return null;
    }
}
