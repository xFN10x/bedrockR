package fn10.bedrockr.addons.resource;

import fn10.bedrockr.addons.resource.interfaces.TextureResource;

import java.io.File;

public class ItemTextureResource extends TextureResource {
    public ItemTextureResource(String name, String id, File png) {
        super(name, id, TextureType.Item, png);
    }

    @Override
    public String getResourceCategory() {
        return "Item Textures";
    }

    @Override
    public String getResourceIconName() {
        return "itemtex";
    }
}
