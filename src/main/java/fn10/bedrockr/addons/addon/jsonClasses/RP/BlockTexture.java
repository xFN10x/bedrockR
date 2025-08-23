package fn10.bedrockr.addons.addon.jsonClasses.RP;

import java.util.HashMap;
import java.util.Map;

public class BlockTexture {
    public static class TextureData {
        public String textures;

        /**
         * Create TextureData with one texture.
         * 
         * @param TextureName the texture name, no need to format it as
         *                    {@code resource_pack/textures/item/(text)}
         */
        public TextureData(String TextureName) {
            textures = "textures/blocks/" + TextureName.replace(".png", "");
        }
    }

    public String resource_pack_name;
    public String texture_name;
    public Map<String, TextureData> texture_data = new HashMap<String, TextureData>();
}
