package fn10.bedrockr.addons.resource;

import fn10.bedrockr.addons.resource.interfaces.TextureResource;

import java.io.File;

public class BlockTextureResource extends TextureResource {
    public BlockTextureResource(String name, String id, File png) {
        super(name, id, TextureType.Block, png);
    }

    @Override
    public String getResourceCategory() {
        return "Block Textures";
    }

    @Override
    public String getResourceIconName() {
        return "blocktex";
    }
}
