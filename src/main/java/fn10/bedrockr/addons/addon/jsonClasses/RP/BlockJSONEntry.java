package fn10.bedrockr.addons.addon.jsonClasses.RP;

import jakarta.annotation.Nonnull;

/**
 * https://learn.microsoft.com/en-us/minecraft/creator/reference/content/blockreference/examples/blocksjsonfilestructure?view=minecraft-bedrock-stable
 */
public class BlockJSONEntry {

    public static class Textures {
        //set all for the same instead of textures being a string
        public String up;
        public String down;

        //set these instead of side
        public String north;
        public String south;
        public String east;
        public String west;

        public Textures(String all) {
            this(all, all, all);
        }

        public Textures(String top, String bottom, String side) {
            this(top, bottom, side, side, side, side);
        }

        public Textures(String top, String bottom, String east, String west, String north, String south) {
            up = top;
            down = bottom;
            this.east = east;
            this.west = west;
            this.north = north;
            this.south = south;
        }
    }

    public String sound;
    public Textures textures;
    public String carried_textures;
    public Boolean isotropic;

    /**
     * Construct a block JSON entry with one texture.
     * @param sound The sounds this block uses.
     * @param texture The all-face texture/
     * @param carried The texture used in the inventory/
     * @param filtering Specifies if this block uses texture filtering to looks smoother.
     */
    public BlockJSONEntry(String sound, String texture, String carried, Boolean filtering) {
        if (sound != null)
            this.sound = sound;
        if (texture != null)
            this.textures = new Textures(texture);
        if (carried != null)
            this.carried_textures = carried;
        if (filtering != null)
            this.isotropic = filtering;
    }

    /**
     * Construct a block JSON entry with one texture.
     * @param sound The sounds this block uses.
     * @param textures The textures
     * @param carried The texture used in the inventory/
     * @param filtering Specifies if this block uses texture filtering to looks smoother.
     */
    public BlockJSONEntry(String sound, @Nonnull Textures textures, String carried, Boolean filtering) {
        if (sound != null)
            this.sound = sound;
        this.textures = textures;
        if (carried != null)
            this.carried_textures = carried;
        if (filtering != null)
            this.isotropic = filtering;
    }

}
