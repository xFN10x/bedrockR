package fn10.bedrockr.addons.source.interfaces;

import java.awt.Image;

import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;

/**
 * An interface used for ItemFiles and BlockFiles.
 * 
 * This is used to signify that the element makes something that is an item, or
 * has an item form (like a block), that can be used in recipes.
 */
public interface ItemLikeElement {
    Image getTexture(WorkspaceFile workspace);

    String getItemId();

    String getDisplayName();
}
