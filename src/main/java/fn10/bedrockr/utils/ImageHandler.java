package fn10.bedrockr.utils;

import fn10.bedrockr.addons.element.supporting.block.BlockTexture;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import jakarta.annotation.Nullable;

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

    public abstract T renderBlock(String id, BlockTexture texture, WorkspaceResources res);
    @Nullable
    public abstract T getRenderedBlock(String id);
    public T getRenderedBlock(String id, BlockTexture texture, WorkspaceResources res) {
        return getRenderedBlock(id, texture, res, false);
    }
    public T getRenderedBlock(String id, BlockTexture texture, WorkspaceResources res, boolean forceRemake) {
        T got = getRenderedBlock(id);
        if (got == null || forceRemake) return renderBlock(id, texture, res);
        else return got;
    }
}