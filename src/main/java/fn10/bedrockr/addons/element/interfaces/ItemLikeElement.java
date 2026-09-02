package fn10.bedrockr.addons.element.interfaces;

import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.utils.ImageHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

/**
 * An interface used for ItemFiles and BlockFiles.
 * 
 * This is used to signify that the element makes something that is an item, or
 * has an item form (like a block), that can be used in recipes.
 */
public interface ItemLikeElement {
    String getItemId();

    String getDisplayName();

    <T> byte[] getTexture(WorkspaceResources res, ImageHandler<T> handler) throws IOException;
    
    default ItemInfo getItemInfo(String wp, WorkspaceResources res,  ImageHandler<?> handler) throws IOException {
        return new ItemInfo(getItemId(), getDisplayName(), wp, ArrayUtils.toObject(getTexture(res, handler)));
    }
}
