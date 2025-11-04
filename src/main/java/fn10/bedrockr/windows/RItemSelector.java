package fn10.bedrockr.windows;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.windows.base.RDialog;

public class RItemSelector extends RDialog {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public class ItemJsonEntry {
        public int id;
        public int stackSize;
        public String name;
        public String displayName;
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

    protected JButton selected = null;

    public static final int OK_CHOICE = 1;
    public static final int CANCEL_CHOICE = 0;

    public final SpringLayout Lay = new SpringLayout();

    protected Integer choice = CANCEL_CHOICE;

    public static ItemJsonEntry[] vanillaItems;

    public static void downloadVanillaItems(WorkspaceFile workspace) {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest dataPathsReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();
            HttpResponse<String> dataPathsRes = client.send(dataPathsReq, BodyHandlers.ofString());

            HashMap<String, String> versionPaths = gson.fromJson(dataPathsRes.body(), DataPathsJson.class).bedrock
                    .get(workspace.MinimumEngineVersion);

            String path = versionPaths.get("items");

            HttpRequest itemJsonReq = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                                    + path
                                    + "/items.json"))
                    .version(HttpClient.Version.HTTP_2).GET().build();

            HttpResponse<String> itemsjsonRes = client.send(itemJsonReq, BodyHandlers.ofString());
            ItemJsonEntry[] itemEntrys = gson.fromJson(itemsjsonRes.body(), ItemJsonEntry[].class);
            vanillaItems = itemEntrys;
            Arrays.sort(vanillaItems);
            for (ItemJsonEntry itemJsonEntry : itemEntrys) {
                System.out.println(itemJsonEntry.displayName);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        InnerPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
        selector.getVerticalScrollBar().setUnitIncrement(18);

        /*
         * for (Map.Entry<String, Integer> entry : resTypes.entrySet()) {
         * try {
         * if (entry.getValue() == TextureType) {
         * JButton ToAdd = new JButton();
         * Dimension size = new Dimension(48, 48);
         * ImageIcon normalIcon = new ImageIcon(res
         * .getResourceFile(parent, Workspace, entry.getKey(),
         * entry.getValue()).getPath());
         * ImageIcon resizedIcon = new
         * ImageIcon(normalIcon.getImage().getScaledInstance((int) size.getWidth(),
         * (int) size.getHeight(), Image.SCALE_AREA_AVERAGING));
         * ToAdd.setMinimumSize(size);
         * ToAdd.setPreferredSize(size);
         * ToAdd.setIcon(resizedIcon);
         * ToAdd.setName(resIDs.get(entry.getKey()));
         * ToAdd.setToolTipText(entry.getKey() + " (" + resIDs.get(entry.getKey()) +
         * ")");
         * ToAdd.addActionListener(e -> {
         * selected = ((JButton) e.getSource());
         * });
         * InnerPanel.add(ToAdd);
         * }
         * } catch (Exception e1) {
         * e1.printStackTrace();
         * }
         * }
         */

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
    public Map.Entry<String, ImageIcon> getSelected() {
        if (selected != null)
            return new Map.Entry<String, ImageIcon>() {

                @Override
                public String getKey() {
                    return selected.getName();
                }

                @Override
                public ImageIcon getValue() {
                    return (ImageIcon) ((JButton) selected).getIcon();
                }

                @Override
                public ImageIcon setValue(ImageIcon value) {
                    return null;
                }

            };
        else
            return null;
    }

    public static Map.Entry<String, ImageIcon> openSelector(Frame parent, String Workspace)
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
