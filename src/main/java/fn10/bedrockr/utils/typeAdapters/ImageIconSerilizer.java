package fn10.bedrockr.utils.typeAdapters;

import java.lang.reflect.Type;
import java.net.URI;

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
            return new ImageIcon(URI.create(context.deserialize(json, String.class)).toURL());
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(ImageIcon src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getDescription());
    }

}
