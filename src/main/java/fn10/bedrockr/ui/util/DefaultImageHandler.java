package fn10.bedrockr.ui.util;

import fn10.bedrockr.addons.element.supporting.block.BlockTexture;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.ui.rendering.RenderHandler;
import fn10.bedrockr.utils.ImageHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DefaultImageHandler extends ImageHandler<BufferedImage> {
    @Override
    public BufferedImage getImageFromBytes(byte[] data) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    @Override
    public byte[] getBytesFromImage(BufferedImage img) throws IOException {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        ImageIO.write(img, "png", str);
        return str.toByteArray();
    }

    @Override
    public BufferedImage resizeImage(int w, int h, BufferedImage img) {
        return ImageUtilities.toBuffered(ImageUtilities.ResizeImage(img,w,h));
    }

    @Override
    public BufferedImage resizeImageSharp(int w, int h, BufferedImage img) {
        return ImageUtilities.toBuffered(ImageUtilities.ResizeImage(img,w,h, Image.SCALE_AREA_AVERAGING));
    }

    @Override
    public BufferedImage renderBlock(String name, BlockTexture btex, WorkspaceResources res) throws IOException {
        return RenderHandler.renderBlock(name, btex, res);
    }
}
