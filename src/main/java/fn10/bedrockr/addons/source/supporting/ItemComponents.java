package fn10.bedrockr.addons.source.supporting;

import com.google.gson.annotations.SerializedName;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.RMapElement.MapValueFilter;
import fn10.bedrockr.addons.RStringDropdownMapElement;
import fn10.bedrockr.addons.source.interfaces.RMapElementProvider;

/**
 * https://wiki.bedrock.dev/items/item-components
 */
public class ItemComponents implements RMapElementProvider {

    public static class minecraftDamage {
        @SerializedName("minecraft:damage")
        public int damage;
    }

    public static class minecraftDestructibleByMining {
        public float seconds_to_destroy;
    }

    public static class minecraftBlockPlacer {
        /**
         * The block that this item places
         */
        public String block;
    }

    /*
     * TODO: add;
     * https://wiki.bedrock.dev/items/item-components#bundle-interaction
     * https://wiki.bedrock.dev/items/item-components#compostable
     * https://wiki.bedrock.dev/items/item-components#cooldown
     * https://wiki.bedrock.dev/items/item-components#damage-absorption
     * (seperate element) https://wiki.bedrock.dev/items/item-components#digger &
     * https://wiki.bedrock.dev/items/item-components#durability &
     * https://wiki.bedrock.dev/items/item-components#durability-sensor
     * https://wiki.bedrock.dev/items/item-components#dyeable
     * (seperate) https://wiki.bedrock.dev/items/item-components#enchantable
     * https://wiki.bedrock.dev/items/item-components#entity-placer
     * https://wiki.bedrock.dev/items/item-components#fire-resistant
     * (seperate) https://wiki.bedrock.dev/items/item-components#food &
     * https://wiki.bedrock.dev/items/custom-food
     * https://wiki.bedrock.dev/items/item-components#fuel
     * https://wiki.bedrock.dev/items/item-components#repairable
     * (seperate) https://wiki.bedrock.dev/items/item-components#shooter
     * https://wiki.bedrock.dev/items/item-components#storage-weight-modifier
     * https://wiki.bedrock.dev/items/item-components#swing-duration
     */
    public RMapElement[] getPickable() {
        return new RMapElement[] {
                new RMapElement("Allow Off Hand", "minecraft:allow_off_hand", Boolean.class,
                        "Determines if this item can go in the Off-Hand slot."),
                new RMapElement("Can Break Blocks in Creative", "minecraft:can_destroy_in_creative",
                        Boolean.class,
                        "Determines if this item breaks blocks in creative, like a sword."),
                new RMapElement("Weapon Damage", "minecraft:damage", Integer.class,
                        "The damage this item does. It will say the amount of damage it does on the tooltip.",
                        MapValueFilter.NotNegitive),
                new RMapElement("Enchanted Glint", "minecraft:glint", Boolean.class,
                        "Specifies if the item looks like its enchanted"),
                new RMapElement("Tool Animations", "minecraft:hand_equipped", Boolean.class,
                        "Determines if the item is held like a tool"),
                new RStringDropdownMapElement("Name Colour", "minecraft:hover_text_color",
                        "Determines the color of the item name when hovering over it.", "black",
                        "dark_blue", "dark_green", "dark_aqua", "dark_red",
                        "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua",
                        "red",
                        "light_purple", "yellow", "white", "minecoin_gold", "material_quartz",
                        "material_iron", "material_netherite", "material_redstone",
                        "material_copper",
                        "material_gold", "material_emerald", "material_diamond",
                        "material_lapis",
                        "material_amethyst", "material_resin"),
                new RMapElement("Interact Text", "minecraft:interact_button", String.class,
                        "The text that is shown when holding the item with controller tips on. \nDefault: \"Use Item\""),
                new RMapElement("Max Stack Size", "minecraft:max_stack_size", Integer.class,
                        "How many of this item can be held in an inventory slot",
                        MapValueFilter.NotNegitive),
                new RMapElement("Item Despawning", "minecraft:should_despawn", Boolean.class,
                        "Determines if this item despawns on the ground after 5 minutes."),
                new RStringDropdownMapElement("Rarity", "minecraft:rarity",
                        "Basicly \"Name Colour\", but the colour changes if its enchanted ((un)common -> rare, rare -> epic)\n\nThis is overwritten if Name Colour is specified.",
                        "common", "uncommon", "rare", "epic"),
                new RMapElement("Block Placer", "minecraft:block_placer", minecraftBlockPlacer.class,
                        "Specifes if this item places a block when used on another. Like a bucket; but its infinite use."),
        };
    }
}
