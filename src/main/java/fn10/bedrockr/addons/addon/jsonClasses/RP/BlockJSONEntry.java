package fn10.bedrockr.addons.addon.jsonClasses.RP;

public class BlockJSONEntry {

    public String sound;
    public String textures;
    public String carried_textures;
    public Boolean isotropic;

    public BlockJSONEntry(String sound, String texture, String carried, Boolean iso) {
        this.sound = sound;
        this.textures = texture;
        this.carried_textures = carried;
        this.isotropic = iso;
    }

}
