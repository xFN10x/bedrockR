package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import fn10.bedrockr.addons.source.elementFiles.RecipeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceRecipeElement implements ElementSource<RecipeFile> {

    public static enum RecipeType {
        Shaped,
        Shapeless,
    }

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
    public RecipeFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, RecipeFile.class);
    }

    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Recipe",

                // ------------------------------------------------| new line there
                "<html>A Crafting table recipe that you <br>edit with a visual guide.</html>",
                ElementSource.class.getResource("/addons/element/Recipe.png").openStream().readAllBytes());
    }

    @Override
    public File buildJSONFile(String workspace) {
        String string = getJSONString();
        File file = RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".reciperef");
        try {
            Files.write(file.toPath(), string.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return file;
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public Class<RecipeFile> getSerilizedClass() {
        return RecipeFile.class;
    }

    @Override
    public RecipeFile getSerilized() {
        return serilized;
    }
}
