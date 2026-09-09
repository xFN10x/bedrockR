package fn10.bedrockr.ui;

import java.awt.*;
import java.util.Map;

import javax.swing.*;

import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.addons.resource.interfaces.ResourcePointer;
import fn10.bedrockr.addons.resource.interfaces.TextureResource;
import fn10.bedrockr.ui.base.RDialog;
import fn10.bedrockr.ui.components.RResourceButton;
import fn10.bedrockr.utils.RFileOperations;

@SuppressWarnings("FieldCanBeLocal")
public class RResourceSelector<R extends Resource> extends RDialog {

    protected final JPanel InnerPanel = new JPanel();
    protected final JScrollPane selector = new JScrollPane(InnerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JButton addButton = new JButton("Add");
    private final JButton newButton = new JButton("New...");
    private final JButton cancelButton = new JButton("Cancel");

    protected RResourceButton<R> selected = null;

    public static final int OK_CHOICE = 1;
    public static final int CANCEL_CHOICE = 0;

    public final SpringLayout Lay = new SpringLayout();

    protected Integer choice = CANCEL_CHOICE;

    protected RResourceSelector(Window parent, Class<R> type, WorkspaceResources res) {
        super(
                parent,
                JDialog.DISPOSE_ON_CLOSE,
                Resource.SelectionTypes.get(type) + " Selection",
                new Dimension(400, 500));

        addButton.addActionListener(_ -> {
            choice = OK_CHOICE;
            dispose();
        });
        cancelButton.addActionListener(_ -> {
            choice = CANCEL_CHOICE;
            dispose();
        });
        newButton.addActionListener(_ -> {
            try {
                RWorkspace.addTexture(parent, res.getWorkspace());
            } catch (WorkspaceResources.WorkspaceUnsupportedException e) {
                throw new RuntimeException(e);
            }
        });

        for (Resource re : res.resources) {
            if (type.isAssignableFrom(re.getClass()))
                InnerPanel.add(new RResourceButton<>(re, res, This -> {
                    for (Component comp : InnerPanel.getComponents()) {
                        if (comp instanceof RResourceButton<?> rrb) {
                            rrb.unselect();
                            selected = (RResourceButton<R>) This;
                        }
                    }
                }));
        }

        // south
        Lay.putConstraint(SpringLayout.SOUTH, addButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, cancelButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, newButton, -10, SpringLayout.SOUTH, getContentPane());
        // sides
        Lay.putConstraint(SpringLayout.EAST, addButton, -10, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, cancelButton, 10, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, newButton, -5, SpringLayout.WEST, addButton);
        // selector
        Lay.putConstraint(SpringLayout.WEST, selector, 5, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, selector, -5, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, selector, -5, SpringLayout.NORTH, addButton);
        Lay.putConstraint(SpringLayout.NORTH, selector, 5, SpringLayout.NORTH, getContentPane());

        InnerPanel.setLayout(new BoxLayout(InnerPanel, BoxLayout.Y_AXIS));
        selector.getVerticalScrollBar().setUnitIncrement(18);
        
        setLayout(Lay);

        // selector.add(InnerPanel);
        add(addButton);
        add(cancelButton);
        add(selector);
        if (TextureResource.class.isAssignableFrom(type)) {
            add(newButton);
        }

        setModal(true);
    }

    /**
     * 
     * @return A map entry, in of which, the key is the UUID, and the value is the
     *         image to be displayed.
     */
    public ResourcePointer<R> getSelected() {
        return selected.get();
    }

    public static <T extends Resource> ResourcePointer<T> openSelector(Window parent, Class<T> resType, WorkspaceResources res) {
        var selec = new RResourceSelector<>(parent, resType, res);

        selec.setVisible(true);

        if (selec.choice == CANCEL_CHOICE) {
            return null;
        } else
            return selec.getSelected();

    }
}
