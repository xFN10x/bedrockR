package fn10.bedrockr.addons.addon.jsonClasses;

import java.util.Arrays;
import java.util.Vector;

import com.google.gson.annotations.SerializedName;

public class SharedJSONClasses {

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
