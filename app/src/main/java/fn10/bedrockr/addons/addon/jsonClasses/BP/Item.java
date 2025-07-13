package fn10.bedrockr.addons.addon.jsonClasses.BP;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Item {
    public static class InnerItem {
        public static class Description {
            public static class MenuCategory {
                public String group;
                public String category;
                @SerializedName("is_hidden_in_commands")
                public boolean hidden;
            }

            public String identifier;
            public MenuCategory menu_category;
        }

        public Description description;
        public Map<String, Object> components;
    }

    public String format_version;
    @SerializedName("minecraft:item")
    public InnerItem body;

}
