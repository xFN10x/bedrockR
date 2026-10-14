package fn10.bedrockr.utils.net;

import org.jspecify.annotations.NonNull;

public class PrismarineBiomeInfo implements Comparable<PrismarineBiomeInfo> {
    int id;
    String name;
    String category;
    float temperature;
    boolean has_precipitation;
    String dimension;
    String displayName;
    int color;

    @Override
    public int compareTo(@NonNull PrismarineBiomeInfo o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
