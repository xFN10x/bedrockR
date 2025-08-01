package fn10.bedrockr.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.jsonClasses.GlobalBuildingVaribles;
import fn10.bedrockr.addons.source.jsonClasses.ResourceFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.base.RFrame;
import fn10.bedrockr.windows.base.RLoadingScreen;
import fn10.bedrockr.windows.componets.RElementFile;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public class RWorkspace extends RFrame implements ActionListener, ElementCreationListener {

    protected Container CP = getContentPane();
    public SourceWPFile SWPF;

    // components
    private JSeparator VerticleSep = new JSeparator(JSeparator.VERTICAL);

    private JTabbedPane Tabs = new JTabbedPane();

    private JPanel ElementInnerPanelView = new JPanel();
    private JPanel ResourceInnerPanelView = new JPanel();
    private JScrollPane ElementView = new JScrollPane(ElementInnerPanelView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JScrollPane ResourceView = new JScrollPane(ResourceInnerPanelView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private JButton AddElement = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/NewElement.png")));
    private JButton AddTextureResource = new JButton(
            new ImageIcon(getClass().getResource("/addons/workspace/NewResource.png")));
    private JButton LaunchMC = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/LaunchMC.png")));
    private JButton BuildElements = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/Build.png")));
    private JButton ReBuildElements = new JButton(
            new ImageIcon(getClass().getResource("/addons/workspace/ReBuild.png")));

    public RWorkspace(SourceWPFile WPF) {
        super(
                DO_NOTHING_ON_CLOSE,
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

        var viewsBorder = new FlatLineBorder(new Insets(2, 2, 2, 2), Color.white, 1, 8);
        // Tabs.setBorder(viewsBorder);
        ElementView.setBorder(viewsBorder);
        ElementView.setVisible(false);
        ResourceView.setBorder(viewsBorder);
        ResourceView.setVisible(false);

        AddElement.setActionCommand("add");
        AddElement.addActionListener(this);

        AddTextureResource.setActionCommand("texture");
        AddTextureResource.addActionListener(this);

        BuildElements.setActionCommand("build");
        BuildElements.addActionListener(this);

        ReBuildElements.setActionCommand("rebuild");
        ReBuildElements.addActionListener(this);

        LaunchMC.setActionCommand("launch");
        LaunchMC.addActionListener(this);

        // constraints
        // tabbedpane
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
        // addtexture button
        Lay.putConstraint(SpringLayout.NORTH, AddTextureResource, 10, SpringLayout.SOUTH, AddElement);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, AddTextureResource, 0, SpringLayout.HORIZONTAL_CENTER,
                AddElement);
        // launch button
        Lay.putConstraint(SpringLayout.SOUTH, LaunchMC, 0, SpringLayout.NORTH, Tabs);
        Lay.putConstraint(SpringLayout.EAST, LaunchMC, -10, SpringLayout.EAST, CP);
        // build button
        Lay.putConstraint(SpringLayout.SOUTH, BuildElements, 0, SpringLayout.NORTH, Tabs);
        Lay.putConstraint(SpringLayout.EAST, BuildElements, -10, SpringLayout.WEST, LaunchMC);
        // rebuild button
        Lay.putConstraint(SpringLayout.SOUTH, ReBuildElements, 0, SpringLayout.NORTH, Tabs);
        Lay.putConstraint(SpringLayout.EAST, ReBuildElements, -10, SpringLayout.WEST, BuildElements);

        ElementInnerPanelView.setLayout(gride);
        ResourceInnerPanelView.setLayout(gride);

        add(Tabs);
        add(VerticleSep);

        add(AddElement);
        add(AddTextureResource);
        add(LaunchMC);
        add(BuildElements);
        add(ReBuildElements);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                var lp = new RLaunchPage(Launcher.LAUNCH_WINDOW_SIZE);
                lp.setVisible(true);
                dispose();
            }
        });

        pack();

        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        refreshElements();
    }

    public void buildElements(boolean rebuild) {
        // make loading screen
        var progress = new RLoadingScreen(this);
        var builddir = RFileOperations.getBaseDirectory(this).getPath() + File.separator + "build" + File.separator
                + "BP" + File.separator +
                SWPF.getSerilized().getElementName() + File.separator;
        var resdir = RFileOperations.getBaseDirectory(this).getPath() + File.separator + "build" + File.separator
                + "RP" + File.separator +
                SWPF.getSerilized().getElementName() + File.separator;
        SwingUtilities.invokeLater(() -> {
            progress.setVisible(true);
        });

        // make a thread so the ui can update
        new Thread(() -> {
            try {
                if (rebuild) {
                    progress.changeText("Removing old files...");
                    FileUtils.deleteDirectory(new File(builddir));
                }
                refreshElements();
                var GlobalResVars = new GlobalBuildingVaribles();
                var ToBuild = new ArrayList<RElementFile>();
                SwingUtilities.invokeAndWait(() -> {
                    for (Component comp : ElementInnerPanelView.getComponents()) {
                        if (!comp.getName().equals("RElementFile"))
                            continue;
                        var casted = ((RElementFile) comp);
                        ToBuild.add(casted);
                    }
                });
                progress.Steps = ToBuild.size() + 1;
                // buildBP
                // build workspace
                progress.changeText("Building workspace..."); // change text
                SWPF.getSerilized().build(builddir,
                        ((WPFile) SWPF.getSerilized()), resdir, GlobalResVars); // build
                progress.increaseProgressBySteps("Done!"); // next
                // build rest
                for (RElementFile rElementFile : ToBuild) {
                    if (rElementFile.getFile().getDraft())
                        continue;
                    // build a element, then incrament the counter
                    progress.changeText("Building " + rElementFile.getFile().getElementName()); // change text
                    rElementFile.getFile().build(builddir,
                            ((WPFile) SWPF.getSerilized()), resdir, GlobalResVars); // build
                    progress.increaseProgressBySteps("Done!"); // next
                }
                // build RP
                // build global res vars
                progress.changeText("Building resources... "); // change text
                GlobalResVars.build(builddir,
                        ((WPFile) SWPF.getSerilized()), resdir, GlobalResVars);

                // do mc sync
                if (((WPFile) SWPF.getSerilized()).MinecraftSync) {
                    progress.increaseProgressBySteps("Syncing..."); // next
                    RFileOperations.mcSync(this);
                }

            } catch (Exception e) {
                e.printStackTrace();
                ErrorShower.showError(this, "Failed to build element.", "Building Error", e);
            } finally {
                SwingUtilities.invokeLater(progress::dispose);
            }
        }).start();
    }

    public void refreshElements() {
        SwingUtilities.invokeLater(() -> {
            ElementInnerPanelView.removeAll();
            for (File file : RFileOperations
                    .getFileFromWorkspace(this, ((WPFile) SWPF.getSerilized()).WorkspaceName,
                            File.separator + "elements" + File.separator)
                    .listFiles()) { // get all elements in workspace
                // Launcher.LOG.info(file.getName());
                var ext = FilenameUtils.getExtension(file.getName());
                try {
                    if (ext.contains("ref")) { // if it is a file, do
                        file.setReadable(true);
                        Class<? extends ElementSource> clazz = RFileOperations.getElementClassFromFileExtension(this,
                                ext);
                        ElementSource newsrc = clazz.getConstructor(String.class)
                                .newInstance(Files.readString(file.toPath()));
                        ElementInnerPanelView
                                .add(new RElementFile(this, (ElementFile) newsrc.getSerilized(), file.getPath()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorShower.showError(this, "Failed to load element", "Element Error", e);
                    return;
                }
            }
            ElementInnerPanelView.updateUI();
        });
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        var ac = arg0.getActionCommand();
        if (ac.equals("add")) {
            SwingUtilities.invokeLater(() -> {
                var addFrame = new RNewElement(this, ((WPFile) SWPF.getSerilized()).WorkspaceName);
                addFrame.setVisible(true);
            });
        } else if (ac.equals("texture")) {
            String[] options = { "Cancel", "Item Texure" };
            var choice = JOptionPane.showOptionDialog(
                    this,
                    "What kind of texture would you like you add?",
                    "Add New Texture Resource",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0:

                    break;

                case 1:

                    RFileOperations.getResources(this, ((WPFile) SWPF.getSerilized()).WorkspaceName).Serilized
                            .importTexture(this, ResourceFile.ITEM_TEXTURE,
                                    ((WPFile) SWPF.getSerilized()).WorkspaceName);

                default:
                    break;
            }
        } else if (ac.equals("build")) {
            buildElements(false);

        } else if (ac.equals("rebuild")) {
            buildElements(true);
        } else if (ac.equals("launch")) {
            try {
                Desktop.getDesktop().browse(new URI("minecraft:///"));
            } catch (Exception e) {
                e.printStackTrace();
                ErrorShower.showError(this, "Failed to open Minecraft", e.getMessage(), e);
            }
        }

    }

    @Override
    public void onElementDraft(ElementSource element) {
        element.buildJSONFile(this, (((WPFile) SWPF.getSerilized()).WorkspaceName));
        refreshElements();
    }

    @Override
    public void onElementCancel() {
        return;
    }

    @Override
    public void onElementCreate(ElementSource element) {
        element.buildJSONFile(this, (((WPFile) SWPF.getSerilized()).WorkspaceName));
        refreshElements();
    }
}
