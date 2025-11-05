package fn10.bedrockr.addons.source;

import java.awt.Frame;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.ImageIcon;

import fn10.bedrockr.addons.source.elementFiles.RecipeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.componets.RCraftingGridValue;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class SourceRecipeElement implements ElementSource {

    private final String Location = File.separator + "elements" + File.separator;
    private RecipeFile serilized;

    public SourceRecipeElement(RecipeFile obj) {
        this.serilized = obj;
    }

    public SourceRecipeElement() {
        this.serilized = null;
    }

    public SourceRecipeElement(String jsonString) {
        this.serilized = (RecipeFile) getFromJSON(jsonString);
    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public ElementFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, RecipeFile.class);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Recipe",

                // ------------------------------------------------| new line there
                "<html>A Crafting table recipe that you <br>edit with a visual guide.</html>",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Recipe.png")));
    }

    @Override
    public File buildJSONFile(Frame doingThis, String workspace) {
        String string = getJSONString();
        File file = RFileOperations.getFileFromWorkspace(doingThis, workspace,
                Location + serilized.ElementName + ".reciperef");
        try {
            Files.writeString(file.toPath(), string, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<?> getSerilizedClass() {
        return RecipeFile.class;
    }

    @Override
    public ElementFile getSerilized() {
        return serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(Frame Parent, ElementCreationListener parent2, String Workspace) {
        var frame = new RElementEditingScreen(Parent, "Item", this, getSerilizedClass(), parent2,
                RElementEditingScreen.DEFAULT_STYLE);

        frame.InnerPane.add(new RCraftingGridValue(Workspace));

        return frame;
    }
}
