package fn10.bedrockr.addons.source.supporting.item;

public class ItemJsonEntry implements Comparable<ItemJsonEntry> {
    public int id;
    public int stackSize;
    public String name;
    public String displayName;

    public ReturnItemInfo toReturnItemInfo() {
        String[] splitId = name.split(":");
        return new ReturnItemInfo(splitId[1], displayName, splitId[0]);
    }

    @Override
    public int compareTo(ItemJsonEntry o) {
        return displayName.compareToIgnoreCase(o.displayName);
    }
}