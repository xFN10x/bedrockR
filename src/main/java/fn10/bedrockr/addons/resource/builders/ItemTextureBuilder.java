package fn10.bedrockr.addons.resource.builders;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.mcjson.resource.ItemTextures;
import fn10.bedrockr.addons.resource.ItemTextureResource;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.RFileOperations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;

public class ItemTextureBuilder extends ResourceBuilder<ItemTextureResource> {
    public ItemTextureBuilder(WorkspaceResources wres) {
        super(wres);
    }

    @Override
    public Class<ItemTextureResource> getResClass() {
        return ItemTextureResource.class;
    }

    @Override
    protected void buildTyped(Collection<ItemTextureResource> resources) throws IOException {
        RFileOperations.LOG.info("Building Item Textures...");
        Path itemTexture = f( "textures/item_texture.json");
        
        ItemTextures itemTextureJObj = new ItemTextures();
        itemTextureJObj.resource_pack_name = wres.wpf.WorkspaceName;
        itemTextureJObj.texture_name = "atlas.items";
        
        HashMap<String, ItemTextures.TextureData> data = new HashMap<>();
        itemTextureJObj.texture_data = data;

        for (ItemTextureResource res : resources) {
            ItemTextures.TextureData tdata = new ItemTextures.TextureData(res.ID);
            data.put(res.getBuiltName(wres), tdata);
            
            RFileOperations.write(f("textures/item/" + res.ID + ".png"), res.getData());
        }
        
        RFileOperations.write(itemTexture, RFileOperations.gson.toJson(itemTextureJObj));
        
        RFileOperations.LOG.info("Built Item Textures.");
    }
}                             
