package fn10.bedrockr.windows;

import javax.swing.*;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;

import fn10.bedrockr.utils.Greetings;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RFonts;
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
    public RLaunchPage(Dimension Size) {
        super(
                EXIT_ON_CLOSE,
                "Welcome back!",
                Size,
                false);

        // Add things to the window.
        Greeting greetingtext = Greetings.GetGreeting();
        JLabel greeting = new JLabel(greetingtext.Text);
        greeting.setFont(RFonts.RegMinecraftFont.deriveFont(2, greetingtext.Size));
        greeting.setSize(Size.width, 100);
        greeting.setHorizontalTextPosition(SwingConstants.LEFT);

        JSeparator seperater = new JSeparator(JSeparator.HORIZONTAL);
        seperater.setPreferredSize(new Dimension(400, 3));

        JLabel othergreeting = new JLabel(
                "Welcome back to bedrockR! Below are your current addons. Have none? Create a new one, and \nmaybe check out the wiki.");
        othergreeting.setFont(RFonts.RegMinecraftFont.deriveFont(1, 9));

        FlowLayout gride = new FlowLayout(1,8,6);

        Color BGC = ColorFunctions.darken(new Color(30, 30, 30), 0.01f);

       // var outerprojectpart = new JPanel();
        //outerprojectpart.setPreferredSize(new Dimension(540, 200));
        //outerprojectpart.setBackground(BGC);
        // outerprojectpart.putClientProperty(FlatClientProperties.STYLE, "arc: 40");
        //outerprojectpart.setBorder(new FlatLineBorder(new Insets(16, 16, 16, 16), Color.WHITE, 1, 16));

        JPanel ProjectsPart = new JPanel();
        ProjectsPart.setPreferredSize(new Dimension(540, 200));
        ProjectsPart.setBackground(BGC);
        ProjectsPart.setLayout(gride);
        ProjectsPart.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.WHITE, 1, 16));

        Desktop desk = Desktop.getDesktop();
        JMenuBar menuBar = new JMenuBar();
        JMenu addonsMenu = new JMenu("Addons");
        JMenu helpMenu = new JMenu("Help");
        // newprojectbutton.addActionListener(this);
        JMenuItem newaddonButton = new JMenuItem("New Addon", KeyEvent.VK_N);
        newaddonButton.addActionListener(this);
        JMenuItem somButton = new JMenuItem("bedrockR on Summer Of Making", KeyEvent.VK_S);
        somButton.addActionListener(ac -> {
            try {
                desk.browse(URI.create("https://summer.hackclub.com/projects/703"));
            } catch (IOException e) {

                e.printStackTrace();
            }
        });
        JMenuItem gitButton = new JMenuItem("bedrockR on Github", KeyEvent.VK_G);
        gitButton.addActionListener(ac -> {
            try {
                desk.browse(URI.create("https://github.com/xFN10x/bedrockR"));
            } catch (IOException e) {

                e.printStackTrace();
            }
        });
        JMenuItem helpButton = new JMenuItem("bedrockR Wiki", KeyEvent.VK_W);
        helpButton.addActionListener(ac -> {
            try {
                desk.browse(URI.create("https://github.com/xFN10x/bedrockR/wiki"));
            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        addonsMenu.add(newaddonButton);

        helpMenu.add(helpButton);
        helpMenu.add(gitButton);
        helpMenu.add(somButton);

        menuBar.add(addonsMenu);
        menuBar.add(helpMenu);

        if (RFileOperations.getWorkspaces(this) != null)
        for (var folder : RFileOperations.getWorkspaces(this)) {
            ProjectsPart.add(new RAddon(folder, this));
        }

        gride.layoutContainer(ProjectsPart);
        // seperater
        Lay.putConstraint(SpringLayout.NORTH, seperater, 10, SpringLayout.SOUTH, greeting);
        // greeting
        Lay.putConstraint(SpringLayout.WEST, greeting, 30, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.NORTH, greeting, 30, SpringLayout.NORTH, this);
        // other greeting
        Lay.putConstraint(SpringLayout.NORTH, othergreeting, 10, SpringLayout.SOUTH, seperater);
        Lay.putConstraint(SpringLayout.WEST, othergreeting, 30, SpringLayout.WEST, this);
        // projects part
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, ProjectsPart, 0, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, ProjectsPart, -7, SpringLayout.HORIZONTAL_CENTER, this);

        setJMenuBar(menuBar);
        add(greeting);
        add(othergreeting);
        add(ProjectsPart);

        add(seperater);
        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand() == "New Addon") {
            SwingUtilities.invokeLater(() -> {
                var newAddonPage = new RNewAddon(this);
                newAddonPage.setVisible(true);
            });
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        throw new UnsupportedOperationException("Unimplemented method 'itemStateChanged'");
    }

}
