package fn10.bedrockr.addons.addon.jsonClasses.BP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Recipe {

    public transient static final Item NULL_ITEM = new Item(
            "you_should_never_see_this:report-a-github-bug-if-you-see-this");

    // classes
    public static class InnerDiscription {
        public String identifier;

        public InnerDiscription(String id) {
            identifier = id;
        }
    }

    public static class UnlockCondition {
        public String item;
        public Integer data;

        public UnlockCondition() {
        }

        public UnlockCondition(String item) {
            this(item, null);
        }

        public UnlockCondition(String item, Integer data) {
            this.item = item;
            if (data != null)
                this.data = data;
        }

        public static UnlockCondition fromRecipeItem(Item item) {
            return new UnlockCondition(item.item);
        }

        public static List<UnlockCondition> fromRecipeItem(Collection<? extends Item> item) {
            ArrayList<UnlockCondition> list = new ArrayList<UnlockCondition>();
            for (Item info : item) {
                list.add(fromRecipeItem(info));
            }
            return list;
        }
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
        public Integer priority;
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
        public Integer data = null;
        public Integer count = null;

        public Item(String itemFullname, int data, int count) {
            this.item = itemFullname;
            this.data = data;
            this.count = count;
        }

        public Item(String itemFullname, int count) {
            this.item = itemFullname;
            this.count = count;
        }

        public Item(String itemFullname) {
            this.item = itemFullname;
        }

        public boolean equals(Item other) {
            return item.equals(other.item);
        }
    }

    public static class RecipeShapeless extends CraftingRecipeType {
        public Item[] ingredients;
        public Item result;
    }

    public static class RecipeShaped extends CraftingRecipeType {
        public Map<String, String> key;
        public String[] pattern;
        public Item[] result;
    }

    // actual values

    @SerializedName("format_version")
    public String formatVersion;

    @SerializedName("minecraft:recipe_shapeless")
    public RecipeShapeless mcRecipeShapeless;

    @SerializedName("minecraft:recipe_shaped")
    public RecipeShaped mcRecipeShaped;
}
