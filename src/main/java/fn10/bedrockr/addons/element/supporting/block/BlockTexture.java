package fn10.bedrockr.addons.element.supporting.block;

import fn10.bedrockr.addons.mcjson.resource.BlockJSONEntry;
import fn10.bedrockr.addons.resource.BlockTextureResource;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.addons.resource.interfaces.ResourcePointer;
import org.intellij.lang.annotations.MagicConstant;

import java.io.FileNotFoundException;
import java.util.Objects;

public class BlockTexture {
    public static final int ALL_FACES_MODE = 0;
    public static final int PILLAR_MODE = 1;
    public static final int PER_FACE_MODE = 2;
    @MagicConstant(intValues = {
            ALL_FACES_MODE, PILLAR_MODE, PER_FACE_MODE
    })
    private int mode;

    public ResourcePointer<BlockTextureResource> upTex;
    public ResourcePointer<BlockTextureResource> downTex;
    public ResourcePointer<BlockTextureResource> eastTex;
    public ResourcePointer<BlockTextureResource> westTex;
    public ResourcePointer<BlockTextureResource> northTex;
    public ResourcePointer<BlockTextureResource> southTex;

    public BlockJSONEntry.Textures convertToBlockJsonTextures(WorkspaceResources res) throws FileNotFoundException {
        return switch (mode) {
            case PILLAR_MODE ->
                    new BlockJSONEntry.Textures(upTex.get(res).getBuiltName(res), downTex.get(res).getBuiltName(res), northTex.get(res).getBuiltName(res));
            case PER_FACE_MODE -> new BlockJSONEntry.Textures(
                    upTex.get(res).getBuiltName(res),
                    downTex.get(res).getBuiltName(res),
                    eastTex.get(res).getBuiltName(res),
                    westTex.get(res).getBuiltName(res),
                    northTex.get(res).getBuiltName(res),
                    southTex.get(res).getBuiltName(res)
            );
            default -> new BlockJSONEntry.Textures(upTex.get(res).getBuiltName(res));
        };
    }

    public BlockTexture() {
        this(ResourcePointer.empty(BlockTextureResource.class));
    }

    public BlockTexture(ResourcePointer<BlockTextureResource> allFace) {
        mode = ALL_FACES_MODE;
        this.upTex = allFace;
    }

    public BlockTexture(ResourcePointer<BlockTextureResource> top, ResourcePointer<BlockTextureResource> bottom, ResourcePointer<BlockTextureResource> sides) {
        this(top, bottom, sides, sides, sides, sides);
        mode = PILLAR_MODE;
    }

    public BlockTexture(ResourcePointer<BlockTextureResource> top, ResourcePointer<BlockTextureResource> bottom, ResourcePointer<BlockTextureResource> north, ResourcePointer<BlockTextureResource> south, ResourcePointer<BlockTextureResource> east, ResourcePointer<BlockTextureResource> west) {
        mode = PER_FACE_MODE;
        this.upTex = top;
        this.downTex = bottom;
        this.northTex = north;
        this.southTex = south;
        this.eastTex = east;
        this.westTex = west;
    }

    public BlockTexture(ResourcePointer<BlockTextureResource> top, ResourcePointer<BlockTextureResource> bottom, ResourcePointer<BlockTextureResource> north, ResourcePointer<BlockTextureResource> south, ResourcePointer<BlockTextureResource> east, ResourcePointer<BlockTextureResource> west, @MagicConstant(intValues = {
            ALL_FACES_MODE, PILLAR_MODE, PER_FACE_MODE
    }) int mode) {
        this.mode = mode;
        this.upTex = top;
        this.downTex = bottom;
        this.northTex = north;
        this.southTex = south;
        this.eastTex = east;
        this.westTex = west;
    }

    @MagicConstant(intValues = {
            ALL_FACES_MODE, PILLAR_MODE, PER_FACE_MODE
    })
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
