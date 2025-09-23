package fn10.bedrockr.addons.addon.jsonClasses.BP;

import java.util.Vector;

import com.google.gson.annotations.SerializedName;

//translated from https://learn.microsoft.com/en-us/minecraft/creator/reference/content/addonsreference/packmanifest?view=minecraft-bedrock-stable
public class Manifest {

    // inner classes
    public static enum ModuleTypes {
        RESOURCES("resources"),
        DATA("data"),
        WORLDTEMPLATE("world_template"),
        SCRIPT("script");

        private final String text;

        ModuleTypes(final String text) {
            this.text = text;
        }

        /**
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public static  class Header {

        /**
         * only for word templates!
         */
        
        public transient boolean allow_random_seed; // only for word templates!
        /**
         * only for word templates!
         */
        public transient Vector<Integer> base_game_version; // only for word templates!
        public String description;
        /**
         * only for word templates!
         */
        public transient boolean lock_template_options; 
        public Vector<Integer> min_engine_version;
        public String name;
        public String pack_scope;
        public String uuid;
        public Vector<Integer> version;
    }

    public static class Module {
        public String description;
        public String type;
        public String uuid;
        public Vector<Integer> version;
        /**
         * add this only if this is a <code>"script"</code>
         * and if it is, only accepted value is <code>"javascript"</code>
         */
        public String language; // only if type is "script", if it is, only value is "javascript"
        public String entry;
    }
    public static class Dependence {
        public String version;
        public String module_name;
    }
    public static class Capability { //i dont think this will be used at all.
        //WARNING: IM GUESSING ON WHAT THE TYPES ARE! HELP
        public boolean chemistry;
        public boolean editorExtension;
        public boolean experimental_custom_ui;
        public boolean raytraced;
    }
    public static class Metadata {
        //even INNER class
        public class GeneratedWithR {
            public String[] bedrockR;
        }

        public String[] authors;
        public String license;
        public String url;
        public GeneratedWithR generated_with;
    }


    @SerializedName("format_version")
    public int formatVersion;
    public Header header;
    public Module[] modules;
    public Capability[] capabilities;
    public Dependence[] dependencies;
    public Metadata metadata;
}
