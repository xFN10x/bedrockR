package fn10.bedrockr.addons.source.items;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.RMapElement.MapValueFilter;
import fn10.bedrockr.addons.source.interfaces.RMapElementProvider;

public class ItemComponents implements RMapElementProvider {

    public static class minecraftIcon {
        public String texture;

        public minecraftIcon(String tex) {
            this.texture = tex;
        }
    }

   /*  private static RMapElement[] Components = {
            new RMapElement("minecraft:allow_off_hand", Boolean.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:can_destroy_in_creative", Boolean.class, "This component was not tested. Use at your own risk"),
            new RMapElement("Weapon Damage", "minecraft:damage", Integer.class),
            new RMapElement("Enchanted Glint", "minecraft:glint", Boolean.class,"Specifies if the item looks like its enchanted"),
            new RMapElement("minecraft:hand_equipped", Boolean.class, "This component was not tested. Use at your own risk"),
            new RMapElement("Text Colour", "minecraft:hover_text_color", String.class,"The colour of the item name"),
            new RMapElement("minecraft:interact_button", String.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:liquid_clipped", Boolean.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:max_stack_size", Integer.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:rarity", String.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:should_despawn", Boolean.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:stacked_by_data", Boolean.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:storage_weight_limit", Integer.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:storage_weight_modifier", Integer.class, "This component was not tested. Use at your own risk"),
            new RMapElement("minecraft:use_animation", String.class, "This component was not tested. Use at your own risk")
    };

    private static Map<String, Type> AvailableComponents = new HashMap<>();
    static {
        AvailableComponents.put("minecraft:allow_off_hand", Boolean.class);
        AvailableComponents.put("minecraft:can_destroy_in_creative", Boolean.class);
        AvailableComponents.put("minecraft:damage", Integer.class);
        AvailableComponents.put("minecraft:glint", Boolean.class);
        AvailableComponents.put("minecraft:hand_equipped", Boolean.class);
        AvailableComponents.put("minecraft:hover_text_color", String.class);
        AvailableComponents.put("minecraft:interact_button", String.class);
        AvailableComponents.put("minecraft:liquid_clipped", Boolean.class);
        AvailableComponents.put("minecraft:max_stack_size", Integer.class);
        AvailableComponents.put("minecraft:rarity", String.class);
        AvailableComponents.put("minecraft:should_despawn", Boolean.class);
        AvailableComponents.put("minecraft:stacked_by_data", Boolean.class);
        AvailableComponents.put("minecraft:storage_weight_limit", Integer.class);
        AvailableComponents.put("minecraft:storage_weight_modifier", Integer.class);
        AvailableComponents.put("minecraft:use_animation", String.class);

        // special
        AvailableComponents.put("minecraft:use_modifiers", null); // add class
        AvailableComponents.put("minecraft:wearable", null); // add class
        AvailableComponents.put("minecraft:tags", null); // add class
        AvailableComponents.put("minecraft:throwable", null); // add class
        AvailableComponents.put("minecraft:storage_item", null); // add class
        AvailableComponents.put("minecraft:record", null); // add class
        AvailableComponents.put("minecraft:repairable", null); // add class
        AvailableComponents.put("minecraft:shooter", null); // add class
        AvailableComponents.put("minecraft:projectile", null);// add class
        AvailableComponents.put("minecraft:icon", null); // add class
        AvailableComponents.put("minecraft:damage_absorption", null); // add class
        AvailableComponents.put("minecraft:digger", null); // add class
        AvailableComponents.put("minecraft:display_name", null); // add class
        AvailableComponents.put("minecraft:durability", null); // add class
        AvailableComponents.put("minecraft:durability_sensor", null); // add class
        AvailableComponents.put("minecraft:block_placer", null); // add class
        AvailableComponents.put("minecraft:bundle_interaction", null); // add class
        AvailableComponents.put("minecraft:compostable", null); // add class
        AvailableComponents.put("minecraft:cooldown", null); // add class
        AvailableComponents.put("minecraft:dyeable", null); // add class
        AvailableComponents.put("minecraft:enchantable", null); // add class
        AvailableComponents.put("minecraft:entity_placer", null); // add class
        AvailableComponents.put("minecraft:food", null); // add class
        AvailableComponents.put("minecraft:fuel", null); // minecraft, WHY DO YOU NEED OBJECTS FOR THESEEEEE

    } */

    public static RMapElement[] getPickable() {
        return new RMapElement[] {
            new RMapElement("Allow Off Hand","minecraft:allow_off_hand", Boolean.class, "Determines if this item can go in the Off-Hand slot."),
            new RMapElement("Can Break Blocks in Creative","minecraft:can_destroy_in_creative", Boolean.class, "Determines if this item breaks blocks in creative, like a sword."),
            new RMapElement("Weapon Damage", "minecraft:damage", Integer.class, "The damage this item does. It will say the amount of damage it does on the tooltip.",MapValueFilter.NotNegitive),
            new RMapElement("Enchanted Glint", "minecraft:glint", Boolean.class,"Specifies if the item looks like its enchanted"),
            new RMapElement("Tool Animations","minecraft:hand_equipped", Boolean.class, "Determines if the item is held like a tool"),
            new RMapElement("Name Colour", "minecraft:hover_text_color", String.class,"Determines the color of the item name when hovering over it."),
            new RMapElement("Interact Text","minecraft:interact_button", String.class, "The text that is shown when holding the item with controller tips on. \nDefault: \"Use Item\""),
            //new RMapElement("minecraft:liquid_clipped", Boolean.class, "This component was not tested. Use at your own risk"),
            new RMapElement("Max Stack Size","minecraft:max_stack_size", Integer.class, "How many of this item can be held in an inventory slot",MapValueFilter.NotNegitive),
            //new RMapElement("minecraft:rarity", String.class, "This component was not tested. Use at your own risk"),
            new RMapElement("Item Despawning","minecraft:should_despawn", Boolean.class, "Determines if this item despawns on the ground after 5 minutes."),
            //new RMapElement("minecraft:stacked_by_data", Boolean.class, "This component was not tested. Use at your own risk"),
            //new RMapElement("minecraft:storage_weight_limit", Integer.class, "This component was not tested. Use at your own risk"),
            //new RMapElement("minecraft:storage_weight_modifier", Integer.class, "This component was not tested. Use at your own risk"),
            //new RMapElement("minecraft:use_animation", String.class, "This component was not tested. Use at your own risk")
    };
    }
}
