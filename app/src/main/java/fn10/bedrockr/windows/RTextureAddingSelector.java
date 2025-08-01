package fn10.bedrockr.windows;

import java.awt.*;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.source.jsonClasses.ResourceFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.base.RDialog;

public class RTextureAddingSelector extends RDialog {

    protected final JPanel InnerPanel = new JPanel();
    protected final JScrollPane selector = new JScrollPane(InnerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JButton addButton = new JButton("Add");
    private final JButton cancelButton = new JButton("Cancel");

    public static final int OK_CHOICE = 1;
    public static final int CANCEL_CHOICE = 0;

    public final SpringLayout Lay = new SpringLayout();

    protected Integer choice = CANCEL_CHOICE;

    protected RTextureAddingSelector(Frame parent, Integer TextureType, String Workspace) {
        super(
                parent,
                JDialog.DISPOSE_ON_CLOSE,
                "Texture Selection",
                new Dimension(500, 400));

        addButton.addActionListener(e -> {
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

        ResourceFile res = RFileOperations.getResources(parent,
                Workspace).Serilized;
        Map<String, Integer> resTypes = res.ResourceTypes;
        Map<String, String> resIDs = res.ResourceIDs;

        for (Map.Entry<String, Integer> entry : resTypes.entrySet()) {
            try {
                if (entry.getValue() == ResourceFile.ITEM_TEXTURE) {
                    var ToAdd = new JButton();
                    var size = new Dimension(48, 48);
                    var normalIcon = new ImageIcon(res
                            .getResourceFile(parent, Workspace, entry.getKey(), ResourceFile.ITEM_TEXTURE).getPath());
                    var resizedIcon = new ImageIcon(normalIcon.getImage().getScaledInstance((int) size.getWidth(),
                            (int) size.getHeight(), Image.SCALE_AREA_AVERAGING));
                    ToAdd.setMinimumSize(size);
                    ToAdd.setPreferredSize(size);
                    ToAdd.setIcon(resizedIcon);
                    ToAdd.setName(resIDs.get(entry.getKey()));
                    ToAdd.setToolTipText(entry.getKey() + " (" + resIDs.get(entry.getKey()) + ")");
                    InnerPanel.add(ToAdd);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
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
     * @return A map entry, in of which, the key is the UUID, and the value is the image to be displayed.
     */
    public Map.Entry<String, ImageIcon> getSelected() {
        for (Component comp : InnerPanel.getComponents()) {
            if (comp.getName() == null || comp.getName().equals("NO"))
                continue; // check to see what it is
            if (((JButton) comp).isSelected())
                return new Map.Entry<String, ImageIcon>() {

                    @Override
                    public String getKey() {
                        return comp.getName();
                    }

                    @Override
                    public ImageIcon getValue() {
                        return (ImageIcon) ((JButton) comp).getIcon();
                    }

                    @Override
                    public ImageIcon setValue(ImageIcon value) {
                        return null;
                    }

                };
        }
        return null;
    }

    public static Map.Entry<String, ImageIcon> openSelector(Frame parent, Integer TextureType, String Workspace)
            throws InterruptedException {
        var thiS = new RTextureAddingSelector(parent, TextureType, Workspace);

        thiS.setVisible(true);

        if (thiS.choice == CANCEL_CHOICE)
            return null;
        else
            return thiS.getSelected();

    }
}
