package fn10.bedrockr.windows;

import javax.swing.*;
import com.formdev.flatlaf.ui.FlatLineBorder;
import fn10.bedrockr.rendering.BlockTextures;
import fn10.bedrockr.utils.Greetings;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RFonts;
import fn10.bedrockr.utils.WrapLayout;
import fn10.bedrockr.utils.Greetings.Greeting;
import fn10.bedrockr.windows.base.RFrame;
import fn10.bedrockr.windows.componets.RAddon;

import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;

public class RLaunchPage extends RFrame implements ActionListener, ItemListener {
    private JPanel ProjectsPart = new JPanel();
    private JScrollPane ProjectsScrollPart = new JScrollPane(ProjectsPart, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private Greeting greeting = Greetings.GetGreeting();
    private JLabel greetingText = new JLabel(greeting.Text);

    private JSeparator seperater = new JSeparator(JSeparator.HORIZONTAL);

    private JLabel othergreeting = new JLabel(
            "<html>Welcome back to bedrockR! Below are your current addons. Have none? Create a new one, and <br>maybe check out the wiki.</html>");
    private WrapLayout InnerLay = new WrapLayout(1, 8, 6);

    private JMenuBar menuBar = new JMenuBar();

    private JMenu fileMenu = new JMenu("File");
    private JMenu addonsMenu = new JMenu("Addons");
    private JMenu helpMenu = new JMenu("Help");

    private Desktop desk = Desktop.getDesktop();
    private JMenuItem newaddonButton = new JMenuItem("New Addon", KeyEvent.VK_N);
    private JMenuItem siegeButton = new JMenuItem("bedrockR on Siege", KeyEvent.VK_I);
    private JMenuItem gitButton = new JMenuItem("bedrockR on Github", KeyEvent.VK_G);
    private JMenuItem somButton = new JMenuItem("bedrockR on Summer Of Making", KeyEvent.VK_S);
    private JMenuItem helpButton = new JMenuItem("bedrockR Wiki", KeyEvent.VK_W);

    public RLaunchPage(Dimension Size) {
        super(
                EXIT_ON_CLOSE,
                "Welcome back!",
                Size,
                false);

        // Add things to the window.
        greetingText.setFont(RFonts.RegMinecraftFont.deriveFont(2, greeting.Size));
        greetingText.setSize(Size.width, 100);
        greetingText.setHorizontalTextPosition(SwingConstants.LEFT);

        seperater.setPreferredSize(new Dimension(400, 3));

        othergreeting.setFont(RFonts.RegMinecraftFont.deriveFont(1, 9));

        ProjectsScrollPart.setPreferredSize(new Dimension(540, 180));
        ProjectsPart.setBackground(Color.darkGray.darker());
        ProjectsPart.setLayout(InnerLay);
        ProjectsPart.setBorder(new FlatLineBorder(new Insets(2, 2, 2, 2), Color.WHITE, 0, 16));
        ProjectsScrollPart.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.WHITE, 1, 16));
        ProjectsScrollPart.getVerticalScrollBar().setUnitIncrement(16);

        newaddonButton.addActionListener(this);
        siegeButton.addActionListener(ac -> {
            try {
                desk.browse(URI.create("https://siege.hackclub.com/armory/1948"));
            } catch (IOException e) {

                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        somButton.addActionListener(ac -> {
            try {
                desk.browse(URI.create("https://summer.hackclub.com/projects/703"));
            } catch (IOException e) {

                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });

        gitButton.addActionListener(ac -> {
            try {
                desk.browse(URI.create("https://github.com/xFN10x/bedrockR"));
            } catch (IOException e) {

                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });
        helpButton.addActionListener(ac -> {
            try {
                desk.browse(URI.create("https://github.com/xFN10x/bedrockR/wiki"));
            } catch (IOException e) {

                fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        });

        addonsMenu.add(newaddonButton);

        helpMenu.add(helpButton);
        helpMenu.add(gitButton);
        helpMenu.add(somButton);
        helpMenu.add(siegeButton);

        fileMenu.add("Render Blocks").addActionListener(ac -> {
            BlockTextures.downloadAllBlockTextures(this);
        });

        menuBar.add(fileMenu);
        menuBar.add(addonsMenu);
        menuBar.add(helpMenu);

        // seperater
        Lay.putConstraint(SpringLayout.NORTH, seperater, 10, SpringLayout.SOUTH, greetingText);
        // greeting
        Lay.putConstraint(SpringLayout.WEST, greetingText, 30, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.NORTH, greetingText, 30, SpringLayout.NORTH, this);
        // other greeting
        Lay.putConstraint(SpringLayout.NORTH, othergreeting, 10, SpringLayout.SOUTH, seperater);
        Lay.putConstraint(SpringLayout.WEST, othergreeting, 30, SpringLayout.WEST, this);
        // projects part
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, ProjectsScrollPart, 0, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, ProjectsScrollPart, -7, SpringLayout.HORIZONTAL_CENTER, this);

        refresh();

        setJMenuBar(menuBar);
        add(greetingText);
        add(othergreeting);
        add(ProjectsScrollPart);

        add(seperater);
        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
    }

    public void refresh() {
        new Thread(() -> {
            ProjectsPart.removeAll();
            JButton ToAdd = new JButton(new ImageIcon(getClass().getResource("/addons/workspace/New.png")));
            ToAdd.setActionCommand("New Addon");
            ToAdd.addActionListener(this);
            ProjectsPart.add(ToAdd);
            if (RFileOperations.getWorkspaces(this) != null)
                for (var folder : RFileOperations.getWorkspaces(this)) {
                    SwingUtilities.invokeLater(() -> {
                        ProjectsPart.add(new RAddon(this, folder));
                        ProjectsPart.repaint();
                        ProjectsPart.revalidate();
                    });
                }

        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand() == "New Addon") {
            SwingUtilities.invokeLater(() -> {
                RNewAddon newAddonPage = new RNewAddon(this);
                newAddonPage.setVisible(true);
            });
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        throw new UnsupportedOperationException("Unimplemented method 'itemStateChanged'");
    }

}
