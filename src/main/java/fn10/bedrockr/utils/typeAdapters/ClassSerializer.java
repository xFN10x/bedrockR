package fn10.bedrockr.utils.typeAdapters;

import com.google.gson.*;

import javax.swing.*;
import java.lang.reflect.Type;

public class ClassSerializer implements JsonSerializer<Class>, JsonDeserializer<Class> {
    @Override
    public Class deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String str = json.getAsString();
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Class: " + str + " doesn't exist.", e);
        }
    }

    @Override
    public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getCanonicalName());
    }
}
