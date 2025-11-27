package fn10.bedrockr.addons.source;

import java.awt.Window;
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.SpringLayout;

import com.google.gson.internal.LinkedTreeMap;

import fn10.bedrockr.addons.source.elementFiles.BiomeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RBlockSelector.DataPathsJson;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.RNewAddon;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceBiomeElement implements ElementSource<BiomeFile> {

    private static transient String[] vanillaBiomeNames;

    @SuppressWarnings("unchecked")
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
                                    + "/biomes.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();

            HttpResponse<String> biomesRes = client.send(biomesJsonReq, BodyHandlers.ofString());
            List<String> biomeNames = new ArrayList<String>();
            for (Map<String, Object> biomeEntry : (ArrayList<LinkedTreeMap<String, Object>>) gson
                    .fromJson(biomesRes.body(), List.class)) {
                biomeNames.add(biomeEntry.get("name").toString());
            }
            vanillaBiomeNames = biomeNames.toArray(new String[0]);
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);

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

    public static ElementDetails getDetails() {
        return new ElementDetails("Biome",
                "<html>A biome that replaced a vanilla one<br />partially, or completly.</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Biome.png")));
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
    public File buildJSONFile(Window doingThis, String workspace) {
        try {
            return Files.writeString(RFileOperations.getFileFromWorkspace(doingThis, workspace,
                    "elements" + File.separator + serilized.getElementName() + ".biomeref").toPath(), getJSONString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE).toFile();
        } catch (IOException e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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

    @SuppressWarnings("unchecked")
    @Override
    public RElementEditingScreen getBuilderWindow(Window Parent, ElementCreationListener parent2, String Workspace) {
        RElementEditingScreen screen = new RElementEditingScreen(Parent, getDetails().Name, this, getSerilizedClass(),
                parent2);
        SpringLayout lay = new SpringLayout();
        screen.InnerPane.setLayout(lay);

        RElementValue elementnameVal = new RElementValue(screen, String.class,
                new FieldFilters.FileNameLikeStringFilter(),
                "ElementName", "Element Name", false, getSerilizedClass(), serilized, Workspace);
        RElementValue idVal = new RElementValue(screen, String.class, new FieldFilters.IDStringFilter(),
                "BiomeID", "Biome ID", false, getSerilizedClass(), serilized, Workspace);
        RElementValue compsVal = new RElementValue(screen, HashMap.class, new FieldFilters.IDStringFilter(),
                "Comps", "Biome Components", false, getSerilizedClass(), serilized, Workspace);
        if (idVal.Input instanceof JComboBox input) {
            input.setModel(new DefaultComboBoxModel<String>(vanillaBiomeNames));
        }
        screen.addField(elementnameVal);
        screen.addField(idVal);
        screen.addField(compsVal);

        lay.putConstraint(SpringLayout.WEST, elementnameVal, 5, SpringLayout.WEST, screen.InnerPane);
        lay.putConstraint(SpringLayout.NORTH, elementnameVal, 5, SpringLayout.NORTH, screen.InnerPane);

        lay.putConstraint(SpringLayout.NORTH, idVal, 0, SpringLayout.NORTH, elementnameVal);
        lay.putConstraint(SpringLayout.EAST, idVal, -5, SpringLayout.EAST, screen.InnerPane);

        lay.putConstraint(SpringLayout.EAST, compsVal, -5, SpringLayout.EAST, screen.InnerPane);
        lay.putConstraint(SpringLayout.WEST, compsVal, 5, SpringLayout.WEST, screen.InnerPane);
        lay.putConstraint(SpringLayout.SOUTH, compsVal, -5, SpringLayout.SOUTH, screen.InnerPane);
        lay.putConstraint(SpringLayout.NORTH, compsVal, 5, SpringLayout.SOUTH, elementnameVal);

        return screen;
    }

}
