package fn10.bedrockr.addons.addon.jsonClasses;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.RMapElement;

public class SharedJSONClasses {

    public static class StrictMapSerilizer implements JsonSerializer<Map<String, Object>> {

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

    @Deprecated
    public static class ComponentMapDeserilizer implements JsonDeserializer<Map<RMapElement, Object>> {
        @Override
        public Map<RMapElement, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Map<RMapElement, Object> toBuild = new HashMap<RMapElement, Object>();
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                RMapElement lookupResault = RMapElement.LookupMap.get(entry.getKey());

                Object value;
                JsonElement Value = entry.getValue();

                JsonPrimitive primitive = Value.getAsJsonPrimitive();

                if (primitive.isJsonPrimitive()) {
                    if (primitive.isBoolean()) {
                        value = primitive.getAsBoolean();
                    } else if (primitive.isString()) {
                        value = primitive.getAsString();
                    } else if (primitive.isNumber()) {
                        value = primitive.getAsNumber();
                    } else {
                        value = null;
                    }
                } else {
                    value = context.deserialize(Value, Value.getClass());
                }

                toBuild.put(lookupResault, value);
            }
            return toBuild;
        }

    }

    public static class minecraftDamage {
        @SerializedName("minecraft:damage")
        public int damage;
    }

    public static class minecraftDestructibleByMining {
        public float seconds_to_destroy;
    }

    public static class VersionVector {

        public static Vector<Integer> fromString(String fromThis) {
            if (fromThis == null)
                return new Vector<Integer>();
            java.util.List<String> array = Arrays.asList(fromThis.split("\\."));

            // make vector
            var vec = new Vector<Integer>(3, 1);
            vec.add(0);
            vec.add(0);
            vec.add(0);

            // try adding to it
            try {
                vec.setElementAt(Integer.valueOf(array.get(0)), 0); // 1
                vec.setElementAt(Integer.valueOf(array.get(1)), 1); // 2
                vec.setElementAt(Integer.valueOf(array.get(2)), 2); // 3
            } catch (Exception e) {
            }
            return vec;
        }
    }
}
