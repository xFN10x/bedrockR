package fn10.bedrockr.utils.typeAdapters;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class StrictMapSerilizer implements JsonSerializer<Map<String, Object>> {

    @Override
    public JsonElement serialize(Map<String, Object> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Object> entry : src.entrySet()) {
            Object value = entry.getValue();

            var key = entry.getKey();

            if (value instanceof Integer) {
                jsonObject.addProperty(key, (int) value);
            } else if (value instanceof Long) {
                jsonObject.addProperty(key, (Long) value);
            } else if (value instanceof String) {
                jsonObject.addProperty(key, (String) value);
            } else if (value instanceof Boolean) {
                jsonObject.addProperty(key, (Boolean) value);
            } else if (value instanceof Double) {
                if (((Double) value).toString().endsWith(".0"))
                    jsonObject.addProperty(key, ((Double) value).intValue());
                else
                    jsonObject.addProperty(key, (Double) value);
            } else {
                jsonObject.add(key, context.serialize(value));
            }
        }
        return jsonObject;
    }
}