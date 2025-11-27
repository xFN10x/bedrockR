package fn10.bedrockr.addons.source.supporting;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.RMapElement.MapValueFilter;
import fn10.bedrockr.addons.source.interfaces.RMapElementProvider;
import fn10.bedrockr.addons.source.supporting.ItemComponents.minecraftDestructibleByMining;

public class BlockComponents implements RMapElementProvider {

    /*
     * TODO: add;
     * https://wiki.bedrock.dev/blocks/block-components#crafting-table
     * https://wiki.bedrock.dev/blocks/block-components#destruction-particles
     * https://wiki.bedrock.dev/blocks/block-components#flower-pottable
     * (add with custom models)
     * https://wiki.bedrock.dev/blocks/block-components#geometry
     * https://wiki.bedrock.dev/blocks/block-components#item-visual
     * https://wiki.bedrock.dev/blocks/block-components#light-dampening
     * https://wiki.bedrock.dev/blocks/block-components#light-emission
     * (add with custom loot) https://wiki.bedrock.dev/blocks/block-components#loot
     * https://wiki.bedrock.dev/blocks/block-components#map-color
     * https://wiki.bedrock.dev/blocks/block-components#movable
     * https://wiki.bedrock.dev/blocks/block-components#redstone-conductivity
     * https://wiki.bedrock.dev/blocks/block-components#redstone-producer
     * https://wiki.bedrock.dev/blocks/block-components#replaceable
     */
    public RMapElement[] getPickable() {
        return new RMapElement[] {
                new RMapElement("Broken from Explosions", "minecraft:destructible_by_explosion",
                        boolean.class,
                        "Determines if the block can be\ndestroyed by explosions."),
                new RMapElement("Hardness", "minecraft:destructible_by_mining",
                        minecraftDestructibleByMining.class,
                        "Determines the length it takes to\ndestroy this block. If you use the\nright tool to destroy the block, it\ntakes less time to destroy\n\nFormula for calculating seconds to\nbreak with fist is: 1.5 x (hardness)"),
                new RMapElement("Flammable", "minecraft:flammable", boolean.class,
                        "Determines if the block can be caught\non fire."),
                new RMapElement("Friction ", "minecraft:friction", float.class,
                        "Determines how much things slow down\nby walking on this block. This is what makes soul sand so slow to walk on.\n\nDefault friction is 0.4",
                        MapValueFilter.Between0And1),
        };
    }
}
