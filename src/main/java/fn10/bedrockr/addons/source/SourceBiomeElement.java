package fn10.bedrockr.addons.source;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.ImageIcon;

import fn10.bedrockr.addons.source.elementFiles.BiomeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceBiomeElement implements ElementSource<BiomeFile> {

    private BiomeFile serilized = new BiomeFile();

    public static ElementDetails getDetails() {
        return new ElementDetails("Biome", "A biome that replaced a vanilla one\npartially, or completly.",
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

    @Override
    public RElementEditingScreen getBuilderWindow(Window Parent, ElementCreationListener parent2, String Workspace) {
        RElementEditingScreen screen = new RElementEditingScreen(Parent, getDetails().Name, this, getSerilizedClass(),
                parent2);

        screen.addField(new RElementValue(screen, String.class, new FieldFilters.FileNameLikeStringFilter(),
                "ElementName", "Element Name", false, getClass(), serilized, Workspace));

        return screen;
    }

}
