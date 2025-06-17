package fn10.bedrockr.windows;

import javax.swing.*;

import fn10.bedrockr.windows.laf.BedrockrDark;

import java.awt.*;
import java.util.Random;
import java.util.random.RandomGenerator;

public class RLaunchPage extends RFrame {

    private static String[] GREETINGS = {
        "Welcome back!",
        "Welcome to BR, how may I serve you today?",
        "Could I interest you in some text-based programming?",
        "Yo.",
        "Why does this not work now?",
        "bugrockR",
        "Waiting for custom biomes to be fi- Oh hi.",
        "Alpha = true",
        "CRITICAL ERROR! LEAVE NOWWWWW!!! (jk)",
        "no"
    };

    public RLaunchPage(Dimension Size) {
        super(
            EXIT_ON_CLOSE,
             "bedrockR",
             Size,
             false);

        //Add things to the window.
        var greeting = new JLabel();
    }

    public static String GetGreeting() {
        var mx = GREETINGS.length;
        var rnd = new Random();

        return GREETINGS[rnd.nextInt(0, mx)];
    }
}
