package fn10.bedrockr.addons.addon.jsonClasses.BP;

import com.google.gson.annotations.SerializedName;

public class Recipe {
    

    // classes
    /**
     * used for extending, because everything in here is shared
     */
    protected class RecipeType {
        private class InnerDiscription {
            public String identifier;
        }

        private class UnlockCondition {
            public String item;
            public int data;
        }

        public InnerDiscription description;

        public String[] tags = new String[] { "crafting_table" }; // TODO: for now, recipes are only for crafting tables

        public UnlockCondition[] unlock;
    }

    protected class CraftingRecipeType extends RecipeType {
        public String group;
        public int priority;
    }

    protected class Item {
        public String item;
        public int data;
        public int count;
    }

    protected class RecipeShapeless extends CraftingRecipeType {
        public Item[] ingredients;
        public Item result;
    }

    // actuall values

    @SerializedName("format_version")
    public String formatVersion;

    @SerializedName("minecraft:recipe_shapeless")
    public RecipeShapeless mcRecipeShapeless;
}
