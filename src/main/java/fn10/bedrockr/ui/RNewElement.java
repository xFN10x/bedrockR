package fn10.bedrockr.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.element.elementSources.SourceScriptElement;
import fn10.bedrockr.addons.element.elementFiles.ScriptFile;
import fn10.bedrockr.addons.element.interfaces.ElementFile;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.addons.element.ElementCreationListener;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.ui.base.RDialog;
import fn10.bedrockr.ui.componets.RElement;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.ui.util.SpringUtilities;

@SuppressWarnings("FieldCanBeLocal")
public class RNewElement extends RDialog {

    private final JPanel MainPane = new JPanel();
    private final JScrollPane MainScrollPane = new JScrollPane(MainPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private final Frame Parent;
    private final String workspaceName;

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

        for (Class<? extends ElementSource<?>> class1 : RFileOperations.ELEMENTS) {
            try {
                ElementSource<?> obj = class1.getConstructor().newInstance();
                RElement relement = new RElement(obj, () -> createNewElement(obj));
                MainPane.add(relement);
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(getParent(), "error", e);
            }
        }

        SpringUtilities.makeCompactGrid(MainPane, RFileOperations.ELEMENTS.size(), 1, 5, 5, 5, 5);

        Lay.putConstraint(SpringLayout.EAST, MainScrollPane, -10, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, MainScrollPane, 10, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.NORTH, MainScrollPane, 10, SpringLayout.NORTH, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, MainScrollPane, -60, SpringLayout.SOUTH, getContentPane());

        add(MainScrollPane);

        setModal(true);
    }

    public void createNewElement(ElementSource<?> e) {
            if (e.getClass() == SourceScriptElement.class) {
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
                RElementEditingScreen screen = RElementEditingScreen.getElementsCreationScreen(e, this.Parent,
                        ((ElementCreationListener) this.Parent), workspaceName);
                if (screen == null) {
                    dispose();
                    return;
                }
                screen.setVisible(true);
                dispose();

            } catch (Exception ex) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
                if (ex.getCause() != null) {
                    ErrorShower.showError(getParent(),
                            "Failed to create component. " + ex.getCause().getMessage() + "\n\n",
                            "Erorrrrrrrrrr", ex);
                } else {
                    ErrorShower.showError(getParent(), "Failed to create component. ",
                            "Erorrrrrrrrrr", ex);
                }
            }
    }
}
