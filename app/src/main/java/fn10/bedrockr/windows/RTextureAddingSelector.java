package fn10.bedrockr.windows;

import java.awt.*;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.addons.resources.TextureResource;
import fn10.bedrockr.addons.source.jsonClasses.ResourceFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RMapElementViewer;

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

    protected RTextureAddingSelector(Frame parent, Integer TextureType, WPFile Workspace) {
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

        InnerPanel.setLayout(new BoxLayout(InnerPanel, BoxLayout.Y_AXIS));
        selector.getVerticalScrollBar().setUnitIncrement(18);

        Map<String, Integer> textureFiles = RFileOperations.getResources(parent,
                Workspace.WorkspaceName).Serilized.Files;

        for (Map.Entry<String,Integer> entry : textureFiles.entrySet()) {
            try {
                if (entry.getValue() == ResourceFile.ITEM_TEXTURE) {
                    var ToAdd = new JButton();
                    ToAdd.setIcon(new ImageIcon());
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

    protected void unselectAll() {
        for (Component comp : InnerPanel.getComponents()) {
            if (comp.getName() == null || !comp.getName().equals("RMEV"))
                continue; // check to see what it is

            RMapElementViewer casted = ((RMapElementViewer) comp);
            casted.unselect();
        }
    }

    public TextureResource getSelected() {
        for (Component comp : InnerPanel.getComponents()) {
            if (comp.getName() == null || !comp.getName().equals("RMEV"))
                continue; // check to see what it is

            RMapElementViewer casted = ((RMapElementViewer) comp); // TODO: change this
            if (casted.getSelected())
                return casted.getMapElement();
        }
        return null;
    }

    public static TextureResource openSelector(Frame parent, RMapElement[] AvailableComponents)
            throws InterruptedException {
        var thiS = new RTextureAddingSelector(parent, AvailableComponents);

        thiS.setVisible(true);

        if (thiS.choice == CANCEL_CHOICE)
            return null;
        else
            return thiS.getSelected();

    }
}
