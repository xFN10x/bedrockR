package fn10.bedrockr.ui;

import fn10.bedrockr.addons.element.elementFiles.BlockFile;
import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.element.interfaces.ItemLikeElement;
import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.addons.element.supporting.item.ItemJsonEntry;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.ui.base.RDialog;
import fn10.bedrockr.ui.components.RItemButton;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RLogUtils;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("FieldCanBeLocal")
public class RItemSelector extends RDialog implements ActionListener {
    public static final int FILTER_BLOCKS = 0b1100;
    public static final int FILTER_ITEMS = 0b0100;
    public static final int FILTER_BLOCKS_WP = 0b1110;
    public static final int FILTER_ITEMS_WP = 0b0110;
    
    private static final int FILTER_WP_ONLY_BIT = 0b0010;
    private static final int FILTER_BLOCKS_BIT = 0b1000;

    public static final int CANCEL_CHOICE = 0;
    public static final int OK_CHOICE = 1;

    protected final JPanel InnerPanel = new JPanel();
    protected final JScrollPane selector = new JScrollPane(InnerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JButton cancelButton = new JButton("Cancel");
    private final JTextField searchBox = new JTextField();

    private ItemInfo selected = null;
    @MagicConstant(intValues = {
            FILTER_BLOCKS, FILTER_BLOCKS_WP, FILTER_ITEMS, FILTER_ITEMS_WP
    })
    private final int filter;

    public final SpringLayout Lay = new SpringLayout();


    public void showItemsWithTerm(String searchTerm, Frame parent, String Workspace) {
        InnerPanel.removeAll();
        for (ElementFile<?> element : RFileOperations.getElementsFromWorkspace(Workspace)) {
            try {
                if (element instanceof ItemLikeElement bf && !element.getDraft()) {
                    if (hasBit(filter, FILTER_BLOCKS_BIT) && bf instanceof BlockFile) continue;
                    ItemInfo info = bf.getItemInfo(Workspace, WorkspaceResources.load(Workspace), ImageUtilities.ImgHandler);
                    if (!info.Name.toLowerCase().contains(searchTerm))
                        continue;

                    InnerPanel.add(new RItemButton(info, this, this));
                }
            } catch (Exception e) {
                RLogUtils.warnException(e);
            }
        }
        if (!hasBit(filter, FILTER_WP_ONLY_BIT))
            for (ItemJsonEntry item : ItemInfo.vanillaItems) {
                try {
                    if (
                            item.displayName.toLowerCase().contains(searchTerm)
                                    || item.name.toLowerCase().contains(searchTerm)
                    )
                        InnerPanel.add(new RItemButton(item.toReturnItemInfo(), this, this));

                } catch (Exception e1) {
                    RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e1);
                }
            }
        InnerPanel.revalidate();
        InnerPanel.repaint();
    }

    public static boolean hasBit(int subject, int bit) {
        return (subject & bit) == bit;
    }

    protected RItemSelector(Frame parent,
                            String Workspace,
                            @MagicConstant(intValues = {
                                    FILTER_BLOCKS, FILTER_BLOCKS_WP, FILTER_ITEMS, FILTER_ITEMS_WP
                            }) int filter) {
        super(
                parent,
                JDialog.DISPOSE_ON_CLOSE,
                (hasBit(filter, FILTER_BLOCKS_BIT) ? "Block" : "Item") + " Selection",
                new Dimension(500, 400));
        this.filter = filter;

        cancelButton.addActionListener(e -> {
            dispose();
        });

        // south
        Lay.putConstraint(SpringLayout.SOUTH, cancelButton, -10, SpringLayout.SOUTH, getContentPane());
        // sides
        Lay.putConstraint(SpringLayout.WEST, cancelButton, 10, SpringLayout.WEST, getContentPane());
        // selector
        Lay.putConstraint(SpringLayout.WEST, selector, 5, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, selector, -5, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, selector, -5, SpringLayout.NORTH, cancelButton);
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
                showItemsWithTerm(searchBox.getText(), parent, Workspace);
            }

        });

        InnerPanel.setLayout(new GridLayout(0, 4, 3, 3));
        selector.getVerticalScrollBar().setUnitIncrement(18);

        showItemsWithTerm("", parent, Workspace);

        setLayout(Lay);

        // selector.add(InnerPanel);
        add(cancelButton);
        add(selector);
        add(searchBox);

        setModal(true);
    }

    public ItemInfo getSelected() {
        return selected;
    }

    public static ItemInfo openSelector(Frame parent, String Workspace, @MagicConstant(intValues = {FILTER_BLOCKS, FILTER_BLOCKS_WP, FILTER_ITEMS, FILTER_ITEMS_WP}) int filter)
            throws InterruptedException {
        var thiS = new RItemSelector(parent, Workspace, filter);
        thiS.setVisible(true);

        return thiS.getSelected();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof RItemButton rib) {
            //hey i just had ribs for dinner!
            selected = rib.get();
            setVisible(false);
        }
    }
}
