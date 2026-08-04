package fn10.bedrockr.addons.element.interfaces;

import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.ImageHandler;

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
}
