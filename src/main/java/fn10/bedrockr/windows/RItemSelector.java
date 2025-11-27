package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.naming.NameNotFoundException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.Item;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.UnlockCondition;
import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ItemLikeElement;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import fn10.bedrockr.windows.base.RDialog;

public class RItemSelector extends RDialog {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public class ItemJsonEntry implements Comparable<ItemJsonEntry> {
        public int id;
        public int stackSize;
        public String name;
        public String displayName;

        public ReturnItemInfo toReturnItemInfo() {
            String[] splitId = name.split(":");
            return new ReturnItemInfo(splitId[0], displayName, splitId[1]);
        }

        @Override
        public int compareTo(ItemJsonEntry o) {
            return name.compareTo(o.name);
        }
    }

    public static class ReturnItemInfo {
        public String Id;
        public String Name;
        public ImageIcon Texture;
        public String Prefix;

        public ReturnItemInfo() {
        }

        public ReturnItemInfo(String id, String name, String prefix) {
            this(id, name, prefix, new ImageIcon());
        }

        public ReturnItemInfo(String id, String name, String prefix, Image texture) {
            this(id, name, prefix, new ImageIcon(texture));
        }

        public ReturnItemInfo(String id, String name, String prefix, ImageIcon texture) {
            Id = id;
            Name = name;
            if (texture != null) {
                if (texture.getImage() != null) {
                    Texture = texture;
                }
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
                return RItemSelector.getItemById(null, con.item, workspace);
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
                return RItemSelector.getItemById(null, con.item, workspace);
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
                    building.add(RItemSelector.getItemById(null, item.item, workspace));
                } catch (NameNotFoundException | IncorrectWorkspaceException e) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                }
            }
            return building;
        }
    }

    public class DataPathsJson {
        public HashMap<String, HashMap<String, String>> pc;
        public HashMap<String, HashMap<String, String>> bedrock;
    }

    protected final JPanel InnerPanel = new JPanel();
    protected final JScrollPane selector = new JScrollPane(InnerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JButton addButton = new JButton("Add");
    private final JButton cancelButton = new JButton("Cancel");

    protected ReturnItemInfo selected = null;

    public static final int OK_CHOICE = 1;
    public static final int CANCEL_CHOICE = 0;

    public final SpringLayout Lay = new SpringLayout();

    protected Integer choice = CANCEL_CHOICE;

    public static ItemJsonEntry[] vanillaItems;

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
    public static ReturnItemInfo getItemById(Window doing, String fullID, String workspaceName)
            throws IncorrectWorkspaceException, NameNotFoundException {
        // check the non-vanilla items
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(doing, workspaceName)) {
            if (element instanceof ItemLikeElement) {
                String Id = ((ItemLikeElement) element).getItemId();
                String Name = ((ItemLikeElement) element).getDisplayName();
                String Prefix = RFileOperations.getWorkspaceFile(doing, workspaceName).Prefix;
                Image img = ((ItemLikeElement) element).getTexture(workspaceName);

                if (fullID.equals(Prefix + ":" + Id)) {
                    return new ReturnItemInfo(Id, Name, Prefix, img);
                }
            }
        }

        if (fullID.startsWith("minecraft")) {
            for (ItemJsonEntry item : vanillaItems) {
                Launcher.LOG.info("lets see if (" + item.name + ") equals (" + fullID + ")");
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
        Launcher.LOG.info("Non-Vanilla items: " + RFileOperations.getElementsFromWorkspace(doing, workspaceName));
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(doing, workspaceName)) {
            if (element instanceof ItemLikeElement ile) {
                Launcher.LOG.info(ile.getItemId());
            }
        }
        throw new NameNotFoundException("The item by id: " + fullID + ", doesnt exist. Printed all items to log.");
    }

    public static void downloadVanillaItems() {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest dataPathsReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
            HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

            HashMap<String, String> versionPaths = gson.fromJson(dataPathsRes.body(), DataPathsJson.class).bedrock
                    .get(RNewAddon.PICKABLE_VERSIONS[0]);

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

                building.name = "minecraft:" + entry.name;

                parsedEntrys.add(building);
            }
            vanillaItems = parsedEntrys.toArray(new ItemJsonEntry[0]);
            Arrays.sort(vanillaItems);
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
    }

    protected RItemSelector(Frame parent, String Workspace) {
        super(
                parent,
                JDialog.DISPOSE_ON_CLOSE,
                "Item Selection",
                new Dimension(500, 400));

        addButton.addActionListener(e -> {
            if (selected == null) {
                JOptionPane.showMessageDialog(parent, "You must select an item, or cancel.", "Selection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            choice = OK_CHOICE;
            dispose();
        });
        cancelButton.addActionListener(e -> {
            choice = CANCEL_CHOICE;
            dispose();
        });

        // south
        Lay.putConstraint(SpringLayout.SOUTH, addButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, cancelButton, -10, SpringLayout.SOUTH, getContentPane());
        // sides
        Lay.putConstraint(SpringLayout.EAST, addButton, -10, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, cancelButton, 10, SpringLayout.WEST, getContentPane());
        // selector
        Lay.putConstraint(SpringLayout.WEST, selector, 5, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, selector, -5, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, selector, -5, SpringLayout.NORTH, addButton);
        Lay.putConstraint(SpringLayout.NORTH, selector, 5, SpringLayout.NORTH, getContentPane());

        InnerPanel.setLayout(new GridLayout(0, 4, 3, 3));
        selector.getVerticalScrollBar().setUnitIncrement(18);

        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(parent, Workspace)) {
            if (element instanceof ItemLikeElement) {
                JButton ToAdd = new JButton();
                Dimension size = new Dimension(48, 48);
                ToAdd.setMinimumSize(size);
                ToAdd.setPreferredSize(size);
                ToAdd.setFont(ToAdd.getFont().deriveFont(8f));
                Image image = ((ItemLikeElement) element).getTexture(Workspace);
                ImageIcon icon = new ImageIcon(image);
                if (image != null)
                    ToAdd.setIcon(icon);
                else
                    ToAdd.setText(((ItemLikeElement) element).getDisplayName());
                ToAdd.setToolTipText(
                        ((ItemLikeElement) element).getDisplayName() + " (" + ((ItemLikeElement) element).getItemId() +
                                ")");
                ToAdd.addActionListener(e -> {
                    ReturnItemInfo building = new ReturnItemInfo();
                    building.Id = ((ItemLikeElement) element).getItemId();
                    building.Name = ((ItemLikeElement) element).getDisplayName();
                    try {
                        building.Prefix = ((WorkspaceFile) new SourceWorkspaceFile(Files.readString(RFileOperations
                                .getFileFromWorkspace(parent, Workspace, "/" + RFileOperations.WPFFILENAME, true)
                                .toPath())).getSerilized()).Prefix;
                    } catch (IOException e1) {
                        fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                        building.Prefix = "error";
                    }
                    building.Texture = icon;
                    selected = building;
                });
                InnerPanel.add(ToAdd);
            }
        }

        for (ItemJsonEntry item : vanillaItems) {
            try {
                JButton ToAdd = new JButton();
                ToAdd.setMargin(new Insets(2, 1, 2, 1));
                Dimension size = new Dimension(48, 48);
                ToAdd.setMinimumSize(size);
                ToAdd.setPreferredSize(size);
                ToAdd.setFont(ToAdd.getFont().deriveFont(8f));
                File proposedRender = Path
                        .of(RFileOperations.getBaseDirectory(this, "cache", "renders").getAbsolutePath(),
                                item.name + ".png")
                        .toFile();
                ToAdd.setText(item.displayName);
                if (proposedRender.exists()) {
                    ToAdd.setIcon(new ImageIcon(ImageIO.read(proposedRender).getScaledInstance(45, 45,
                            BufferedImage.SCALE_AREA_AVERAGING)));
                }
                ToAdd.setToolTipText(item.displayName + " (" + item.name +
                        ")");
                ToAdd.addActionListener(e -> {
                    ReturnItemInfo building = new ReturnItemInfo();
                    building.Id = item.name;
                    building.Name = item.displayName;
                    building.Prefix = "minecraft";
                    building.Texture = null;
                    selected = building;
                });
                InnerPanel.add(ToAdd);

            } catch (Exception e1) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
            }
        }

        setLayout(Lay);

        // selector.add(InnerPanel);
        add(addButton);
        add(cancelButton);
        add(selector);

        setModal(true);
    }

    /**
     * 
     * @return A map entry, in of which, the key is the UUID, and the value is the
     *         image to be displayed.
     */
    public ReturnItemInfo getSelected() {
        return selected;
    }

    public static ReturnItemInfo openSelector(Frame parent, String Workspace)
            throws InterruptedException {
        var thiS = new RItemSelector(parent, Workspace);

        thiS.setVisible(true);

        if (thiS.choice == CANCEL_CHOICE) {
            Launcher.LOG.info("canceled");
            return null;
        } else
            return thiS.getSelected();

    }
}
