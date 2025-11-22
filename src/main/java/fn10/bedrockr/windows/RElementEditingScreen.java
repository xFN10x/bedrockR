package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.WrapLayout;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;
import fn10.bedrockr.windows.interfaces.ValidatableValue;

/**
 * An RDialog that provides the basic parts to make a source element builder
 * window. Fields, and field generation is up to the source element.
 */
public class RElementEditingScreen extends RDialog implements ActionListener {

    private ElementCreationListener Listener;
    public JPanel InnerPane = new JPanel();
    private JScrollPane Pane = new JScrollPane(InnerPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JPanel SpecialPane = null;
    private SpringLayout SpecialPaneLay = new SpringLayout();
    public BoxLayout PaneLay = new BoxLayout(InnerPane, BoxLayout.PAGE_AXIS);

    public Class<?> SourceClass;
    public Class<? extends ElementSource<?>> SourceElementClass;
    public ElementSource<?> SourceElement;

    public List<ValidatableValue> Fields = new ArrayList<ValidatableValue>();
    public List<ValidatableValue> RequiredFields = new ArrayList<ValidatableValue>();
    public List<ValidatableValue> IncorrectFields = new ArrayList<ValidatableValue>();

    public static final Integer DEFAULT_STYLE = 0;
    public static final Integer SPECIAL_AREA_STYLE = 1;

    private CustomCreateFunction createFunction = null;

    public final JButton CreateButton = new JButton("Create!");
    public final JButton DraftButton = new JButton("Save as draft");
    public final JButton CancelButton = new JButton("Cancel");

    public static interface CustomCreateFunction {
        void onCreate(RElementEditingScreen Sindow, ElementCreationListener Listener, boolean isDraft);

    }

    public RElementEditingScreen addVaildations(ValidatableValue... values) {
        Fields.addAll(List.of(values));
        return this;
    }

    /**
     * Replaces the Fields array, with a custom one.
     * 
     * @param func - the {@code CustomCreateFunction} to use for creation.
     * @return the {@code RElementEditingScreen}
     */
    public RElementEditingScreen setCustomCreateFunction(CustomCreateFunction func) {
        this.createFunction = func;
        return this;
    }

    public RElementEditingScreen(Window Parent, String elementName, ElementSource<?> sourceElementClass,
            Class<?> sourceClass,
            ElementCreationListener listenier) {
        this(Parent, elementName, sourceElementClass, sourceClass, listenier, DEFAULT_STYLE);
    }

    @SuppressWarnings("unchecked")
    public RElementEditingScreen(Window Parent, String elementName, ElementSource<?> sourceElementClass,
            Class<?> sourceClass,
            ElementCreationListener listenier, Integer layout) {
        super(
                Parent,
                DISPOSE_ON_CLOSE,
                "Editing " + elementName,
                ElementSource.defaultSize);

        this.Listener = listenier;
        this.SourceClass = sourceClass;
        this.SourceElementClass = (Class<? extends ElementSource<?>>) sourceElementClass.getClass();

        CreateButton.setActionCommand("create");
        CreateButton.addActionListener(this);

        DraftButton.setActionCommand("draft");
        DraftButton.addActionListener(this);

        CancelButton.setActionCommand("cancel");
        CancelButton.addActionListener(this);

        JSeparator Sep = new JSeparator(JSeparator.HORIZONTAL);

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
        if (layout == DEFAULT_STYLE) {
            InnerPane.setLayout(new WrapLayout(FlowLayout.CENTER, 6, 6));

            Lay.putConstraint(SpringLayout.EAST, Pane, -5, SpringLayout.EAST, getContentPane());
        } else if (layout == SPECIAL_AREA_STYLE) {
            InnerPane.setLayout(new BoxLayout(InnerPane, BoxLayout.Y_AXIS));
            InnerPane.add(Box.createRigidArea(new Dimension(0, 4)));

            Lay.putConstraint(SpringLayout.EAST, Pane, -5, SpringLayout.HORIZONTAL_CENTER, getContentPane());
            SpecialPane = new JPanel();
            SpecialPane.setLayout(SpecialPaneLay);

            Lay.putConstraint(SpringLayout.SOUTH, SpecialPane, 0, SpringLayout.NORTH, Sep);
            Lay.putConstraint(SpringLayout.NORTH, SpecialPane, 0, SpringLayout.NORTH, getContentPane());
            Lay.putConstraint(SpringLayout.WEST, SpecialPane, 0, SpringLayout.EAST, Pane); // only one not copied
            Lay.putConstraint(SpringLayout.EAST, SpecialPane, 0, SpringLayout.EAST, getContentPane());

            add(SpecialPane);
        }

        Pane.getVerticalScrollBar().setUnitIncrement(16);

        add(CreateButton);
        add(DraftButton);
        add(CancelButton);
        add(Sep);
        add(Pane);
        setModal(true);
    }

    public void addField(RElementValue Field) {
        InnerPane.add(Field);
        InnerPane.add(Box.createRigidArea(new Dimension(0, 4)));
        Fields.add(Field);
        if (Field.Required)
            RequiredFields.add(Field);
    }

    public void setSpecialField(RElementValue Field) throws IllegalAccessError {
        if (SpecialPane == null)
            throw new IllegalAccessError("This Element Creation Screen was not set to be the special layout.");
        SpecialPane.add(Field);
        // put field all over
        SpecialPaneLay.putConstraint(SpringLayout.SOUTH, Field, 0, SpringLayout.SOUTH, SpecialPane);
        SpecialPaneLay.putConstraint(SpringLayout.NORTH, Field, 0, SpringLayout.NORTH, SpecialPane);
        SpecialPaneLay.putConstraint(SpringLayout.WEST, Field, 0, SpringLayout.WEST, SpecialPane);
        SpecialPaneLay.putConstraint(SpringLayout.EAST, Field, 0, SpringLayout.EAST, SpecialPane);

        Fields.add(Field);
        if (Field.Required)
            RequiredFields.add(Field);
    }

    public List<ValidatableValue> checkForErrors(boolean strict) {
        List<ValidatableValue> IncorrectFields = new ArrayList<ValidatableValue>();
        var log = Launcher.LOG;
        log.info("--------------------- CHECKING FOR ERRORS-----------------------");
        for (ValidatableValue validatable : Fields) {
            if (!validatable.valid(strict))
                IncorrectFields.add(validatable);
        }
        if (IncorrectFields.size() != 0) {
            this.IncorrectFields = IncorrectFields;
            return IncorrectFields;
        } else {
            return null;
        }
    }

    private void create(boolean isDraft) {
        try { // handle if there is no constructor
            var workingClass = ((ElementFile<?>) SourceClass.getConstructor().newInstance()); // make new elementfile
            for (ValidatableValue validatable : Fields) { // add the fields
                if (validatable instanceof RElementValue) {
                    if (!((RElementValue) validatable).getOptionallyEnabled()) // if its not enabled, continue
                    {
                        continue;
                    } else
                        try {
                            SourceClass.getField(((RElementValue) validatable).getTarget()).set(workingClass,
                                    ((RElementValue) validatable).getValue());
                            // try to set field ^
                        } catch (Exception e) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                            ErrorShower.showError(null, "Failed to change a field; continuing", e.getMessage(), e);
                            continue;
                        }
                }

            }

            workingClass.setDraft(isDraft);

            Listener.onElementCreate(SourceElementClass.getConstructor(SourceClass).newInstance(workingClass)); // create
            this.dispose();
        } catch (Exception ex) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", ex);
            ErrorShower.showError((Frame) getParent(), "Failed to create ElementSource",
                    "Source Creation Error", ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var action = e.getActionCommand();
        if (action.equals("create")) { // create, check for errors, get user to solve them, ready for build
            if (checkForErrors(true) == null) {
                if (createFunction == null)
                    create(false);
                else
                    createFunction.onCreate(this, Listener, false);
            } else { // show errored things
                var builder = new StringBuilder("<html>There were error(s) while creating this element: <br><ul>");
                for (ValidatableValue EV : IncorrectFields) {
                    builder.append("<li>" + EV.getName() + ": " + EV.getProblemMessage() + "</li>");
                }

                JOptionPane.showMessageDialog(this, builder.toString(), "Element Creation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (action.equals("draft")) { // drafting, check if nessesary fields are entered
            if (checkForErrors(false) == null) {
                if (createFunction == null)
                    create(true);
                else
                    createFunction.onCreate(this, Listener, true);
            } else { // show errored things
                // pov: you thought something was going to complicated, but you didnt need to
                // search anything up VVVVVVV
                var builder = new StringBuilder("<html>There were error(s) while creating this element: <br><ul>");
                for (ValidatableValue EV : IncorrectFields) {
                    builder.append("<li>" + EV.getName() + ": " + EV.getProblemMessage() + "</li>");
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
