package fn10.bedrockr.addons.element.elementSources;

import java.io.IOException;
import fn10.bedrockr.addons.element.elementFiles.RecipeFile;
import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

public class SourceRecipeElement extends ElementSource<RecipeFile> {

    @Override
    public ElementDetails getDetails() {
        return new ElementDetails("Recipe", "A crafting table recipe.");
    }

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

    @Override
    public String getFileExtension() {
        return ".reciperef";
    }

    @Override
    public Class<RecipeFile> getSerilizedClass() {
        return RecipeFile.class;
    }
}
