package fn10.bedrockr.addons.addon.jsonClasses.BP;

/**
 * TODO: finish this in a2.1
 * 
 * taken from https://learn.microsoft.com/en-us/minecraft/creator/reference/content/featuresreference/examples/features/minecraftstructure_template_feature?view=minecraft-bedrock-stable
 */
public class Feature {
    public class StructureTemplateFeature {
        public class Description {
            /**
             * The name of this feature in the form 'namespace_name:feature_name'. 'feature_name' must match the filename.
             */
            public String identifier;
        }

        public Description description;
        /**
         * Reference to the structure to be placed.
         */
        public String structure_name;
        /**
         * 0-16
         * 
         * 
         * How far the structure is allowed to move when searching for a valid placement position. Search is radial, stopping when the nearest valid position is found. Defaults to 0 if omitted.
         */
        public int adjustment_radius;

    }

    
    public String format_version;
}
