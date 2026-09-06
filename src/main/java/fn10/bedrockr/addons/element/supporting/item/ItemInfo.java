package fn10.bedrockr.addons.element.supporting.item;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.naming.NameNotFoundException;

import fn10.bedrockr.addons.element.interfaces.ItemLikeElement;
import fn10.bedrockr.addons.mcjson.behav.Recipe.Item;
import fn10.bedrockr.addons.mcjson.behav.Recipe.UnlockCondition;
import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.ImageHandler;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import org.apache.commons.lang3.ArrayUtils;

import static fn10.bedrockr.utils.RFileOperations.gson;

@SuppressWarnings({"unused", "unchecked"})
public class ItemInfo {
    public String Id;
    public String Name;
    public Byte[] Texture;
    public String Prefix;

    public static ItemJsonEntry[] vanillaItems;

    public static void downloadVanillaItems() throws URISyntaxException, IOException, InterruptedException {
        try (HttpClient client = newDownloadingClient()) {
            HttpRequest dataPathsReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
            HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

            Map<String, String> versionPaths = (Map<String, String>) ((Map<String, Object>) gson
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
            for (ItemJsonEntry building : itemEntrys) {
                if (!building.name.startsWith("minecraft"))
                    building.name = "minecraft:" + building.name;

                parsedEntrys.add(building);
            }
            vanillaItems = parsedEntrys.toArray(new ItemJsonEntry[0]);
            Arrays.sort(vanillaItems);
        }
    }

    private static HttpClient newDownloadingClient() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public static class BlockJsonEntry implements Comparable<BlockJsonEntry> {
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

        public ItemInfo toReturnItemInfo() {
            if (name.contains(":")) {
                String[] splitId = name.split(":");
                return new ItemInfo(splitId[1], displayName, splitId[0]);
            } else {
                return new ItemInfo(name, displayName, "");
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
    public static ItemInfo getBlockById(String fullID, String workspaceName, ImageHandler<?> handler)
            throws IncorrectWorkspaceException, NameNotFoundException, IOException, WorkspaceResources.WorkspaceUnsupportedException {
        // check the non-vanilla items
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(workspaceName)) {
            if (element instanceof ItemLikeElement ile) {
                String Id = ile.getItemId();
                String Name = ile.getDisplayName();
                String Prefix = RFileOperations.getWorkspacePrefix(workspaceName);
                byte[] img = ile.getTexture(RFileOperations.getWorkspaceFile(workspaceName).getRes(), handler);

                if (fullID.equals(Prefix + ":" + Id)) {
                    return new ItemInfo(Id, Name, Prefix, ArrayUtils.toObject(img));
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
    public static void downloadVanillaBlocks() throws IOException, InterruptedException, URISyntaxException {
        try (HttpClient client = newDownloadingClient()) {
            
            HttpRequest dataPathsReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
            HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

            Map<String, String> versionPaths = (Map<String, String>) ((Map<String, Object>)gson.fromJson(dataPathsRes.body(), Map.class).get("bedrock"))
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
        }
    }

    public ItemInfo() {
    }

    public ItemInfo(String id, String name, String prefix) {
        this(id, name, prefix, null);
    }

    public ItemInfo(String id, String name, String prefix, Byte[] texture) {
        Id = id;
        Name = name;
        if (texture != null) {
            Texture = texture;
        }
        Prefix = prefix;
    }

    public boolean equals(ItemInfo other) {
        return (other.Prefix + ":" + other.Id).equals(Prefix + ":" + Id);
    }

    public boolean equals(Item other) {
        return other.item.equals(Prefix + ":" + Id);
    }

    public static ItemInfo fromUnlockCondition(UnlockCondition con, String workspace, ImageHandler<?> handler) {
        try {
            return ItemInfo.getItemById(con.item, workspace, handler);
        } catch (IncorrectWorkspaceException | NameNotFoundException | IOException |
                 WorkspaceResources.WorkspaceUnsupportedException e) {
            RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    public static List<ItemInfo> fromUnlockCondition(Collection<? extends UnlockCondition> list,
                                                     String workspace, ImageHandler<?> handler) {
        ArrayList<ItemInfo> building = new ArrayList<ItemInfo>();
        for (UnlockCondition info : list) {
            building.add(ItemInfo.fromUnlockCondition(info, workspace, handler));
        }
        return building;
    }

    public Item toRecipeItem() {
        return new Item(Prefix + ":" + Id);
    }

    public static ItemInfo fromRecipeItem(Item con, String workspace, ImageHandler<?> handler) {
        try {
            return ItemInfo.getItemById(con.item, workspace, handler);
        } catch (IncorrectWorkspaceException | NameNotFoundException | IOException |
                 WorkspaceResources.WorkspaceUnsupportedException e) {
            RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    public static List<ItemInfo> fromRecipeItem(Collection<? extends Item> list,
                                                String workspace, ImageHandler<?> handler) {
        ArrayList<ItemInfo> building = new ArrayList<ItemInfo>();
        for (Item item : list) {
            try {
                building.add(ItemInfo.getItemById(item.item, workspace, handler));
            } catch (NameNotFoundException | IncorrectWorkspaceException | IOException |
                     WorkspaceResources.WorkspaceUnsupportedException e) {
                RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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
    public static ItemInfo getItemById(String fullID, String workspaceName, ImageHandler<?> handler)
            throws IncorrectWorkspaceException, NameNotFoundException, IOException, WorkspaceResources.WorkspaceUnsupportedException {
        // check the non-vanilla items
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(workspaceName)) {
            if (element instanceof ItemLikeElement ile) {
                String Id = ile.getItemId();
                String Prefix = RFileOperations.getWorkspacePrefix(workspaceName);
                if (fullID.equals(Prefix + ":" + Id)) {
                    String Name = ile.getDisplayName();
                byte[] img = ile.getTexture(RFileOperations.getWorkspaceFile(workspaceName).getRes(), handler);
                    return new ItemInfo(Id, Name, Prefix, ArrayUtils.toObject(img));
                }
            }
        }

        if (fullID.startsWith("minecraft")) {
            for (ItemJsonEntry item : vanillaItems) {
                // fn10.bedrockr.Launcher.LOG.info("lets see if (" + item.name + ") equals (" + fullID + ")");
                if (item.name.equals(fullID)) {
                    return item.toReturnItemInfo();
                }
            }
        } else {
            throw new IncorrectWorkspaceException("The prefix: " + fullID.split(":")[0]
                    + ", isnt vanilla, and it isnt used in the workspace: " + workspaceName);
        }
        RFileOperations.LOG.info("Vanilla items: ");
        for (ItemJsonEntry vanillaItem : vanillaItems) {
            RFileOperations.LOG.info(vanillaItem.name);
        }
        RFileOperations.LOG.info("Non-Vanilla items: " + String.join(", ",RFileOperations.getElementNamesFromWorkspace(workspaceName)));
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(workspaceName)) {
            if (element instanceof ItemLikeElement ile) {
                RFileOperations.LOG.info(ile.getItemId());
            }
        }
        throw new NameNotFoundException("The item by id: " + fullID + ", doesnt exist. Printed all items to log.");
    }
}
