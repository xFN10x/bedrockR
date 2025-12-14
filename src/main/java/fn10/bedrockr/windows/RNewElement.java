package fn10.bedrockr.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.source.SourceBiomeElement;
import fn10.bedrockr.addons.source.SourceBlockElement;
import fn10.bedrockr.addons.source.SourceFoodElement;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceRecipeElement;
import fn10.bedrockr.addons.source.SourceScriptElement;
import fn10.bedrockr.addons.source.elementFiles.ScriptFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.SpringUtilities;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RElement;
import fn10.bedrockr.windows.util.ErrorShower;

public class RNewElement extends RDialog implements ActionListener {

    private JPanel MainPane = new JPanel();
    private JScrollPane MainScrollPane = new JScrollPane(MainPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private JButton CreateAsNormalButton = new JButton("Create!");
    private Frame Parent;
    private String workspaceName;

    @SuppressWarnings("unchecked")
    public static final Class<? extends ElementSource<?>>[] ELEMENTS = new Class[] {
            SourceItemElement.class,
            SourceBlockElement.class,
            SourceScriptElement.class,
            SourceRecipeElement.class,
            SourceFoodElement.class,
            SourceBiomeElement.class,
    };

    public RNewElement(Frame Parent, String WorkspaceName) {
        super(
                Parent,
                DISPOSE_ON_CLOSE,
                "New Element",
                new Dimension(400, 500));

        MainPane.setLayout(new SpringLayout());
        this.Parent = Parent;
        this.workspaceName = WorkspaceName;

        MainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        for (Class<? extends ElementSource<?>> class1 : ELEMENTS) {
            try {
                MainPane.add(new RElement(class1, () -> {
                    for (Component c : MainPane.getComponents()) {
                        ((RElement) c).unselect();
                    }
                }));
            } catch (Exception e) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError((Frame) getParent(), "error", e);
                continue;
            }
        }

        CreateAsNormalButton.setActionCommand("create");
        CreateAsNormalButton.addActionListener(this);

        SpringUtilities.makeCompactGrid(MainPane, ELEMENTS.length, 1, 5, 5, 5, 5);

        Lay.putConstraint(SpringLayout.EAST, MainScrollPane, -10, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, MainScrollPane, 10, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.NORTH, MainScrollPane, 10, SpringLayout.NORTH, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, MainScrollPane, -60, SpringLayout.SOUTH, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, CreateAsNormalButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, CreateAsNormalButton, -10, SpringLayout.EAST, getContentPane());

        add(MainScrollPane);
        add(CreateAsNormalButton);

        setModal(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "create") {
            Class<? extends ElementSource<?>> Creating = null;

            for (Component component : MainPane.getComponents()) { // for loop to check what is selected, then breaking
                if (((RElement) component).getSelected()) {
                    java.util.logging.Logger.getGlobal().info("Making new " + ((RElement) component).getElement());
                    Creating = ((RElement) component).getElement();
                    break;
                }
            }
            if (Creating == null)
                return;

            if (Creating == SourceScriptElement.class) {
                for (ElementFile<?> elementsFromWorkspace : RFileOperations.getElementsFromWorkspace(
                        workspaceName)) {
                    if (elementsFromWorkspace.getClass() == ScriptFile.class) {
                        JOptionPane.showMessageDialog(Parent, "As of a1.2, you can only make 1 script in your addon.",
                                "Cannot make more than 1 script", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }

            try {
                ElementSource<?> instance = Creating.getDeclaredConstructor().newInstance();

                RElementEditingScreen screen = (RElementEditingScreen) Creating
                        .getMethod("getBuilderWindow", Window.class, ElementCreationListener.class, String.class)
                        .invoke(instance, this.Parent, this.Parent, workspaceName);
                screen.setVisible(true);
                dispose();

            } catch (Exception ex) {
                java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
                if (ex.getCause() != null) {
                    ErrorShower.showError((Frame) getParent(),
                            "Failed to create component. " + ex.getCause().getMessage() + "\n\n",
                            "Erorrrrrrrrrr", ex);
                } else {
                    ErrorShower.showError((Frame) getParent(), "Failed to create component. ",
                            "Erorrrrrrrrrr", ex);
                }
                return;
            }
        }
    }
}
