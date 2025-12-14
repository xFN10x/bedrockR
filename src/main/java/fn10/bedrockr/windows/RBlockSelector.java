package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.ArrayUtils;

import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.BlockFile;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo;
import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo.BlockJsonEntry;
import fn10.bedrockr.rendering.BlockTextures;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.base.RDialog;

public class RBlockSelector extends RDialog {
    protected final JPanel InnerPanel = new JPanel();
    protected final JScrollPane selector = new JScrollPane(InnerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JButton addButton = new JButton("Add");
    private final JButton cancelButton = new JButton("Cancel");
    private final JTextField searchBox = new JTextField();

    protected ReturnItemInfo selected = null;

    public static final int OK_CHOICE = 1;
    public static final int CANCEL_CHOICE = 0;

    public final SpringLayout Lay = new SpringLayout();

    protected Integer choice = CANCEL_CHOICE;

    public void showBlocksWithTerm(String searchTerm, Frame parent, String Workspace) {
        InnerPanel.removeAll();
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(Workspace)) {
            if (element instanceof BlockFile bf) {
                if (!searchTerm.isEmpty())
                    if (!bf.Name.toLowerCase().contains(searchTerm))
                        continue;
                JButton ToAdd = new JButton();
                ToAdd.setMargin(new Insets(2, 1, 2, 1));
                Dimension size = new Dimension(48, 48);
                ToAdd.setMinimumSize(size);
                ToAdd.setPreferredSize(size);
                ToAdd.setFont(ToAdd.getFont().deriveFont(8f));
                byte[] image = ArrayUtils.toPrimitive(bf.getTexture(Workspace));
                ImageIcon icon = new ImageIcon(image);
                if (!ArrayUtils.isEmpty(image))
                    ToAdd.setIcon(icon);
                else
                    ToAdd.setText(bf.getDisplayName());
                ToAdd.setToolTipText(
                        bf.getDisplayName() + " (" + bf.getItemId() +
                                ")");
                ToAdd.addActionListener(e -> {
                    ReturnItemInfo building = new ReturnItemInfo();
                    building.Id = bf.getItemId();
                    building.Name = bf.getDisplayName();
                    try {
                        building.Prefix = ((WorkspaceFile) new SourceWorkspaceFile(new String(Files.readAllBytes(RFileOperations
                                .getFileFromWorkspace(Workspace, "/" + RFileOperations.WPFFILENAME, true)
                                .toPath()))).getSerilized()).Prefix;
                    } catch (IOException e1) {
                        java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                        building.Prefix = "error";
                    }
                    building.Texture = ArrayUtils.toObject(image);
                    selected = building;
                });
                InnerPanel.add(ToAdd);
            }
        }

        for (BlockJsonEntry item : ReturnItemInfo.vanillaBlocks) {
            if (item.displayName.toLowerCase().contains(searchTerm))
                try {
                    JButton ToAdd = new JButton();
                    ToAdd.setMargin(new Insets(2, 1, 2, 1));
                    Dimension size = new Dimension(48, 48);
                    ToAdd.setMinimumSize(size);
                    ToAdd.setPreferredSize(size);
                    ToAdd.setFont(ToAdd.getFont().deriveFont(8f));
                    ImageIcon icon = BlockTextures.getBlockTexture(parent, item.name.split(":")[1]);
                    if (icon != null)
                        ToAdd.setIcon(icon);
                    ToAdd.setText(item.displayName);
                    ToAdd.setToolTipText(item.displayName + " (" + item.name +
                            ")");
                    ToAdd.addActionListener(e -> {
                        selected = item.toReturnItemInfo();
                    });
                    InnerPanel.add(ToAdd);

                } catch (Exception e1) {
                    java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                }
        }
        InnerPanel.revalidate();
        InnerPanel.repaint();
    }

    protected RBlockSelector(Frame parent, String Workspace) {
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
        Lay.putConstraint(SpringLayout.NORTH, selector, 3, SpringLayout.SOUTH, searchBox);
        // search
        Lay.putConstraint(SpringLayout.WEST, searchBox, 5, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, searchBox, -5, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.NORTH, searchBox, 3, SpringLayout.NORTH, getContentPane());

        searchBox.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                showBlocksWithTerm(searchBox.getText(), parent, Workspace);
            }

        });

        InnerPanel.setLayout(new GridLayout(0, 4, 3, 3));
        selector.getVerticalScrollBar().setUnitIncrement(18);

        showBlocksWithTerm("", parent, Workspace);

        setLayout(Lay);

        // selector.add(InnerPanel);
        add(addButton);
        add(cancelButton);
        add(selector);
        add(searchBox);

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
        RBlockSelector thiS = new RBlockSelector(parent, Workspace);

        thiS.setVisible(true);

        if (thiS.choice == CANCEL_CHOICE) {
            return null;
        } else
            return thiS.getSelected();

    }
}
