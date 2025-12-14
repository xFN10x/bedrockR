package fn10.bedrockr.addons.source.interfaces;

/**
 * An interface used for ItemFiles and BlockFiles.
 * 
 * This is used to signify that the element makes something that is an item, or
 * has an item form (like a block), that can be used in recipes.
 */
public interface ItemLikeElement {
    /**
     * Get the texture for the item in GUI
     * @param workspaceName - the name of the workspace this item is in
     * @return the image data
     */
    Byte[] getTexture(String workspaceName);

    String getItemId();

    String getDisplayName();
}
