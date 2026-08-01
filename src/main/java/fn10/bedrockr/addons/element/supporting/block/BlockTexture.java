package fn10.bedrockr.addons.element.supporting.block;

import fn10.bedrockr.addons.mcjson.resource.BlockJSONEntry;
import fn10.bedrockr.addons.resource.BlockTextureResource;
import fn10.bedrockr.addons.resource.ResourcePointer;

import java.io.FileNotFoundException;
import java.util.Objects;

public class BlockTexture {
    public static final int ALL_FACES_MODE = 0;
    public static final int PILLAR_MODE = 1;
    public static final int PER_FACE_MODE = 2;
    private int mode;

    public ResourcePointer<BlockTextureResource> upTex;
    public ResourcePointer<BlockTextureResource> downTex;
    public ResourcePointer<BlockTextureResource> eastTex;
    public ResourcePointer<BlockTextureResource> westTex;
    public ResourcePointer<BlockTextureResource> northTex;
    public ResourcePointer<BlockTextureResource> southTex;

    public BlockJSONEntry.Textures convertToBlockJsonTextures() throws FileNotFoundException {
        return null;
    }

    public BlockTexture(ResourcePointer<BlockTextureResource> allFace) {
        mode = 0;
        this.upTex = allFace;
    }

    public BlockTexture(ResourcePointer<BlockTextureResource> top, ResourcePointer<BlockTextureResource> bottom, ResourcePointer<BlockTextureResource> sides) {
        this(top, bottom, sides, sides, sides, sides);
        mode = 1;
    }

    public BlockTexture(ResourcePointer<BlockTextureResource> top, ResourcePointer<BlockTextureResource> bottom, ResourcePointer<BlockTextureResource> north, ResourcePointer<BlockTextureResource> south, ResourcePointer<BlockTextureResource> east, ResourcePointer<BlockTextureResource> west) {
        mode = 2;
        this.upTex = top;
        this.downTex = bottom;
        this.northTex = north;
        this.southTex = south;
        this.eastTex = east;
        this.westTex = west;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockTexture bt)
            return Objects.equals(upTex, bt.upTex) && Objects.equals(downTex, bt.downTex) && Objects.equals(northTex, bt.northTex) && Objects.equals(southTex, bt.southTex) && Objects.equals(eastTex, bt.eastTex) && Objects.equals(westTex, bt.westTex);
        else return super.equals(obj);
    }
}
