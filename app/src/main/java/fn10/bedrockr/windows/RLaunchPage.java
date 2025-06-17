package fn10.bedrockr.windows;

import javax.swing.*;

import fn10.bedrockr.windows.utils.Greetings;
import fn10.bedrockr.windows.utils.RFonts;

import java.awt.*;
import java.util.Random;

public class RLaunchPage extends RFrame {

    public RLaunchPage(Dimension Size) {
        super(
            EXIT_ON_CLOSE,
             "bedrockR",
             Size,
             false);

        //Add things to the window.
        var greetingtext = Greetings.GetGreeting();
        var greeting = new JLabel(greetingtext.Text);
        greeting.setFont(RFonts.RegMinecraftFont.deriveFont(2,greetingtext.Size));
        greeting.setSize(Size.width, 100);
        greeting.setHorizontalTextPosition(SwingConstants.LEFT);
        
        Lay.putConstraint(SpringLayout.WEST, greeting, 30, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.NORTH, greeting, 30, SpringLayout.NORTH, this);

        add(greeting,SpringLayout.NORTH);
    }

    
}
