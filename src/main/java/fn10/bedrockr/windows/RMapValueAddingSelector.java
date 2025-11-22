package fn10.bedrockr.windows;

import java.awt.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.RMapElement;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RMapElementViewer;

public class RMapValueAddingSelector extends RDialog {

    protected final JPanel InnerPanel = new JPanel();
    protected final JScrollPane selector = new JScrollPane(InnerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JButton addButton = new JButton("Add");
    private final JButton cancelButton = new JButton("Cancel");

    public static final int OK_CHOICE = 1;
    public static final int CANCEL_CHOICE = 0;

    public final SpringLayout Lay = new SpringLayout();

    protected Integer choice = CANCEL_CHOICE;

    protected RMapValueAddingSelector(Window parent, RMapElement[] AvailableComponents,
            java.util.List<RMapElement> UnavailableComponents) {
        super(
                parent,
                JDialog.DISPOSE_ON_CLOSE,
                "New Map Item",
                new Dimension(330, 400));

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

        for (RMapElement rMapElement : AvailableComponents) {
            try {
                Boolean continu = false;
                for (RMapElement unavailable : UnavailableComponents)
                    if (rMapElement.equals(unavailable)) {
                        continu = true;
                        break;
                    }

                if (continu)
                    continue;
                RMapElementViewer toAdd = new RMapElementViewer(() -> unselectAll(), rMapElement);
                toAdd.setAlignmentX(.5f);
                toAdd.setName("RMEV");
                InnerPanel.add(toAdd);
                InnerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

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

    protected void unselectAll() {
        for (Component comp : InnerPanel.getComponents()) {
            if (comp.getName() == null || !comp.getName().equals("RMEV"))
                continue; // check to see what it is

            RMapElementViewer casted = ((RMapElementViewer) comp);
            casted.unselect();
        }
    }

    public RMapElement getSelected() {
        for (Component comp : InnerPanel.getComponents()) {
            if (comp.getName() == null || !comp.getName().equals("RMEV"))
                continue; // check to see what it is

            RMapElementViewer casted = ((RMapElementViewer) comp);
            if (casted.getSelected())
                return casted.getMapElement();
        }
        return null;
    }

    public static RMapElement openSelector(Window parent, RMapElement[] AvailableComponents,
            java.util.List<RMapElement> UnavailableComponents)
            throws InterruptedException {
        var thiS = new RMapValueAddingSelector(parent, AvailableComponents, UnavailableComponents);

        thiS.setVisible(true);

        if (thiS.choice == CANCEL_CHOICE)
            return null;
        else
            return thiS.getSelected();
    }
}
