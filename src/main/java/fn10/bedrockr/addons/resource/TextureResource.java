package fn10.bedrockr.addons.resource;

import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.ImageHandler;

import java.io.File;
import java.io.IOException;

public abstract class TextureResource extends Resource {
    
    public final TextureType type;
            
    public TextureResource(String name, String id, TextureType type, File png) {
        super(name, id, png);
        this.type = type;
    }

    public <T> void resizeImage(int w, int h, ImageHandler<T> handler) throws IOException {
        handler.resizeImage(w,h, loadImage(handler));
    }
    
    public <T> void readImage(ImageHandler<T> handler, T image) throws IOException {
        data = handler.getBytesFromImage(image);
    }
    
    public <T> T loadImage(ImageHandler<T> handler) throws IOException {
        return handler.getImageFromBytes(data);
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
        Item,
        Block
    }
}
