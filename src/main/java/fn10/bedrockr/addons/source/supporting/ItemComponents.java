package fn10.bedrockr.addons.source.supporting;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.RMapElement.MapValueFilter;
import fn10.bedrockr.addons.source.interfaces.RMapElementProvider;

/**
 * https://wiki.bedrock.dev/items/item-components
 */
public class ItemComponents implements RMapElementProvider {

    public static class minecraftIcon {
        public String texture;

        public minecraftIcon(String tex) {
            this.texture = tex;
        }
    }

    public RMapElement[] getPickable() {
        return new RMapElement[] {
                new RMapElement("Allow Off Hand", "minecraft:allow_off_hand", Boolean.class,
                        "Determines if this item can go in the Off-Hand slot."),
                new RMapElement("Can Break Blocks in Creative", "minecraft:can_destroy_in_creative", Boolean.class,
                        "Determines if this item breaks blocks in creative, like a sword."),
                new RMapElement("Weapon Damage", "minecraft:damage", Integer.class,
                        "The damage this item does. It will say the amount of damage it does on the tooltip.",
                        MapValueFilter.NotNegitive),
                new RMapElement("Enchanted Glint", "minecraft:glint", Boolean.class,
                        "Specifies if the item looks like its enchanted"),
                new RMapElement("Tool Animations", "minecraft:hand_equipped", Boolean.class,
                        "Determines if the item is held like a tool"),
                new RMapElement("Name Colour", "minecraft:hover_text_color", String.class,
                        "Determines the color of the item name when hovering over it."),
                new RMapElement("Interact Text", "minecraft:interact_button", String.class,
                        "The text that is shown when holding the item with controller tips on. \nDefault: \"Use Item\""),
                // new RMapElement("minecraft:liquid_clipped", Boolean.class, "This component
                // was not tested. Use at your own risk"),
                new RMapElement("Max Stack Size", "minecraft:max_stack_size", Integer.class,
                        "How many of this item can be held in an inventory slot", MapValueFilter.NotNegitive),
                // new RMapElement("minecraft:rarity", String.class, "This component was not
                // tested. Use at your own risk"),
                new RMapElement("Item Despawning", "minecraft:should_despawn", Boolean.class,
                        "Determines if this item despawns on the ground after 5 minutes."),
                // new RMapElement("minecraft:stacked_by_data", Boolean.class, "This component
                // was not tested. Use at your own risk"),
                // new RMapElement("minecraft:storage_weight_limit", Integer.class, "This
                // component was not tested. Use at your own risk"),
                // new RMapElement("minecraft:storage_weight_modifier", Integer.class, "This
                // component was not tested. Use at your own risk"),
                // new RMapElement("minecraft:use_animation", String.class, "This component was
                // not tested. Use at your own risk")
        };
    }
}
