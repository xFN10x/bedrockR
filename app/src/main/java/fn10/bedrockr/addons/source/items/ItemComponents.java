package fn10.bedrockr.addons.source.items;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ItemComponents {

    public static ItemComponent[] Components = {
            new ItemComponent("minecraft:allow_off_hand", Boolean.class),
            new ItemComponent("minecraft:can_destroy_in_creative", Boolean.class),
            new ItemComponent("minecraft:damage", Integer.class),
            new ItemComponent("minecraft:glint", Boolean.class),
            new ItemComponent("minecraft:hand_equipped", Boolean.class),
            new ItemComponent("minecraft:hover_text_color", String.class),
            new ItemComponent("minecraft:interact_button", String.class),
            new ItemComponent("minecraft:liquid_clipped", Boolean.class),
            new ItemComponent("minecraft:max_stack_size", Integer.class),
            new ItemComponent("minecraft:rarity", String.class),
            new ItemComponent("minecraft:should_despawn", Boolean.class),
            new ItemComponent("minecraft:stacked_by_data", Boolean.class),
            new ItemComponent("minecraft:storage_weight_limit", Integer.class),
            new ItemComponent("minecraft:storage_weight_modifier", Integer.class),
            new ItemComponent("minecraft:use_animation", String.class)
    };

    public static  class ItemComponent {
        public String ID;
        public Type Type;
        public String DisplayName;
        public String HelpDescription;

        public ItemComponent(String ID, Type Type) {
            this.Type = Type;
            this.ID = ID;

            this.DisplayName = ID;
            this.HelpDescription = "No help for this component.";
        }
    }

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

    }

}
