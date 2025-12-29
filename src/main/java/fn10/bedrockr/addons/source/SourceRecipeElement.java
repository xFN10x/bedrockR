package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;
import fn10.bedrockr.addons.source.elementFiles.RecipeFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceRecipeElement extends ElementSource<RecipeFile> {

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
    public Class<RecipeFile> getSerilizedClass() {
        return RecipeFile.class;
    }

    @Override
    public RecipeFile getSerilized() {
        return serilized;
    }

    @Override
    public File getLocation(String workspace) {
        return RFileOperations.getFileFromWorkspace(workspace,
                Location + serilized.ElementName + ".reciperef");
    }
}
