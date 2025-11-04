package fn10.bedrockr.addons.source.elementFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.Item;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.UnlockCondition;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;

public class RecipeFile implements ElementFile {

    public List<String> testList = new ArrayList<String>();
    public String[] testArray = new String[] {};
    @CantEditAfter
    @HelpMessage("The name of the element. This only shows up in the workspace view.")
    public String ElementName;

    @HelpMessage("The ID of the recipe. Only used internally, and for debugging.")
    public String RecipeID;

    @HelpMessage("The group that the recipe is in. This field doesn't have any known effects.")
    public String RecipeGroup;

    public List<UnlockCondition> UnlockConditions = new ArrayList<UnlockCondition>();

    public Item Result;
    /**
     * Only used in Shaped Recipes
     */
    public List<Item> ExtraResults;

    public List<Item> ShapelessIngredients;

    public Vector<Item> ShapedPattern = new Vector<>(9);

    private boolean Draft;


    @Override
    public Class<? extends ElementSource> getSourceClass() {
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
    public void build(String rootPath, WPFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
           
    }
}
