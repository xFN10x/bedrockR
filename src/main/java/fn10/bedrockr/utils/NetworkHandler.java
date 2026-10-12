package fn10.bedrockr.utils;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public abstract class NetworkHandler {
    
    public void setup() {}
    
    ///Perform an HTTP GET request and return the body as JSON.
    public abstract String getJSON(URI url) throws Exception;
    public abstract CompletableFuture<String> getJSONAsync(URI url) throws Exception;
    public abstract byte[] getBytes(URI url) throws Exception;
    public abstract CompletableFuture<byte[]> getBytesAsync(URI url) throws Exception;
    
    ///Returns whether this version of bedrockR is the latest (stable) release.
    public abstract boolean upToDate();
}
