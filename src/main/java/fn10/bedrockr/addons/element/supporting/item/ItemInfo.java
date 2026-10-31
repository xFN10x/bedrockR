package fn10.bedrockr.addons.element.supporting.item;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NameNotFoundException;

import fn10.bedrockr.addons.element.interfaces.ItemLikeElement;
import fn10.bedrockr.addons.mcjson.behav.Recipe.Item;
import fn10.bedrockr.addons.mcjson.behav.Recipe.UnlockCondition;
import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.utils.ImageHandler;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import fn10.bedrockr.utils.net.PrismarineBlockInfo;
import fn10.bedrockr.utils.net.PrismarineItemInfo;
import org.apache.commons.lang3.ArrayUtils;

/**
 * A class repersenting an Item in bedrockR, this could be vanilla, or a custom item. This is different from a {@link fn10.bedrockr.utils.net.PrismarineItemInfo}/{@link fn10.bedrockr.utils.net.PrismarineBlockInfo}, as this is more simple and shouold be used in favour of them.
 * 
 * @see fn10.bedrockr.utils.net.PrismarineItemInfo
 * @see fn10.bedrockr.utils.net.PrismarineBlockInfo
 */
@SuppressWarnings({"unused"})
public class ItemInfo {
    ///The ID of the item, excluding the namespace. e.g, diamond_sword
    public String Id;
    ///The display name of the item, e.g, "Diamond Sword"
    public String Name;
    ///The texture in raw bytes.
    public Byte[] Texture;
    //The namespace of the item, e.g, "minecraft", or a workspace id.
    public String Prefix;

    /**
     * Searches for an item/block using the id of it.
     * @param fullID        the id of the item. 'prefix':'id'
     * @param workspaceName the name of the workspace.
     * @return an ItemInfo with the info of the item found, or {@code null} if it
     *         wasn't found.
     * @throws IncorrectWorkspaceException if the prefix of the fullID isn't used in
     *                                     the workspace
     * @throws NameNotFoundException       if the item isn't found
     */
    public static ItemInfo getById(String fullID, String workspaceName, ImageHandler<?> handler)
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
            for (PrismarineItemInfo item : RFileOperations.getCurrentNetHandler().getVanillaItems()) {
                if (item.name.equals(fullID)) {
                    return item.toItemInfo();
                }
            }
            for (PrismarineBlockInfo block : RFileOperations.getCurrentNetHandler().getVanillaBlocks()) {
                if (block.name.equals(fullID)) {
                    return block.toItemInfo();
                }
            }
        } else {
            throw new IncorrectWorkspaceException("The prefix: " + fullID.split(":")[0]
                    + ", isnt vanilla, and it isnt used in the workspace: " + workspaceName);
        }
        throw new NameNotFoundException("The item by id: " + fullID + ", doesnt exist.");
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
            return ItemInfo.getById(con.item, workspace, handler);
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
            return ItemInfo.getById(con.item, workspace, handler);
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
                building.add(ItemInfo.getById(item.item, workspace, handler));
            } catch (NameNotFoundException | IncorrectWorkspaceException | IOException |
                     WorkspaceResources.WorkspaceUnsupportedException e) {
                RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        }
        return building;
    }
}
