package fn10.bedrockr.utils.typeAdapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.file.Path;

public class PathSerializer implements JsonSerializer<Path>, JsonDeserializer<Path> {

    @Override
    public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Path.of(json.getAsString());
    }

    @Override
    public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
