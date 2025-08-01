package fn10.bedrockr.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceItemElement;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.SpringUtilities;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RElement;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class RNewElement extends RDialog implements ActionListener {

    private JPanel MainPane = new JPanel();
    private JScrollPane MainScrollPane = new JScrollPane(MainPane);

    private JButton CreateAsNormalButton = new JButton("Create!");
    private Frame Parent;
    private String workspaceName;

    @SuppressWarnings("unchecked")
    public static final Class<? extends ElementSource>[] ELEMENTS = new Class[] {
            SourceWPFile.class,
            SourceItemElement.class
    };

    public RNewElement(Frame Parent, String WorkspaceName ) {
        super(
                Parent,
                DISPOSE_ON_CLOSE,
                "New Element",
                new Dimension(400, 500));

        MainPane.setLayout(new SpringLayout());
        this.Parent = Parent;
        this.workspaceName = WorkspaceName;

        for (Class<? extends ElementSource> class1 : ELEMENTS) {
            try {
                MainPane.add(new RElement(class1, () -> {
                    for (Component c : MainPane.getComponents()) {
                        ((RElement) c).unselect();
                    }
                }));
            } catch (Exception e) {
                e.printStackTrace();
                ErrorShower.showError((Frame)getParent(), "error", "very bad error message", e);
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
            Class<? extends ElementSource> Creating = null;

            for (Component component : MainPane.getComponents()) { // for loop to check what is selected, then breaking
                if (((RElement) component).getSelected()) {
                    Launcher.LOG.info("Making new " + ((RElement) component).getElement());
                    Creating = ((RElement) component).getElement();
                    break;
                }
            }
            if (Creating == null)
                return;

            try {
                ElementSource instance = Creating.getDeclaredConstructor().newInstance();

                RElementEditingScreen screen = (RElementEditingScreen) Creating
                        .getMethod("getBuilderWindow", Frame.class, ElementCreationListener.class, String.class)
                        .invoke(instance, this.Parent, this.Parent,workspaceName);
                screen.setVisible(true);
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                if (ex.getCause() != null) {
                    ErrorShower.showError((Frame)getParent(), "Failed to create component. " + ex.getCause().getMessage() + "\n\n",
                            "Erorrrrrrrrrr", ex);
                } else {
                    ErrorShower.showError((Frame)getParent(), "Failed to create component. ",
                            "Erorrrrrrrrrr", ex);
                }
                return;
            }
        }
    }
}
