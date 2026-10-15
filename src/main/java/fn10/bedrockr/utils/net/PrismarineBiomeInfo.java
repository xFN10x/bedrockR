package fn10.bedrockr.utils.net;

import org.jspecify.annotations.NonNull;

public class PrismarineBiomeInfo implements Comparable<PrismarineBiomeInfo> {
    public int id;
    public String name;
    public String category;
    public float temperature;
    public boolean has_precipitation;
    public String dimension;
    public String displayName;
    public int color;

    @Override
    public int compareTo(@NonNull PrismarineBiomeInfo o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
