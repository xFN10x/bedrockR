package fn10.bedrockr.addons.resource;

import fn10.bedrockr.ui.util.ImageUtilities;
import org.apache.commons.io.monitor.FileEntry;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class TextureResource extends Resource {
    
    public final TextureType type;
            
    public TextureResource(String name, String id, TextureType type, File png) {
        super(name, id, png);
        this.type = type;
    }

    public void resizeImage(int w, int h) throws IOException {
        ImageUtilities.ResizeImage(loadImage(), w, h);
    }
    
    public void saveImage(RenderedImage img) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(img, resourceDataExtension(), stream);
        data = stream.toByteArray();
    }
    
    public BufferedImage loadImage() throws IOException {
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    @Override
    public String resourceDataExtension() {
        return "png";
    }

    @Override
    public String resourceTypeName() {
        return "texture/" + type.toString() ;
    }

    public enum TextureType {
        ITEM,
        BLOCK
    }
}
