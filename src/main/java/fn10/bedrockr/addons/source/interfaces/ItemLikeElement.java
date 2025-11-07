package fn10.bedrockr.addons.source.interfaces;

import java.awt.Image;

/**
 * An interface used for ItemFiles and BlockFiles.
 * 
 * This is used to signify that the element makes something that is an item, or
 * has an item form (like a block), that can be used in recipes.
 */
public interface ItemLikeElement {
    Image getTexture(String workspaceName);

    String getItemId();

    String getDisplayName();
}
