package fn10.bedrockr.addons.addon.jsonClasses;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

public class SharedJSONClasses {

    @Deprecated(forRemoval = true)
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
