package fn10.bedrockr.addons.source.elementFiles;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.supporting.BiomeComponents;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.MapFieldSelectables;
import fn10.bedrockr.utils.RAnnotation.StringDropdownField;
import fn10.bedrockr.utils.RAnnotation.VeryImportant;
import fn10.bedrockr.windows.RBlockSelector.DataPathsJson;
import fn10.bedrockr.windows.RNewAddon;

public class BiomeFile implements ElementFile<SourceBiomeElement> {
    private static transient String[] vanillaBiomeNames;

    public static void getVanillaBiomeNames() {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest dataPathsReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
            HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

            HashMap<String, String> versionPaths = gson.fromJson(dataPathsRes.body(), DataPathsJson.class).bedrock
                    .get(RNewAddon.PICKABLE_VERSIONS[0]);

            String path = versionPaths.get("biomes");

            HttpRequest biomesJsonReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                                    + path
                                    + "/blocks.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);

        }
    }

    @CantEditAfter
    @VeryImportant
    public String ElementName;

    public boolean Draft = false;

    @StringDropdownField({"vanilla biomes"})
    public String BiomeID;

    @MapFieldSelectables(BiomeComponents.class)
    public Map<String, Object> Comps = new HashMap<String, Object>();

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'build'");
    }

    @Override
    public Class<SourceBiomeElement> getSourceClass() {
        return SourceBiomeElement.class;
    }

    @Override
    public String getElementName() {
        return ElementName;
    }

    @Override
    public void setDraft(Boolean draft) {
        this.Draft = draft;
    }

    @Override
    public Boolean getDraft() {
        return Draft;
    }

}
