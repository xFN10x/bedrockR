package fn10.bedrockr.addons.resource.builders;

import fn10.bedrockr.addons.mcjson.resource.TerrainTextures;
import fn10.bedrockr.addons.resource.BlockTextureResource;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.RFileOperations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;

public class BlockTextureBuilder extends ResourceBuilder<BlockTextureResource> {
    public BlockTextureBuilder(WorkspaceResources wres) {
        super(wres);
    }

    @Override
    public Class<BlockTextureResource> getResClass() {
        return BlockTextureResource.class;
    }

    @Override
    protected void buildTyped(Collection<BlockTextureResource> resources) throws IOException {
        RFileOperations.LOG.info("Building Block Textures...");
        Path blockTexture = f( "textures/terrain_texture.json");
        
        TerrainTextures blockTextureJObj = new TerrainTextures();
        blockTextureJObj.resource_pack_name = wres.wpf.WorkspaceName;
        blockTextureJObj.texture_name = "atlas.terrain";
        
        HashMap<String, TerrainTextures.TextureData> data = new HashMap<>();
        blockTextureJObj.texture_data = data;

        for (BlockTextureResource res : resources) {
            TerrainTextures.TextureData tdata = new TerrainTextures.TextureData(res.ID);
            data.put(res.getBuiltName(wres), tdata);
            
            RFileOperations.write(f("textures/blocks/" + res.ID + ".png"), res.getData());
        }
        
        RFileOperations.write(blockTexture, RFileOperations.gson.toJson(blockTextureJObj));
        
        RFileOperations.LOG.info("Built Block Textures.");
    }
}                             
