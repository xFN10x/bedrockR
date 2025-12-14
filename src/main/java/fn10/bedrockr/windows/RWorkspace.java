package fn10.bedrockr.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Dialog.ModalExclusionType;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;
import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceResourceElement;
import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.GlobalBuildingVariables;
import fn10.bedrockr.addons.source.elementFiles.ResourceFile;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.addons.source.supporting.item.ReturnItemInfo;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.SettingsFile;
import fn10.bedrockr.windows.base.RFrame;
import fn10.bedrockr.windows.componets.RElement;
import fn10.bedrockr.windows.componets.RElementFile;
import fn10.bedrockr.windows.util.ErrorShower;
import fn10.bedrockr.windows.util.ImageUtilites;
import fn10.bedrockr.windows.util.WrapLayout;

public class RWorkspace extends RFrame implements ActionListener, ElementCreationListener {

    protected Container CP = getContentPane();
    public SourceWorkspaceFile SWPF;

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
    private JButton HelpWikiButton = new JButton(
            new ImageIcon(getClass().getResource("/addons/workspace/Help.png")));

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu helpMenu = new JMenu("Help");

    public RWorkspace(SourceWorkspaceFile WPF) {
        super(
                DO_NOTHING_ON_CLOSE,
                ((WorkspaceFile) WPF.getSerilized()).WorkspaceName,
                Toolkit.getDefaultToolkit().getScreenSize(),
                true,
                false);
        setExtendedState(MAXIMIZED_BOTH);
        ResourceFile.ActiveWorkspace = this;

        this.SWPF = WPF;
        WrapLayout InnerLayout1 = new WrapLayout(FlowLayout.CENTER);
        BoxLayout InnerLayout2 = new BoxLayout(ResourceInnerPanelView, BoxLayout.Y_AXIS);

        Tabs.addTab("Elements", ElementView);
        Tabs.addTab("Resources", ResourceView);
        // Tabs.addTab("Settings", null);
        Tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // #region menu bar
        setJMenuBar(menuBar);
        Desktop desk = Desktop.getDesktop();

        fileMenu.add("Change/Add MC Sync").addActionListener(ac -> {
            try {
                showMCSyncPopup(this, WPF);
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        fileMenu.add("Open Workspace folder").addActionListener(ac -> {
            try {
                desk.open(RFileOperations.getWorkspace(WPF.workspaceName()));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        fileMenu.add("Open built RP Folder").addActionListener(ac -> {
            try {
                desk.open(RFileOperations.getBaseDirectory("build", "RP", WPF.workspaceName()));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        fileMenu.add("Open built BP Folder").addActionListener(ac -> {
            try {
                desk.open(RFileOperations.getBaseDirectory( "build", "BP", WPF.workspaceName()));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        helpMenu.add("bedrockR Wiki").addActionListener(ac -> {
            try {
                desk.browse(new URI("https://github.com/xFN10x/bedrockR/wiki"));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        helpMenu.add("bedrockR Github").addActionListener(ac -> {
            try {
                desk.browse(new URI("https://github.com/xFN10x/bedrockR"));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        helpMenu.add("bedrockR on Summer Of Making").addActionListener(ac -> {
            try {
                desk.browse(new URI("https://summer.hackclub.com/projects/703"));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        helpMenu.add("bedrockR on Siege").addActionListener(ac -> {
            try {
                desk.browse(new URI("https://siege.hackclub.com/armory/1948"));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        helpMenu.add("Open bedrockR Directory").addActionListener(ac -> {
            try {
                desk.open(RFileOperations.getBaseDirectory());
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        // #endregion
        FlatLineBorder viewsBorder = new FlatLineBorder(new Insets(2, 2, 2, 2), Color.white, 1, 8);

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

        HelpWikiButton.addActionListener(action -> {
            try {
                Desktop.getDesktop()
                        .browse(URI.create("https://github.com/xFN10x/bedrockR/wiki"));

            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });

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
        // help button
        Lay.putConstraint(SpringLayout.SOUTH, HelpWikiButton, 0, SpringLayout.NORTH, Tabs);
        Lay.putConstraint(SpringLayout.EAST, HelpWikiButton, -10, SpringLayout.WEST, ReBuildElements);

        ElementInnerPanelView.setLayout(InnerLayout1);
        ElementInnerPanelView.setMaximumSize(new Dimension(400, 0));
        ElementInnerPanelView.setPreferredSize(new Dimension(400, 0));
        ResourceInnerPanelView.setLayout(InnerLayout2);

        ResourceView.getVerticalScrollBar().setUnitIncrement(18);
        ElementView.getVerticalScrollBar().setUnitIncrement(18);

        add(Tabs);
        add(VerticleSep);

        add(AddElement);
        add(AddTextureResource);
        add(LaunchMC);
        add(BuildElements);
        add(ReBuildElements);
        add(HelpWikiButton);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                var lp = new RLaunchPage(Launcher.LAUNCH_WINDOW_SIZE);
                lp.setVisible(true);
                dispose();
            }
        });

        pack();

        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        refreshAll();
    }

    public void buildElements(boolean rebuild) {
        // make loading screen
        RLoadingScreen progress = new RLoadingScreen(this);
        String BPdir = Path.of(RFileOperations.getBaseDirectory().getPath(), "build", "BP",
                SWPF.getSerilized().getElementName()).toString();
        String RPdir = Path.of(RFileOperations.getBaseDirectory().getPath(), "build", "RP",
                SWPF.getSerilized().getElementName()).toString();

        SwingUtilities.invokeLater(() -> {
            progress.setVisible(true);
        });

        // make a thread so the ui can update
        new Thread(() -> {
            try {
                if (rebuild) {
                    progress.changeText("Removing old files...");
                    FileUtils.deleteDirectory(new File(BPdir));
                    FileUtils.deleteDirectory(new File(RPdir));
                }
                refreshAll();
                GlobalBuildingVariables GlobalResVars = new GlobalBuildingVariables((WorkspaceFile) SWPF.getSerilized(),
                        RFileOperations.getResources(SWPF.workspaceName()).Serilized);
                List<ElementFile<?>> ToBuild = List
                        .of(RFileOperations.getElementsFromWorkspace(SWPF.workspaceName()));

                progress.Steps = ToBuild.size() + 1;

                SWPF.getSerilized().reset(BPdir);

                // build rest
                for (ElementFile<?> elementFile : ToBuild) {
                    if (elementFile.getDraft())
                        continue;
                    // build a element, then incrament the counter
                    progress.changeText("Building " + elementFile.getElementName()); // change text
                    elementFile.build(BPdir,
                            SWPF.getSerilized(), RPdir, GlobalResVars); // build
                    progress.increaseProgressBySteps("Done!"); // next
                }
                // build RP
                // build global res vars
                progress.changeText("Building resources... "); // change text
                GlobalResVars.build(BPdir,
                        SWPF.getSerilized(), RPdir, GlobalResVars);
                // build workspace
                progress.changeText("Building workspace..."); // change text
                SWPF.getSerilized().build(BPdir,
                        SWPF.getSerilized(), RPdir, GlobalResVars); // build

                progress.increaseProgressBySteps("Done!"); // next
                // do mc sync
                if (SWPF.getSerilized().MinecraftSync) {
                    progress.increaseProgressBySteps("Syncing..."); // next
                    RFileOperations.mcSync(this);
                }

            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(this, "Failed to build element.", "Building Error", e);
            } finally {
                SwingUtilities.invokeLater(progress::dispose);
            }
        }).start();
    }

    public void refreshAll() {
        refreshResources();
        refreshElements();
    }

    public void refreshResources() {
        SwingUtilities.invokeLater(() -> {
            SourceResourceElement resFile = RFileOperations.getResources(SWPF.workspaceName());
            ResourceInnerPanelView.removeAll();
            for (Map.Entry<String, String> entry : resFile.Serilized.ResourceIDs.entrySet()) {
                try {
                    RElement ToAdd = new RElement(null, null);
                    ToAdd.setMaximumSize(new Dimension(1400, 80));
                    ToAdd.setPreferredSize(new Dimension(1400, 80));
                    ToAdd.Name.setText(entry.getKey());
                    ToAdd.Desc.setText(entry.getValue());
                    ToAdd.CanBeSelected = false;

                    File file = resFile.Serilized.getFileOfResource(this, SWPF.workspaceName(), entry.getKey(),
                            resFile.Serilized.ResourceTypes.get(entry.getKey()));
                    ImageIcon icon = new ImageIcon(Files.readAllBytes(file.toPath()));

                    JPopupMenu popup = new JPopupMenu();
                    popup.add("Open with...").addActionListener(ac -> {
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (Exception e) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                        }
                    });

                    popup.add("Open Resource Directory").addActionListener(ac -> {
                        try {
                            Desktop.getDesktop().browse(new URI(file.toURI().toString().replace(file.getName(), "")));
                        } catch (Exception e) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                        }
                    });

                    popup.add("Resize").addActionListener(ac -> {
                        String choice = JOptionPane.showInputDialog(this,
                                "What size do you want this image to be?",
                                "Resize " + entry.getKey(), JOptionPane.QUESTION_MESSAGE,
                                null, new String[] {
                                        "16x16",
                                        "32x32",
                                        "64x64",
                                        "96x96",
                                        "128x128",
                                        "256x256",
                                        "512x512",
                                },
                                "16x16").toString();

                        if (choice == null)
                            return;

                        try {
                            Dimension di = new Dimension(Integer.parseInt(choice.split("x")[0]),
                                    Integer.parseInt(choice.split("x")[1]));
                            String old = icon.getImage().getWidth(null) + "x" + icon.getImage().getHeight(null);

                            Image resized = ImageUtilites.ResizeImage(ImageIO.read(file), di,
                                    Image.SCALE_AREA_AVERAGING);

                            BufferedImage buff = new BufferedImage(di.width, di.height,
                                    BufferedImage.TYPE_INT_ARGB);

                            Graphics2D grah = buff.createGraphics();
                            grah.drawImage(
                                    resized, 0, 0,
                                    null);
                            file.delete();
                            ImageIO.write(buff, "png", file);
                            buff.getGraphics().dispose();

                            JOptionPane.showMessageDialog(this, "Resized image from " + old + ", to " + choice);
                            this.refreshResources();
                        } catch (Exception e) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                            ErrorShower.showError(this, "Failed to resize image.", e);
                        }
                    });
                    popup.add("Delete").addActionListener(ac -> {
                        try {
                            resFile.Serilized.ResourceIDs.remove(entry.getKey());
                            resFile.Serilized.ResourceTypes.remove(entry.getKey());
                            file.delete();
                            this.refreshAll();
                            ResourceInnerPanelView.repaint();
                            ResourceView.repaint();
                            resFile.Serilized.build(SWPF.workspaceName(), null, null, null);
                        } catch (Exception e) {
                            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                        }
                    });

                    ToAdd.setComponentPopupMenu(popup);

                    ToAdd.Icon.setIcon(
                            ImageUtilites.ResizeIcon(icon, 70, 70));
                    ResourceInnerPanelView.add(ToAdd);

                    ResourceInnerPanelView.add(Box.createVerticalStrut(4));
                } catch (Exception e) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                }
            }
        });
    }

    public void refreshElements() {
        SwingUtilities.invokeLater(() -> {
            ElementInnerPanelView.removeAll();
            for (ElementFile<?> file : RFileOperations.getElementsFromWorkspace(SWPF.workspaceName())) {
                try {
                    ElementInnerPanelView
                            .add(new RElementFile(this, file, RFileOperations
                                    .getFileFromElementFile(SWPF.workspaceName(), file).toString()));

                } catch (Exception e) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
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
                var addFrame = new RNewElement(this, SWPF.getSerilized().WorkspaceName);
                addFrame.setVisible(true);
            });
        } else if (ac.equals("texture")) {
            String[] options = new String[] { "Cancel", "Item Texure", "Block Texture" };
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

                    RFileOperations.getResources(SWPF.getSerilized().WorkspaceName).Serilized
                            .importTexture(this, ResourceFile.ITEM_TEXTURE,
                                    SWPF.getSerilized().WorkspaceName);
                    break;
                case 2:

                    RFileOperations.getResources(SWPF.getSerilized().WorkspaceName).Serilized
                            .importTexture(this, ResourceFile.BLOCK_TEXTURE,
                                    SWPF.getSerilized().WorkspaceName);
                    break;
                default:
                    break;
            }
        } else if (ac.equals("build")) {
            buildElements(false);
        } else if (ac.equals("rebuild")) {
            buildElements(true);
        } else if (ac.equals("launch")) {
            if (SettingsFile.load().comMojangPath != null) {
                if (!new File(SettingsFile.load().comMojangPath).exists()) {
                    JOptionPane.showMessageDialog(this, "You... cant launch minecraft without it installed...",
                            "Minecraft not installed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            try {
                Desktop.getDesktop().browse(new URI("minecraft:///"));
            } catch (Exception e) {
                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                ErrorShower.showError(this, "Failed to open Minecraft", e.getMessage(), e);
            }
        }

    }

    @Override
    public void onElementDraft(ElementSource<?> element) {
        element.buildJSONFile((SWPF.getSerilized().WorkspaceName));
        refreshAll();
    }

    @Override
    public void onElementCancel() {
        return;
    }

    @Override
    public void onElementCreate(ElementSource<?> element) {
        element.buildJSONFile((SWPF.getSerilized().WorkspaceName));
        refreshAll();
    }

    /**
     * Shows a popup asking the user to enable Minecraft Sync, overriding it if they
     * already have it.
     * 
     * @param doingThis - the window to parent too
     * @param WPF       - the workspace that is having mc sync enabled
     */
    public static void showMCSyncPopup(Component doingThis, SourceWorkspaceFile WPF) {
        ((WorkspaceFile) WPF.getSerilized()).MinecraftSync = true; // enable
        WPF.buildJSONFile( // rebuild
                ((WorkspaceFile) WPF.getSerilized()).WorkspaceName);

        String[] platforms = { "Windows (pre-1.21.120)", "Windows" };
        var platformSelection = JOptionPane.showOptionDialog(
                doingThis,
                "To use MC Sync, bedrockR needs to be synced to Minecraft's files. Which platform are you on?",
                "Platform Selection",
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                platforms,
                "Windows");

        var settings = SettingsFile.load();
        switch (platformSelection) {
            case 0:

                settings.comMojangPath = System.getenv("LOCALAPPDATA")
                        + "\\Packages\\Microsoft.MinecraftUWP_8wekyb3d8bbwe\\LocalState\\games\\com.mojang";
                if (!new File(settings.comMojangPath).exists()) {
                    JOptionPane.showMessageDialog(doingThis,
                            "Couldn't find com.mojang (pre-1.21.120 / windows). Did you pick the right version? Do you have Minecraft Bedrock installed? \n Retry by selecting the option in the File menu.",
                            "oops", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                settings.save();

                JOptionPane.showMessageDialog(doingThis,
                        "Minecraft Sync is now enabled. You only need to reload your world to test now! (after you build of course!)",
                        "yippe", JOptionPane.INFORMATION_MESSAGE);
            case 1:

                settings.comMojangPath = System.getenv("APPDATA")
                        + "\\Minecraft Bedrock\\Users\\Shared\\games\\com.mojang";
                if (!new File(settings.comMojangPath).exists()) {
                    JOptionPane.showMessageDialog(doingThis,
                            "Couldn't find com.mojang (after 1.21.120 / windows). Did you pick the right version? Do you have Minecraft Bedrock installed? \\n"
                                    + //
                                    " Retry by selecting the option in the File menu.",
                            "oops", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                settings.save();

                JOptionPane.showMessageDialog(doingThis,
                        "Minecraft Sync is now enabled. You only need to reload your world to test now! (after you build of course!)",
                        "yippe", JOptionPane.INFORMATION_MESSAGE);

            default:
                break;
        }
    }

    /**
     * Opens a workspace, showing the window, and doing all other nessesary steps
     * too have a person be able to work on one
     * 
     * @param doingThis - the window to parent to, and hide once the window appears
     * @param WPF       - The workspace to open
     */
    public static void openWorkspace(Window doingThis, SourceWorkspaceFile WPF) {
        new Thread(() -> {
            RWorkspace workspaceView = new RWorkspace(WPF);

            RLoadingScreen loading = new RLoadingScreen(doingThis);
            loading.setAlwaysOnTop(true);
            SwingUtilities.invokeLater(() -> {
                loading.setVisible(true);
            });

            ReturnItemInfo.downloadVanillaItems();
            ReturnItemInfo.downloadVanillaBlocks();

            // get items ready for use
            SwingUtilities.invokeLater(() -> {
                loading.setVisible(false);
                if (doingThis != null)
                    doingThis.dispose();
                workspaceView.setVisible(true);
                if (!((WorkspaceFile) WPF.getSerilized()).MinecraftSync) { // ask to enable mc sync if not enabled
                    var ask = JOptionPane.showConfirmDialog(doingThis,
                            "This project does not currently have Minecraft Sync enabled. Minecraft Sync automaticly copies your built project files into com.mojang, so you can build, and playtest without needing to restart the game. Enable MC Sync?",
                            "Enable MC Sync", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (ask == JOptionPane.YES_OPTION) {
                        showMCSyncPopup(doingThis, WPF);
                    }
                } else if (!new File(SettingsFile.load().comMojangPath).exists()) {
                    var ask = JOptionPane.showConfirmDialog(doingThis,
                            "com.mojang is gone now. Either you uninstalled minecraft, or this is a compatibility issue. Please report this on github.\n\n Do you want to re-enabled MC Sync?",
                            "Re-Enable MC Sync", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (ask == JOptionPane.YES_OPTION) {
                        showMCSyncPopup(doingThis, WPF);
                    } else {
                        ((WorkspaceFile) WPF.getSerilized()).MinecraftSync = false;
                    }
                }
                RFileOperations.setCurrentWorkspace(WPF);
            });
        }).start();
    }
}
