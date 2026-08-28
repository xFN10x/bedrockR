package fn10.bedrockr.addons.resource.interfaces;

import fn10.bedrockr.utils.ImageHandler;
import fn10.bedrockr.utils.RFileOperations;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public abstract class TextureResource extends Resource {
    
    public final TextureType type;
            
    public TextureResource(String name, String id, TextureType type, File png) {
        super(name, id, png);
        this.type = type;
    }

    public <T> void resizeImage(int w, int h, ImageHandler<T> handler) throws IOException {
        data = handler.getBytesFromImage(handler.resizeImage(w,h, loadImage(handler)));
    }
    
    public <T> void readImage(ImageHandler<T> handler, T image) throws IOException {
        data = handler.getBytesFromImage(image);
    }
    
    public <T> T loadImage(ImageHandler<T> handler) throws IOException {
        return handler.getImageFromBytes(data);
    }

    @Override
    public String getDataExtension() {
        return "png";
    }

    @Override
    public String getResourceTypeFolder() {
        return "texture/" + type.toString() ;
    }

    @Override
    public byte[] getResourceIcon() {
        if (data != null) return data;
        else return super.getResourceIcon();
    }

    @Override
    public void getTasks(HashMap<String, ResourceTask> map) {
        map.put("Resize 16x16", getResizeTask(16));
        map.put("Resize 32x32", getResizeTask(32));
        map.put("Resize 64x64", getResizeTask(64));
        map.put("Resize 128x128", getResizeTask(128));
    }

    private static @NonNull ResourceTask getResizeTask(int s) {
        return res -> {
            if (res instanceof TextureResource tres)
                tres.resizeImage(s, s, RFileOperations.getCurrentImgHandler());
        };
    }

    public enum TextureType {
        Item,
        Block
    }
}
