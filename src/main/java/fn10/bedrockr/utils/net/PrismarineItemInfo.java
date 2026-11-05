package fn10.bedrockr.utils.net;

import fn10.bedrockr.addons.element.supporting.item.ItemInfo;

public class PrismarineItemInfo implements Comparable<PrismarineItemInfo> {
    public int id;
    public int stackSize;
    public String name;
    public String displayName;

    public ItemInfo toItemInfo() {
        return new ItemInfo(name, displayName, "minecraft");
    }
    
    @Override
    public int compareTo(PrismarineItemInfo o) {
        return displayName.compareToIgnoreCase(o.displayName);
    }
}
