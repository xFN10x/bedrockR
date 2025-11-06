package fn10.bedrockr.addons.addon.jsonClasses.BP;

import com.google.gson.annotations.SerializedName;

public class Recipe {

    public transient static final Item NULL_ITEM = new Item(
            "you_should_never_see_this:report-a-github-bug-if-you-see-this");

    // classes
    public static class InnerDiscription {
        public String identifier;
    }

    public static class UnlockCondition {
        public String item;
        public int data;
    }

    /**
     * used for extending, because everything in here is shared
     */
    public static class RecipeType {

        public InnerDiscription description;

        public String[] tags = new String[] { "crafting_table" }; // TODO: for now, recipes are only for crafting tables

        public UnlockCondition[] unlock;
    }

    public static class CraftingRecipeType extends RecipeType {
        public String group;
        public int priority;
    }

    /**
     * An item referance for recipes.
     * 
     * {@code count} isn't used in the ingrediants of a shaped recipe, and in
     * shapeless, its used to signifiy that it is in the grid multiple times in
     * different places. Not one stack
     */
    public static class Item {
        public String item;
        public int data;
        public int count;

        public Item(String name, int data, int count) {
            this.item = name;
            this.data = data;
            this.count = count;
        }

        public Item(String name, int count) {
            this(name, 0, count);
        }

        public Item(String name) {
            this(name, 0);
        }

        public boolean equals(Item other) {
            return item.equals(other.item);
        }
    }

    public static class RecipeShapeless extends CraftingRecipeType {
        public Item[] ingredients;
        public Item result;
    }

    // actuall values

    @SerializedName("format_version")
    public String formatVersion;

    @SerializedName("minecraft:recipe_shapeless")
    public RecipeShapeless mcRecipeShapeless;
}
