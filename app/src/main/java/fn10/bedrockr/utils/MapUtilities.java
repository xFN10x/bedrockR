package fn10.bedrockr.utils;

import java.util.Map;
import java.util.Map.Entry;

public class MapUtilities {

    public static <K, V> K getKeyFromValue(Map<K, V> map, V Value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(Value))
                return entry.getKey();
        }
        return null;
    }

}
