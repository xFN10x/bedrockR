package fn10.bedrockr.addons.addon.jsonClasses.BP;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Biome {

    public static class minecraftBiome {
        public static class description {
            public String identifier;
        }

        public description description;
        public Map<String, Object> components;
    }

    public String format_version;

    @SerializedName("minecraft:biome")
    public minecraftBiome biome;
}
