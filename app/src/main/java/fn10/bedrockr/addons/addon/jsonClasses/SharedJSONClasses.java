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

import fn10.bedrockr.Launcher;

public class SharedJSONClasses {

    public static class StrictMapSerilizer implements JsonSerializer<Map<String, Object>> {

        @Override
        public JsonElement serialize(Map<String, Object> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, Object> entry : src.entrySet()) {
                Object value = entry.getValue();
                Launcher.LOG.info(value.getClass().getName());
                if (value instanceof Integer) {
                    jsonObject.addProperty(entry.getKey(), (int) value);
                } else if (value instanceof Long) {
                    jsonObject.addProperty(entry.getKey(), (Long) value);
                } else if (value instanceof String) {
                    jsonObject.addProperty(entry.getKey(), (String) value);
                } else if (value instanceof Boolean) {
                    jsonObject.addProperty(entry.getKey(), (Boolean) value);
                } else if (value instanceof Double) {
                    jsonObject.addProperty(entry.getKey(), ((Double) value).intValue());
                } else {
                    jsonObject.add(entry.getKey(), context.serialize(value));
                }
            }
            return jsonObject;
        }

    }

    public static class minecraftDamage {
        @SerializedName("minecraft:damage")
        public int damage;

    }

    public static class VersionVector {

        public static Vector<Integer> fromString(String fromThis) {
            if (fromThis == null)
                return new Vector<Integer>();
            java.util.List<String> array = Arrays.asList(fromThis.split("\\."));

            // expand if not big enough
            // if (array.size() < 3) {
            // while (array.size() <= 3) {
            // array.addLast(fromThis);
            // }
            // }

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
