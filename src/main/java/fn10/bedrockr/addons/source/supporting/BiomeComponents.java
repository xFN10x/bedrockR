package fn10.bedrockr.addons.source.supporting;

import java.util.List;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.source.interfaces.RMapElementProvider;

/**
 * thinks like help and javadoc taken from
 * https://learn.microsoft.com/en-us/minecraft/creator/reference/content/biomesreference/examples/componentlist?view=minecraft-bedrock-stable
 */
public class BiomeComponents implements RMapElementProvider {

    public static class Climate {
        /**
         * Amount that precipitation affects colors and block changes. Setting to 0 will
         * stop rain from falling in the biome.
         */
        public float downfall;
        /**
         * Minimum and maximum snow level, each multiple of 0.125 is another snow layer
         * Value must have at least 2 items. Value must have at most 2 items.
         */
        public float[] snow_accumulation;
        /**
         * Temperature affects a variety of visual and behavioral things, including snow
         * and ice placement, sponge drying, and sky color
         */
        public float temperature;
    }

    public static class CreatureSpawnProbablity {
        /**
         * Probabiltity between [0.0, 0.75] of creatures spawning within the biome on
         * chunk generation. Value must be {@code value <= 0.75}.
         */
        public float probability;
    }

    public static class Humidity {
        public boolean is_humid;
    }

    public static class MapTints {
        public static class GrassTint {
            public String tint;
            public String type;
        }

        /**
         * Sets the color foliage will be tinted by in this biome on the map.
         * (hex colour string)
         */
        public String foliage;
        /**
         * Controls whether the grass will use a custom tint color or a noise based tint
         * color. (only colour tint for now)
         */
        public GrassTint grass;
    }

    public static class ReplaceBiomes {
        public static class Replacement {
            /**
             * Noise value used to determine whether or not the replacement is attempted,
             * similar to a percentage. Must be in the range (0.0, 1.0]. Value must be <= 1.
             */
            public float amount;
            /**
             * Dimension in which this replacement can happen. Must be
             * 'minecraft:overworld'.
             */
            public String dimension = "minecraft:overworld";
            /**
             * Scaling value used to alter the frequency of replacement attempts. A lower
             * frequency will mean a bigger contiguous biome area that occurs less often. A
             * higher frequency will mean smaller contiguous biome areas that occur more
             * often. Must be in the range (0.0, 100.0]. Value must be <= 100.
             */
            public float noise_frequency_scale;
            /**
             * Biomes that are going to be replaced by the overriding biome. Target biomes
             * must not contain namespaces (yes they have too microsoft). Value must have at
             * least 1 items.
             */
            public List<String> targets;
        }
        public Replacement[] replacements;
    }

    public static class SurfaceBuilder {
        public static class OverworldBuilder {
            /**
             * Controls the type of surface builder to use (do not change this, it is
             * already set to the value one.)
             */
            public String type = "minecraft:overworld";
            /**
             * Controls how deep below the world water level the floor should occur Value
             * must be <= 127.
             */
            public int sea_floor_depth;
            /** Controls the block type used for the bodies of water in this biome */
            public String sea_material;
            /** Controls the block type used as a floor for bodies of water in this biome */
            public String sea_floor_material;
            /** Controls the block type used deep underground in this biome */
            public String foundation_material;
            /** Controls the block type used in a layer below the surface of this biome */
            public String mid_material;
            /** Controls the block type used for the surface of this biome */
            public String top_material;
        }

        public OverworldBuilder builder;
    }

    public static class Tags {
        public List<String> tags;
    }

    // not adding
    // https://learn.microsoft.com/en-us/minecraft/creator/reference/content/biomesreference/examples/components/minecraftbiomes_mountain_parameters?view=minecraft-bedrock-stable#biome-mountain-parameters-properties,
    // since i cant seem to get it to work

    // not adding
    // https://learn.microsoft.com/en-us/minecraft/creator/reference/content/biomesreference/examples/components/minecraftbiomes_surface_material_adjustments?view=minecraft-bedrock-stable
    // since i dont know what it does

    @Override
    public RMapElement[] getPickable() {
        return new RMapElement[] {
                new RMapElement("Climate", "minecraft:climate", Climate.class,
                        "Describes temperature, humidity,\nprecipitation, and similar.\nBiomes without this component will\nhave default values."),

                new RMapElement("Creature Spawning", "minecraft:creature_spawn_probability",
                        CreatureSpawnProbablity.class,
                        "Probability that creatures will spawn\nwithin the biome when a\nchunk is generated."),

                new RMapElement("Is Humid", "minecraft:humidity", Humidity.class,
                        "Forces a biome to ether always be\nhumid or never humid.\nHumidity effects the spread chance,\nand spread rate of fire in the biome."),

                new RMapElement("Map Foliage Colour", "minecraft:map_tints", MapTints.class,
                        "Sets the color grass and foliage will\nbe tinted by in this biome on the map."),

                new RMapElement("Replace Biome(s)", "minecraft:replace_biomes", ReplaceBiomes.class,
                        "Replaces a specified portion of one\nor more Minecraft biomes."),

                new RMapElement("Biome Surface", "minecraft:surface_builder", SurfaceBuilder.class,
                        "Controls the materials used for\nterrain generation."),

                new RMapElement("Tags", "minecraft:tags", Tags.class,
                        """
                                Attach arbitrary string tags to this\nbiome. Most biome tags are referenced\nby JSON settings, but some meanings of tags are directly implemented in the game's code.

                                These tags are listed here:

                                birch:
                                Biome uses wildflowers (mutually exclusive with other flower biome tags).
                                Does nothing if biome is tagged "hills".

                                cold:
                                Villagers will be dressed for snowy weather.

                                deep:
                                Pre-Caves and Cliffs, prevents an ocean from having islands or connected rivers and makes the biome less likely to have hills.

                                desert:
                                Allows partially-buried ruined portals to be placed in the biome.
                                Sand blocks will play ambient sounds when the player is nearby.

                                extreme_hills:
                                Ruined portals can be placed higher than normal.
                                Biomes tagged "forest" or "forest_generation" will use normal Overworld flowers instead of forest flowers.

                                flower_forest:
                                Biome uses forest flowers (mutually exclusive with other flower biome tags).

                                forest:
                                Biome uses forest flowers (mutually exclusive with other flower biome tags).
                                Does nothing if biome is tagged "taiga" or "extreme_hills".

                                forest_generation:
                                Equivalent to "forest".

                                frozen:
                                Villagers will be dressed for snowy weather.
                                Prevents the biome from containing lava springs if it is also tagged "ocean".

                                ice:
                                Around ruined portals, lava is always replaced by Netherrack and Netherrack cannot be replaced by magma.

                                ice_plains:
                                Prevents the biome from containing lava springs if it is also tagged "mutated".

                                jungle:
                                Ruined portals will be very mossy.

                                hills:
                                Biomes tagged "meadow" or "birch" will use normal Overworld flowers instead of wildflowers.

                                meadow:
                                Biome uses wildflowers (mutually exclusive with other flower biome tags).
                                Does nothing if biome is tagged "hills".

                                mesa:
                                Sand blocks will play ambient sounds when the player is nearby.

                                mountain:
                                Ruined portals can be placed higher than normal.

                                mutated:
                                Pre-Caves and Cliffs, prevents switching to the specified "mutate_transformation" as the biome is already considered mutated.
                                Prevents the biome from containing lava springs if it is also tagged "ice_plains".

                                no_legacy_worldgen:
                                Prevents biome from using legacy world generation behavior unless the biome is being placed in the Overworld.

                                ocean:
                                Prevents the biome from containing lava springs if it is also tagged "frozen".
                                Allows ruined portals to be found underwater.
                                Pre-Caves and Cliffs, determines if shorelines and rivers should be placed at the edges of the biome and identifies the biome as a shallow ocean for placing islands, unless the "deep" tag is present.

                                pale_garden:
                                Biome uses closed-eye blossoms (mutually exclusive with other flower biome tags).

                                plains:
                                Biome uses plains flowers (mutually exclusive with other flower biome tags).

                                rare:
                                Pre-Caves and Cliffs, this tag flags the biome as a special biome.
                                Oceans cannot be special.

                                swamp:
                                Allows ruined portals to be found underwater.
                                Biome uses swamp flowers (mutually exclusive with other flower biome tags).

                                taiga:
                                Biomes tagged "forest" or "forest_generation" will use normal Overworld flowers instead of forest flowers.
                                """),

        };
    }

}
