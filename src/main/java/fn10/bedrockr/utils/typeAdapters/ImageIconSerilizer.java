package fn10.bedrockr.utils.typeAdapters;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ImageIconSerilizer implements JsonSerializer<ImageIcon>, JsonDeserializer<ImageIcon> {

    @Override
    public ImageIcon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return new ImageIcon(ImageIO.read(new ByteArrayInputStream(context.deserialize(json, byte[].class))));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(ImageIcon src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            BufferedImage bi = new BufferedImage(src.getIconWidth(), src.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            bi.getGraphics().drawImage(src.getImage(), 0, 0, null);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", output);
            return context.serialize(Base64.getEncoder().encodeToString(output.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
