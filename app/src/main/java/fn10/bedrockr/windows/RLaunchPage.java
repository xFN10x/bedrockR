package fn10.bedrockr.windows;

import javax.swing.*;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.ColorFunctions;

import fn10.bedrockr.bedrockRLauncher;
import fn10.bedrockr.windows.utils.Greetings;
import fn10.bedrockr.windows.utils.RFonts;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

public class RLaunchPage extends RFrame implements ActionListener, ItemListener {


    public RLaunchPage(Dimension Size) {
        super(
                EXIT_ON_CLOSE,
                "Welcome back!",
                Size,
                false);

        // Add things to the window.
        var greetingtext = Greetings.GetGreeting();
        var greeting = new JLabel(greetingtext.Text);
        greeting.setFont(RFonts.RegMinecraftFont.deriveFont(2, greetingtext.Size));
        greeting.setSize(Size.width, 100);
        greeting.setHorizontalTextPosition(SwingConstants.LEFT);

        var seperater = new JSeparator(JSeparator.HORIZONTAL);
        seperater.setPreferredSize(new Dimension(400, 3));

        var othergreeting = new JLabel(
                "Welcome back to bedrockR! Below are your current addons. Have none? Create a new one.");
        othergreeting.setFont(RFonts.RegMinecraftFont.deriveFont(1, 9));

        var projectspart = new JScrollPane();
        projectspart.setPreferredSize(new Dimension(540, 200));
        projectspart.setBackground(ColorFunctions.darken(new Color(30, 30, 30),0.01f));
        projectspart.putClientProperty( FlatClientProperties.STYLE, "arc: 28" );

        var menuBar = new JMenuBar();
        var addonsMenu = new JMenu("Addons");
       // newprojectbutton.addActionListener(this);
       var newaddonButton = new JMenuItem("New Addon", KeyEvent.VK_N);
       newaddonButton.addActionListener(this);

       addonsMenu.add(newaddonButton);

        menuBar.add(addonsMenu);

        // seperater
        Lay.putConstraint(SpringLayout.NORTH, seperater, 10, SpringLayout.SOUTH, greeting);
        // greeting
        Lay.putConstraint(SpringLayout.WEST, greeting, 30, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.NORTH, greeting, 30, SpringLayout.NORTH, this);
        // other greeting
        Lay.putConstraint(SpringLayout.NORTH, othergreeting, 10, SpringLayout.SOUTH, seperater);
        Lay.putConstraint(SpringLayout.WEST, othergreeting, 30, SpringLayout.WEST, this);
        //projects part
        Lay.putConstraint(SpringLayout.SOUTH, projectspart,-100, SpringLayout.SOUTH, this);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, projectspart,0, SpringLayout.HORIZONTAL_CENTER, this);

        setJMenuBar(menuBar);
        add(greeting);
        add(othergreeting);
        add(projectspart);
        add(seperater);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand() == "New Addon") {
            //TODO: add new addon handling
            throw new UnsupportedOperationException("need to work on this");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent arg0) {
        System.out.println("test");
    }

}
