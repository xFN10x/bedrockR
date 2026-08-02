package fn10.bedrockr.addons.resource;

import java.io.File;

public class BlockTextureResource extends TextureResource {
    public BlockTextureResource(String name, String id, File png) {
        super(name, id, TextureType.Block, png);
    }
}
