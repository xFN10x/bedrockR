package fn10.bedrockr.windows;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.html.parser.Element;

import org.apache.commons.io.FilenameUtils;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.google.gson.Gson;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.ElementFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.base.RFrame;
import fn10.bedrockr.windows.componets.RElementFile;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class RWorkspace extends RFrame implements ActionListener, ElementCreationListener {

    protected Container CP = getContentPane();
    public SourceWPFile SWPF;

    // componates
    private JSeparator VerticleSep = new JSeparator(JSeparator.VERTICAL);

    private JTabbedPane Tabs = new JTabbedPane();
    private JPanel ElementView = new JPanel();
    private JPanel ResourceView = new JPanel();

    private JButton AddElement = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/NewElement.png")));

    public RWorkspace(SourceWPFile WPF) {
        super(
                EXIT_ON_CLOSE,
                ((WPFile) WPF.getSerilized()).WorkspaceName,
                Toolkit.getDefaultToolkit().getScreenSize(),
                true,
                false);
        setExtendedState(MAXIMIZED_BOTH);

        this.SWPF = WPF;
        var gride = new FlowLayout(1, 8, 6);

        Tabs.addTab("Elements", ElementView);
        Tabs.addTab("Resources", ResourceView);
        Tabs.addTab("Settings", null);
        Tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        var viewsBorder = new FlatLineBorder(new Insets(2, 2, 2, 2), Color.white, 1, 16);
        //Tabs.setBorder(viewsBorder);
        ElementView.setBorder(viewsBorder);
        ElementView.setVisible(false);
        ResourceView.setBorder(viewsBorder);
        ResourceView.setVisible(false);

        AddElement.setActionCommand("add");
        AddElement.addActionListener(this);

        // constraints
        // tabedpane
        Lay.putConstraint(SpringLayout.EAST, Tabs, -30, SpringLayout.EAST, CP);
        Lay.putConstraint(SpringLayout.WEST, Tabs, 100, SpringLayout.WEST, CP);
        Lay.putConstraint(SpringLayout.NORTH, Tabs, 70, SpringLayout.NORTH, CP);
        Lay.putConstraint(SpringLayout.SOUTH, Tabs, -10, SpringLayout.SOUTH, CP);
        // verticle seperator
        Lay.putConstraint(SpringLayout.NORTH, VerticleSep, 70, SpringLayout.NORTH, CP);
        Lay.putConstraint(SpringLayout.SOUTH, VerticleSep, -10, SpringLayout.SOUTH, CP);
        Lay.putConstraint(SpringLayout.EAST, VerticleSep, -10, SpringLayout.WEST, Tabs);
        // add button
        Lay.putConstraint(SpringLayout.NORTH, AddElement, 70, SpringLayout.NORTH, CP);
        Lay.putConstraint(SpringLayout.EAST, AddElement, -15, SpringLayout.WEST, VerticleSep);
        // views
        // Lay.putConstraint(SpringLayout.EAST, ElementView, -30, SpringLayout.EAST,
        // CP);
        /// Lay.putConstraint(SpringLayout.WEST, ElementView, 100, SpringLayout.WEST,
        // CP);
        // Lay.putConstraint(SpringLayout.NORTH, ElementView, 10, SpringLayout.SOUTH,
        // Tabs);
        // Lay.putConstraint(SpringLayout.SOUTH, ElementView, -10, SpringLayout.SOUTH,
        // CP);
        // Lay.putConstraint(SpringLayout.EAST, ResourceView, -30, SpringLayout.EAST,
        // CP);
        // Lay.putConstraint(SpringLayout.WEST, ResourceView, 100, SpringLayout.WEST,
        // CP);
        // Lay.putConstraint(SpringLayout.NORTH, ResourceView, 10, SpringLayout.SOUTH,
        // Tabs);
        // Lay.putConstraint(SpringLayout.SOUTH, ResourceView, -10, SpringLayout.SOUTH,
        // CP);

        ElementView.setLayout(gride);
        ResourceView.setLayout(gride);

        add(Tabs);
        add(VerticleSep);
        add(AddElement);

        // add(ElementView);
        // add(ResourceView);

        pack();

        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        update();
    }

    private void update() {
        SwingUtilities.invokeLater(() -> {
            ElementView.removeAll();
            for (File file : RFileOperations
                    .getFileFromWorkspace(this, ((WPFile) SWPF.getSerilized()).WorkspaceName, "/elements/")
                    .listFiles()) { // get all elements in workspace
                Launcher.LOG.info(file.getName());
                var ext = FilenameUtils.getExtension(file.getName());
                try {
                    if (ext.contains("ref")) { // if it is a file, do
                        file.setReadable(true);
                        Class<? extends ElementSource> clazz = RFileOperations.getElementClassFromFileExtension(this,
                                ext);
                        ElementSource newsrc = clazz.getConstructor(String.class)
                                .newInstance(Files.readString(file.toPath()));
                        ElementView.add(new RElementFile(this,(ElementFile) newsrc.getSerilized()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorShower.showError(this, "Failed to load element", "Element Error", e);
                    return;
                }
            }
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
    public void onElementDraft() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onElementDraft'");
    }

    @Override
    public void onElementCancel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onElementCancel'");
    }

    @Override
    public void onElementCreate(ElementSource element) {
        element.buildJSONFile(this, (((WPFile) SWPF.getSerilized()).WorkspaceName));
        update();
    }
}
