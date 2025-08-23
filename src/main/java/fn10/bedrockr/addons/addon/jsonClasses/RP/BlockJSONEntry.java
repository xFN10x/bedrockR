package fn10.bedrockr.addons.addon.jsonClasses.RP;

public class BlockJSONEntry {

    public String sound;
    public String textures;
    public String carried_textures;
    public Boolean isotropic;

    public BlockJSONEntry(String sound, String texture, String carried, Boolean iso) {
        if (sound != null)
        this.sound = sound;
        if (texture != null)
        this.textures = texture;
        if (carried != null)
        this.carried_textures = carried;
        if (iso != null)
        this.isotropic = iso;
    }

}
