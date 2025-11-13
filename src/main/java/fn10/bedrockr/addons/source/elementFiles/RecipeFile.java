package fn10.bedrockr.addons.source.elementFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.InnerDiscription;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.Item;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.RecipeShaped;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.RecipeShapeless;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.UnlockCondition;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.SourceRecipeElement.RecipeType;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RAnnotation.VeryImportant;

/**
 * use as referance
 * https://wiki.bedrock.dev/loot/recipes
 */
public class RecipeFile implements ElementFile<SourceRecipeElement> {

    @CantEditAfter
    @VeryImportant
    @HelpMessage("The name of the element. This only shows up in the workspace view.")
    public String ElementName;

    @HelpMessage("The ID of the recipe. Only used internally, and for debugging.")
    public String RecipeID;

    @HelpMessage("The group that the recipe is in. This field doesn't have any known effects.")
    public String RecipeGroup;

    public List<UnlockCondition> UnlockConditions = new ArrayList<UnlockCondition>();

    public Item Result;

    public RecipeType recipeType = RecipeType.Shaped;

    /**
     * Only used in Shaped Recipes
     */
    public List<Item> ExtraResults = new ArrayList<Item>();

    public List<Item> ShapelessIngredients = new ArrayList<Item>();

    public String[] ShapedPattern;
    public Map<String, String> ShapedKey = new HashMap<String, String>();

    private boolean Draft;

    @Override
    public Class<SourceRecipeElement> getSourceClass() {
        return SourceRecipeElement.class;
    }

    @Override
    public String getElementName() {
        return ElementName;
    }

    @Override
    public void setDraft(Boolean draft) {
        Draft = draft;
    }

    @Override
    public Boolean getDraft() {
        return Draft;
    }

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        Recipe recipe = new Recipe();
        recipe.formatVersion = "1.21.50";
        switch (recipeType) {
            case RecipeType.Shapeless:
                RecipeShapeless shapeless = new RecipeShapeless();
                shapeless.description = new InnerDiscription(workspaceFile.Prefix + ":" + RecipeID);
                shapeless.unlock = UnlockConditions.toArray(new UnlockCondition[0]);
                shapeless.result = Result;
                shapeless.ingredients = ShapelessIngredients.toArray(new Item[0]);
                recipe.mcRecipeShapeless = shapeless;

                break;

            case RecipeType.Shaped:
                RecipeShaped shaped = new RecipeShaped();
                shaped.description = new InnerDiscription(workspaceFile.Prefix + ":" + RecipeID);
                shaped.key = ShapedKey;
                shaped.pattern = ShapedPattern;
                shaped.unlock = UnlockConditions.toArray(new UnlockCondition[0]);
                List<Item> fullResults = new ArrayList<Item>();
                fullResults.add(Result);
                if (!ExtraResults.isEmpty())
                    fullResults.addAll(ExtraResults);
                shaped.result = fullResults.toArray(new Item[0]);
                recipe.mcRecipeShaped = shaped;
                break;
        }

        // build file
        String json = gson.toJson(recipe);
        Path path = Path.of(rootPath, "recipes", RecipeID + ".json");
        FileUtils.createParentDirectories(path.toFile());
        Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }
}
