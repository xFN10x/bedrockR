package fn10.bedrockr.utils;

import fn10.bedrockr.addons.element.supporting.block.BlockTexture;
import fn10.bedrockr.addons.resource.WorkspaceResources;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * An abstract class for manuiplulating images. This is a thing because android doesn't jave AWT BufferedImages and such, so now i can abstract them.
 * @param <T>
 */
public abstract class ImageHandler<T> {
    public abstract T getImageFromBytes(byte[] data) throws IOException;

    public abstract byte[] getBytesFromImage(T img) throws IOException;

    public abstract T resizeImage(int w, int h, T img);

    public abstract T resizeImageSharp(int w, int h, T img);

    public abstract T renderBlock(String name, BlockTexture btex, WorkspaceResources res) throws IOException;

}