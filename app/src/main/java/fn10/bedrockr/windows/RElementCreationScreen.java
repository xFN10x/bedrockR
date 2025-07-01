package fn10.bedrockr.windows;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;

import com.google.gson.Gson;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.componets.RElementValue;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class RElementCreationScreen extends RDialog implements ActionListener {

    private ElementCreationListener Listener;
    private JPanel Pane = new JPanel();
    private FlowLayout PaneLay = new FlowLayout(1, 8, 6);

    public Class<?> SourceClass;
    public List<RElementValue> Fields = new ArrayList<RElementValue>();
    public List<RElementValue> RequiredFields = new ArrayList<RElementValue>();

    public RElementCreationScreen(Frame Parent, String elementName, Class<?> sourceElementClass,
            ElementCreationListener listenier) {
        super(
                Parent,
                DISPOSE_ON_CLOSE,
                "New " + elementName,
                ElementSource.defaultSize);

        this.Listener = Listener;
        this.SourceClass = sourceElementClass;

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

    public List<RElementValue> checkForErrors() {
        List<RElementValue> IncorrectFields = new ArrayList<RElementValue>();
        for (RElementValue rElementValue : Fields) {
            if (!rElementValue.valid())
                IncorrectFields.add(rElementValue);
        }
        Launcher.LOG.info(String.valueOf(IncorrectFields.size()));
        if (IncorrectFields.size() != 0) {
            return IncorrectFields;
        } else {
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var action = e.getActionCommand();
        if (action.equals("create")) {
            if (checkForErrors() == null) {
                try {
                    var workingClass = SourceClass.getConstructor().newInstance();
                    for (RElementValue rElementValue : Fields) {
                        SourceClass.getField(rElementValue.getTarget()).set(workingClass, rElementValue.getValue());
                    }
                    JOptionPane.showMessageDialog(this, "sigmaaaaa wezzy\n" + new Gson().toJson(workingClass), "Element Creation Error",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                var builder = new StringBuilder("<html>There were error(s) while creating this element: <br><ul>");
                for (RElementValue EV : Fields) {
                    builder.append("<li>" + EV.getTarget() + ": " + EV.Problem + "</li>");
                }

                JOptionPane.showMessageDialog(this, builder.toString(), "Element Creation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (action == "draft") {

        } else if (action == "cancel") {

        } else {
            var ex = new Exception("That button dont exist! man i forgot how good dark tranquility is");
            ErrorShower.showError((Component) this,
                    "woah mate, button dont fit, dont fit, button, it dont fit, wont fit", "I did an oppsie", ex);

            throw new IllegalAccessError();
        }
    }
}
