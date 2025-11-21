package fn10.bedrockr.rendering;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;

import fn10.bedrockr.addons.source.interfaces.ElementFile;

/**
 * Downloads the lastest minecraft java jar, and gets the block models, then
 * saves a map to a json
 */
public class BlockGeometryDownloader {

    public static Gson gson = ElementFile.gson;

    public static class VersionManifest {
        static class Latest {
            public String release;
            public String snapshot;

        }
        static class VersionEntry {
            public String id;
            public String type;
            public String url;
            public String time;
            public String releaseTime;
        }

        public Latest latest;
        public VersionEntry[] versions;
    }

    private static HttpClient client = HttpClient.newBuilder().build();

    public static void main(String[] args) {
        try {
            get();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void get() throws IOException, InterruptedException {

        HttpRequest dataPathsReq = HttpRequest.newBuilder()
                .uri(URI.create("https://launchermeta.mojang.com/mc/game/version_manifest.json"))
                .version(HttpClient.Version.HTTP_2).GET().build();
        HttpResponse<String> response = client.send(dataPathsReq, BodyHandlers.ofString());


        //uhh i dont think this will work
    }
}
