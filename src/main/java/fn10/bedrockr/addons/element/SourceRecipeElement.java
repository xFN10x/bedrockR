package fn10.bedrockr.addons.element;

import java.io.IOException;
import fn10.bedrockr.addons.element.elementFiles.RecipeFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

public class SourceRecipeElement extends ElementSource<RecipeFile> {

    public SourceRecipeElement(@NonNull RecipeFile serialized) {
        super(serialized);
    }

    public SourceRecipeElement() {
        super(new RecipeFile());
    }

    public enum RecipeType {
        Shaped,
        Shapeless,
    }

    public static ElementDetails getDetails() throws IOException {
        return new ElementDetails("Recipe",
                // ------------------------------------------------| new line there
                "<html>A Crafting table recipe that you <br>edit with a visual guide.</html>",
                RFileOperations.readAllBytes(ElementSource.class.getResource("/addons/element/Recipe.png").openStream()));
    }

    @Override
    public String getFileExtension() {
        return ".reciperef";
    }

    @Override
    public Class<RecipeFile> getSerilizedClass() {
        return RecipeFile.class;
    }
}
