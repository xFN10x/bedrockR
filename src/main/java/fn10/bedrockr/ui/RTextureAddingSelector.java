package fn10.bedrockr.ui;

import java.awt.*;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.ui.base.RDialog;
import fn10.bedrockr.utils.RFileOperations;

@SuppressWarnings("FieldCanBeLocal")
public class RTextureAddingSelector extends RDialog {

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

    protected RTextureAddingSelector(Window parent, Integer TextureType, String Workspace) {
        super(
                parent,
                JDialog.DISPOSE_ON_CLOSE,
                "Texture Selection",
                new Dimension(500, 400));

        addButton.addActionListener(e -> {
            if (selected == null) {
                JOptionPane.showMessageDialog(parent, "You must select a texture, or cancel.", "Selection Error",
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
                    return (ImageIcon) selected.getIcon();
                }

                @Override
                public ImageIcon setValue(ImageIcon value) {
                    return null;
                }

            };
        else
            return null;
    }

    public static Map.Entry<String, ImageIcon> openSelector(Window parent, Integer TextureType, String Workspace)
            throws InterruptedException {
        var thiS = new RTextureAddingSelector(parent, TextureType, Workspace);

        thiS.setVisible(true);

        if (thiS.choice == CANCEL_CHOICE) {
            RFileOperations.LOG.info("canceled");
            return null;
        } else
            return thiS.getSelected();

    }
}
