package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

import fn10.bedrockr.addons.source.elementFiles.BiomeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

import static fn10.bedrockr.utils.RFileOperations.gson;

public class SourceBiomeElement extends ElementSource<BiomeFile> {

    public static String[] vanillaBiomeNames = null;
    public static String[] prefixedVanillaBiomeNames = null;

    public static String[] getPrefixedVanillaBiomeNames() {
        if (prefixedVanillaBiomeNames == null)
            getVanillaBiomeNames();
        return prefixedVanillaBiomeNames;
    }

    @SuppressWarnings("unchecked")
    public static String[] getVanillaBiomeNames() {
        if (vanillaBiomeNames != null) {
            return vanillaBiomeNames;
        } else
            try {
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest dataPathsReq = HttpRequest.newBuilder()
                        .uri(new URI(
                                "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                        .version(HttpClient.Version.HTTP_2).GET().build();
                HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

                Map<String, String> versionPaths = ((Map<String,Map<String,String>>)gson.fromJson(dataPathsRes.body(), Map.class).get("bedrock"))
                        .get(RFileOperations.PICKABLE_VERSIONS[0]);

                String path = versionPaths.get("biomes");

                HttpRequest biomesJsonReq = HttpRequest.newBuilder()
                        .uri(new URI(
                                "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                                        + path
                                        + "/biomes.json"))
                        .version(HttpClient.Version.HTTP_2).GET().build();

                HttpResponse<String> biomesRes = client.send(biomesJsonReq, BodyHandlers.ofString());
                List<String> biomeNames = new ArrayList<String>();
                List<String> biomeNamesPrefix = new ArrayList<String>();
                for (Map<String, Object> biomeEntry : (ArrayList<LinkedTreeMap<String, Object>>) gson
                        .fromJson(biomesRes.body(), List.class)) {
                    biomeNames.add(biomeEntry.get("name").toString());
                    biomeNamesPrefix.add("minecraft:" + biomeEntry.get("name").toString());
                }
                vanillaBiomeNames = biomeNames.toArray(new String[0]);
                prefixedVanillaBiomeNames = biomeNamesPrefix.toArray(vanillaBiomeNames);
                return vanillaBiomeNames;
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return new String[0];
            }
    }


    public SourceBiomeElement(BiomeFile obj) {
        super(obj);
        getVanillaBiomeNames();
    }

    @Override
    public String getFileExtension() {
        return ".biomeref";
    }

    public SourceBiomeElement() {
        this(new BiomeFile());
    }

    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Biome",
                "<html>A biome that replaced a vanilla one<br />partially, or completly.</html>",
                RFileOperations.readAllBytes(ElementSource.class.getResource("/addons/element/Biome.png").openStream()));
    }

    @Override
    public Class<BiomeFile> getSerilizedClass() {
        return BiomeFile.class;
    }
}
