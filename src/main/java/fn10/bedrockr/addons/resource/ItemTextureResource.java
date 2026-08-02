package fn10.bedrockr.addons.resource;

import java.io.File;

public class ItemTextureResource extends TextureResource {
    public ItemTextureResource(String name, String id, File png) {
        super(name, id, TextureType.Item, png);
    }
}
