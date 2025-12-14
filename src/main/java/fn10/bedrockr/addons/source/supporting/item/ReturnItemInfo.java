package fn10.bedrockr.addons.source.supporting.item;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NameNotFoundException;

import com.google.gson.Gson;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.Item;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.UnlockCondition;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.interfaces.ItemLikeElement;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;

public class ReturnItemInfo {
    private static final Gson gson = ElementSource.gson;

    public String Id;
    public String Name;
    public Byte[] Texture;
    public String Prefix;

    public static ItemJsonEntry[] vanillaItems;

    public static void downloadVanillaItems() {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest dataPathsReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
            HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

            @SuppressWarnings("unchecked")
            HashMap<String, String> versionPaths = (HashMap<String, String>) ((Map<String, Object>) gson
                    .fromJson(dataPathsRes.body(), Map.class).get("bedrock"))
                    .get(RFileOperations.PICKABLE_VERSIONS[0]);

            String path = versionPaths.get("items");

            HttpRequest itemJsonReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                                    + path
                                    + "/items.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();

            HttpResponse<String> itemsjsonRes = client.send(itemJsonReq, BodyHandlers.ofString());
            ItemJsonEntry[] itemEntrys = gson.fromJson(itemsjsonRes.body(), ItemJsonEntry[].class);
            ArrayList<ItemJsonEntry> parsedEntrys = new ArrayList<ItemJsonEntry>();
            for (ItemJsonEntry entry : itemEntrys) {
                ItemJsonEntry building = entry;

                if (!building.name.startsWith("minecraft"))
                    building.name = "minecraft:" + entry.name;

                parsedEntrys.add(building);
            }
            vanillaItems = parsedEntrys.toArray(new ItemJsonEntry[0]);
            Arrays.sort(vanillaItems);
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
    }

    public class BlockJsonEntry implements Comparable<BlockJsonEntry> {
        public int id;
        public String name;
        public String displayName;
        public float hardness;
        public float resistance;
        public int stackSize;
        public boolean diggable;
        public String material;
        public boolean transparent;
        public int emitLight;
        public int filterLight;
        public int defaultState;
        public int minStateId;
        public int maxStateId;
        public Map<String, Boolean> harvestTools;
        public int[] drops;
        public String boundingBox;

        public ReturnItemInfo toReturnItemInfo() {
            if (name.contains(":")) {
                String[] splitId = name.split(":");
                return new ReturnItemInfo(splitId[1], displayName, splitId[0]);
            } else {
                return new ReturnItemInfo(name, displayName, "");
            }
        }

        @Override
        public int compareTo(BlockJsonEntry o) {
            return displayName.compareToIgnoreCase(o.displayName);
        }
    }

    public static BlockJsonEntry[] vanillaBlocks;

    /**
     * 
     * @param fullID        the id of the item. 'prefix':'id'
     * @param workspaceName the name of the workspace.
     * @return a ReturnItemInfo with the info of the item found, or null if it
     *         wasent found.
     * @throws IncorrectWorkspaceException if the prefix of the fullID isnt used in
     *                                     the workspace
     * @throws NameNotFoundException       if the item isnt found
     */
    public static ReturnItemInfo getBlockById(String fullID, String workspaceName)
            throws IncorrectWorkspaceException, NameNotFoundException {
        // check the non-vanilla items
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(workspaceName)) {
            if (element instanceof ItemLikeElement ile) {
                String Id = ile.getItemId();
                String Name = ile.getDisplayName();
                String Prefix = RFileOperations.getWorkspaceFile(workspaceName).Prefix;
                Byte[] img = ile.getTexture(workspaceName);

                if (fullID.equals(Prefix + ":" + Id)) {
                    return new ReturnItemInfo(Id, Name, Prefix, img);
                }
            }
        }

        if (fullID.startsWith("minecraft")) {
            for (BlockJsonEntry item : vanillaBlocks) {
                if (item.name.equals(fullID)) {
                    return item.toReturnItemInfo();
                }
            }
        } else {
            throw new IncorrectWorkspaceException("The prefix: " + fullID.split(":")[0]
                    + ", isnt vanilla, and it isnt used in the workspace: " + workspaceName);
        }
        throw new NameNotFoundException("The item by id: " + fullID + ", doesnt exist.");
    }

    @SuppressWarnings("unchecked")
    public static void downloadVanillaBlocks() {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest dataPathsReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
            HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

            HashMap<String, String> versionPaths = (HashMap<String, String>) ((HashMap<String, Object>)gson.fromJson(dataPathsRes.body(), Map.class).get("bedrock"))
                    .get(RFileOperations.PICKABLE_VERSIONS[0]);

            String path = versionPaths.get("blocks");

            HttpRequest itemJsonReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                                    + path
                                    + "/blocks.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();

            HttpResponse<String> itemsjsonRes = client.send(itemJsonReq, BodyHandlers.ofString());
            BlockJsonEntry[] itemEntrys = gson.fromJson(itemsjsonRes.body(), BlockJsonEntry[].class);
            ArrayList<BlockJsonEntry> parsedEntrys = new ArrayList<BlockJsonEntry>();
            for (BlockJsonEntry entry : itemEntrys) {
                BlockJsonEntry building = entry;
                if (!building.name.startsWith("minecraft"))
                    building.name = "minecraft:" + entry.name;
                parsedEntrys.add(building);
            }
            vanillaBlocks = parsedEntrys.toArray(new BlockJsonEntry[0]);
            Arrays.sort(vanillaBlocks);
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
    }

    public ReturnItemInfo() {
    }

    public ReturnItemInfo(String id, String name, String prefix) {
        this(id, name, prefix, null);
    }

    public ReturnItemInfo(String id, String name, String prefix, Byte[] texture) {
        Id = id;
        Name = name;
        if (texture != null) {
            Texture = texture;
        }
        Prefix = prefix;
    }

    public boolean equals(ReturnItemInfo other) {
        return (other.Prefix + ":" + other.Id).equals(Prefix + ":" + Id);
    }

    public boolean equals(Item other) {
        return other.item.equals(Prefix + ":" + Id);
    }

    public static ReturnItemInfo fromUnlockCondition(UnlockCondition con, String workspace) {
        try {
            return ReturnItemInfo.getItemById(con.item, workspace);
        } catch (IncorrectWorkspaceException | NameNotFoundException e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    public static List<ReturnItemInfo> fromUnlockCondition(Collection<? extends UnlockCondition> list,
            String workspace) {
        ArrayList<ReturnItemInfo> building = new ArrayList<ReturnItemInfo>();
        for (UnlockCondition info : list) {
            building.add(ReturnItemInfo.fromUnlockCondition(info, workspace));
        }
        return building;
    }

    public Item toRecipeItem() {
        return new Item(Prefix + ":" + Id);
    }

    public static ReturnItemInfo fromRecipeItem(Item con, String workspace) {
        try {
            return ReturnItemInfo.getItemById(con.item, workspace);
        } catch (IncorrectWorkspaceException | NameNotFoundException e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    public static List<ReturnItemInfo> fromRecipeItem(Collection<? extends Item> list,
            String workspace) {
        ArrayList<ReturnItemInfo> building = new ArrayList<ReturnItemInfo>();
        for (Item item : list) {
            try {
                building.add(ReturnItemInfo.getItemById(item.item, workspace));
            } catch (NameNotFoundException | IncorrectWorkspaceException e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        }
        return building;
    }

    /**
     * 
     * @param fullID        the id of the item. 'prefix':'id'
     * @param workspaceName the name of the workspace.
     * @return a ReturnItemInfo with the info of the item found, or null if it
     *         wasent found.
     * @throws IncorrectWorkspaceException if the prefix of the fullID isnt used in
     *                                     the workspace
     * @throws NameNotFoundException       if the item isnt found
     */
    public static ReturnItemInfo getItemById(String fullID, String workspaceName)
            throws IncorrectWorkspaceException, NameNotFoundException {
        // check the non-vanilla items
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(workspaceName)) {
            if (element instanceof ItemLikeElement) {
                String Id = ((ItemLikeElement) element).getItemId();
                String Name = ((ItemLikeElement) element).getDisplayName();
                String Prefix = RFileOperations.getWorkspaceFile(workspaceName).Prefix;
                Byte[] img = ((ItemLikeElement) element).getTexture(workspaceName);

                if (fullID.equals(Prefix + ":" + Id)) {
                    return new ReturnItemInfo(Id, Name, Prefix, img);
                }
            }
        }

        if (fullID.startsWith("minecraft")) {
            for (ItemJsonEntry item : vanillaItems) {
                // Launcher.LOG.info("lets see if (" + item.name + ") equals (" + fullID + ")");
                if (item.name.equals(fullID)) {
                    return item.toReturnItemInfo();
                }
            }
        } else {
            throw new IncorrectWorkspaceException("The prefix: " + fullID.split(":")[0]
                    + ", isnt vanilla, and it isnt used in the workspace: " + workspaceName);
        }
        Launcher.LOG.info("Vanilla items: ");
        for (ItemJsonEntry vanillaItem : vanillaItems) {
            Launcher.LOG.info(vanillaItem.name);
        }
        Launcher.LOG.info("Non-Vanilla items: " + RFileOperations.getElementsFromWorkspace(workspaceName));
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(workspaceName)) {
            if (element instanceof ItemLikeElement ile) {
                Launcher.LOG.info(ile.getItemId());
            }
        }
        throw new NameNotFoundException("The item by id: " + fullID + ", doesnt exist. Printed all items to log.");
    }
}
