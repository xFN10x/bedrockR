package fn10.bedrockr.windows;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.w3c.dom.css.ElementCSSInlineStyle;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.windows.base.RFrame;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class RWorkspace extends RFrame implements ActionListener,ElementCreationListener {

    protected Container CP = getContentPane();

    // componates
    private JSeparator VerticleSep = new JSeparator(JSeparator.VERTICAL);

    private JTabbedPane Tabs = new JTabbedPane();
    private JPanel ElementView = new JPanel();

    private JButton AddElement = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/NewElement.png")));

    public RWorkspace(SourceWPFile WPF) {
        super(
                EXIT_ON_CLOSE,
                ((WPFile) WPF.getSerilized()).WorkspaceName,
                Toolkit.getDefaultToolkit().getScreenSize(),
                true,
                false);
        setExtendedState(MAXIMIZED_BOTH);

        Tabs.addTab("Elements", null);
        Tabs.addTab("Resources", null);
        Tabs.addTab("Settings", null);
        Tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        ElementView.setBorder(new FlatLineBorder(new Insets(2, 2, 2, 2), Color.white,1,16));

        AddElement.setActionCommand("add");
        AddElement.addActionListener(this);

        // constraints
        // tabedpane
        Lay.putConstraint(SpringLayout.EAST, Tabs, -30, SpringLayout.EAST, CP);
        Lay.putConstraint(SpringLayout.WEST, Tabs, 100, SpringLayout.WEST, CP);
        Lay.putConstraint(SpringLayout.NORTH, Tabs, 70, SpringLayout.NORTH, CP);
        // verticle seperator
        Lay.putConstraint(SpringLayout.NORTH, VerticleSep, 70, SpringLayout.NORTH, CP);
        Lay.putConstraint(SpringLayout.SOUTH, VerticleSep, -10, SpringLayout.SOUTH, CP);
        Lay.putConstraint(SpringLayout.EAST, VerticleSep, -10, SpringLayout.WEST, Tabs);
        //add button
        Lay.putConstraint(SpringLayout.NORTH, AddElement, 70, SpringLayout.NORTH, CP);
        Lay.putConstraint(SpringLayout.EAST, AddElement, -15, SpringLayout.WEST, VerticleSep);
        // element view
        Lay.putConstraint(SpringLayout.EAST, ElementView, -30, SpringLayout.EAST, CP);
        Lay.putConstraint(SpringLayout.WEST, ElementView, 100, SpringLayout.WEST, CP);
        Lay.putConstraint(SpringLayout.NORTH, ElementView, 10, SpringLayout.SOUTH, Tabs);
        Lay.putConstraint(SpringLayout.SOUTH, ElementView, -10, SpringLayout.SOUTH, CP);

        add(Tabs);
        add(VerticleSep);
        add(AddElement);

        add(ElementView);

        pack();

        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
    }

    private void update() { 
        SwingUtilities.invokeLater(() -> {

        });
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand() == "add") {
            SwingUtilities.invokeLater(() -> {
                var addFrame = new RNewSelector(this);
                addFrame.setVisible(true);
            });
        }
    }

    @Override
    public void onElementCreate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onElementCreate'");
    }

    @Override
    public void onElementDraft() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onElementDraft'");
    }

    @Override
    public void onElementCancel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onElementCancel'");
    }
}
