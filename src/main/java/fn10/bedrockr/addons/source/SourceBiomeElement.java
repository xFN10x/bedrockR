package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

import fn10.bedrockr.addons.source.elementFiles.BiomeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceBiomeElement implements ElementSource<BiomeFile> {

    private static transient String[] vanillaBiomeNames = null;
    private static transient String[] prefixedVanillaBiomeNames = null;

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
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return null;
            }
    }

    private BiomeFile serilized = new BiomeFile();

    public SourceBiomeElement(BiomeFile obj) {
        this();
        this.serilized = obj;
    }

    public SourceBiomeElement() {
        getVanillaBiomeNames();
    }

    public SourceBiomeElement(String jsonString) {
        this();
        this.serilized = (BiomeFile) getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Biome",
                "<html>A biome that replaced a vanilla one<br />partially, or completly.</html>",
                ElementSource.class.getResource("/addons/element/Biome.png").openStream().readAllBytes());
    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public BiomeFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, getSerilizedClass());
    }

    @Override
    public File buildJSONFile(String workspace) {
        try {
            return Files.writeString(RFileOperations.getFileFromWorkspace(workspace,
                    "elements" + File.separator + serilized.getElementName() + ".biomeref").toPath(), getJSONString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE).toFile();
        } catch (IOException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public Class<BiomeFile> getSerilizedClass() {
        return BiomeFile.class;
    }

    @Override
    public BiomeFile getSerilized() {
        return serilized;
    }
}
