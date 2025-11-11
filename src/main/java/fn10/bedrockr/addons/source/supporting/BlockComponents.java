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
         * (add with custom models) https://wiki.bedrock.dev/blocks/block-components#geometry
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
                        "Determines if the block can be destroyed by explosions."),
                new RMapElement("Hardness", "minecraft:destructible_by_mining",
                        minecraftDestructibleByMining.class,
                        "Determines the length it takes to destroy this block. If you use the right tool to destroy the block, it takes less tiem to destroy\n\nFormula for calculating seconds to break with fist is: 1.5 x (hardness)"),
                new RMapElement("Flammable", "minecraft:flammable", boolean.class,
                        "Determines if the block can be caught on fire."),
                new RMapElement("Friction ", "minecraft:friction", float.class,
                        "Determines how much things slow down by walking on this block. This is what makes soul sand so slow to walk on.\n\nDefault friction is 0.4",
                        MapValueFilter.Between0And1),
        };
    }
}
