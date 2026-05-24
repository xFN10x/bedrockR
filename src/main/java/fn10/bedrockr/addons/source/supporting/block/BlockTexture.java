package fn10.bedrockr.addons.source.supporting.block;

import java.util.UUID;

public class BlockTexture {
    public enum BlockTextureMode {
        LOG,
        ALL,
        PERFACE
    }

    private final BlockTextureMode mode;

    public UUID upTexID;
    public UUID downTexID;
    public UUID eastTexID;
    public UUID westTexID;
    public UUID northTexID;
    public UUID southTexID;

    public BlockTexture(UUID allFace) {
        mode = BlockTextureMode.ALL;
        this.upTexID = allFace;
    }
}
