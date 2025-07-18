package fn10.bedrockr.windows;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

/**
 * An RDialog that provides the basic parts to make a source element builder window. Fields, and field generation is up to the source element.
 */
public class RElementEditingScreen extends RDialog implements ActionListener {

    private ElementCreationListener Listener;
    private JPanel Pane = new JPanel();
    private FlowLayout PaneLay = new FlowLayout(1, 8, 6);

    public Class<?> SourceClass;
    public Class<? extends ElementSource> SourceElementClass;
    public ElementSource SourceElement;

    public List<RElementValue> Fields = new ArrayList<RElementValue>();
    public List<RElementValue> RequiredFields = new ArrayList<RElementValue>();
    public List<RElementValue> IncorrectFields = new ArrayList<RElementValue>();

    public RElementEditingScreen(Frame Parent, String elementName, ElementSource sourceElementClass,
            Class<?> sourceClass,
            ElementCreationListener listenier) {
        super(
                Parent,
                DISPOSE_ON_CLOSE,
                "Editing " + elementName,
                ElementSource.defaultSize);

        this.Listener = listenier;
        this.SourceClass = sourceClass;
        this.SourceElementClass = sourceElementClass.getClass();

        var CreateButton = new JButton("Create!");
        CreateButton.setActionCommand("create");
        CreateButton.addActionListener(this);

        var DraftButton = new JButton("Save as draft");
        DraftButton.setActionCommand("draft");
        DraftButton.addActionListener(this);

        var CancelButton = new JButton("Cancel");
        CancelButton.setActionCommand("cancel");
        CancelButton.addActionListener(this);

        var Sep = new JSeparator(JSeparator.HORIZONTAL);

        Lay.putConstraint(SpringLayout.SOUTH, CreateButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, CreateButton, -10, SpringLayout.EAST, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, DraftButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, DraftButton, -10, SpringLayout.WEST, CreateButton);

        Lay.putConstraint(SpringLayout.SOUTH, CancelButton, -10, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, CancelButton, 10, SpringLayout.WEST, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, Sep, -10, SpringLayout.NORTH, CreateButton);
        Lay.putConstraint(SpringLayout.WEST, Sep, 5, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, Sep, -5, SpringLayout.EAST, getContentPane());

        Lay.putConstraint(SpringLayout.SOUTH, Pane, -5, SpringLayout.NORTH, Sep);
        Lay.putConstraint(SpringLayout.NORTH, Pane, 5, SpringLayout.NORTH, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, Pane, 5, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, Pane, -5, SpringLayout.EAST, getContentPane());

        Pane.setLayout(PaneLay);

        add(CreateButton);
        add(DraftButton);
        add(CancelButton);
        add(Sep);
        add(Pane);
        setModal(true);
    }

    public void addField(RElementValue Field) {
        Pane.add(Field);
        Fields.add(Field);
        if (Field.Required)
            RequiredFields.add(Field);
    }

    public List<RElementValue> checkForErrors(boolean strict) {
        List<RElementValue> IncorrectFields = new ArrayList<RElementValue>();
        for (RElementValue rElementValue : Fields) {
            if (!rElementValue.valid(strict))
                IncorrectFields.add(rElementValue);
        }
        // Launcher.LOG.info(String.valueOf(IncorrectFields.size()));
        if (IncorrectFields.size() != 0) {
            this.IncorrectFields = IncorrectFields;
            return IncorrectFields;
        } else {
            return null;
        }
    }

    private void create(boolean isDraft) {
        try { // handle if there is no constructor
            var workingClass = ((ElementFile) SourceClass.getConstructor().newInstance()); // make new elementfile
            for (RElementValue rElementValue : Fields) { // add the fields
                try {
                    SourceClass.getField(rElementValue.getTarget()).set(workingClass, rElementValue.getValue());
                } catch (Exception e) {
                    continue;
                }

            }

            workingClass.setDraft(isDraft);

            Listener.onElementCreate(SourceElementClass.getConstructor(SourceClass).newInstance(workingClass)); // create
            this.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorShower.showError((Frame) getParent(), "Failed to create ElementSource",
                    "Source Creation Error", ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var action = e.getActionCommand();
        if (action.equals("create")) { // create, check for errors, get user to solve them, ready for build
            if (checkForErrors(true) == null) {
                create(false);
            } else { // show errored things
                var builder = new StringBuilder("<html>There were error(s) while creating this element: <br><ul>");
                for (RElementValue EV : IncorrectFields) {
                    builder.append("<li>" + EV.getTarget() + ": " + EV.Problem + "</li>");
                }

                JOptionPane.showMessageDialog(this, builder.toString(), "Element Creation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (action.equals("draft")) { // drafting, check if nessesary fields are entered
            if (checkForErrors(false) == null) {
                create(false);
            } else { // show errored things
                // pov: you thought something was going to complicated, but you didnt need to
                // search anything up VVVVVVV
                var builder = new StringBuilder("<html>There were error(s) while creating this element: <br><ul>");
                for (RElementValue EV : IncorrectFields) {
                    builder.append("<li>" + EV.getTarget() + ": " + EV.Problem + "</li>");
                }

                JOptionPane.showMessageDialog(this, builder.toString(), "Element Creation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (action.equals("cancel")) {
            Listener.onElementCancel();
            this.dispose();
        } else {
            var ex = new Exception("That button dont exist! man i forgot how good dark tranquility is");
            ErrorShower.showError((Frame) getParent(),
                    "woah mate, button dont fit, dont fit, button, it dont fit, wont fit", "I did an oppsie", ex);

            throw new IllegalAccessError();
        }
    }
}
