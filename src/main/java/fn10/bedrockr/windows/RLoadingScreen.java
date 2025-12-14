package fn10.bedrockr.windows;


import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import fn10.bedrockr.windows.base.RDialog;
import fn10.bedrockr.windows.util.ImageUtilites;
import fn10.bedrockr.windows.util.RFonts;
import jakarta.annotation.Nullable;

import java.awt.*;

public class RLoadingScreen extends RDialog {

    protected JProgressBar MainBar = new JProgressBar();
    protected JLabel MainText = new JLabel("Loading...");;
    protected double actualProgress = 0;

    public Integer Steps = null;

    public void changeText(String text) {
        try {
            SwingUtilities.invokeLater(() -> {
                MainText.setText(text);
            });
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().warning("Failed to change progress message.");
        }
    }

    public void increaseProgress(double increase, @Nullable String TextChange) {
        MainBar.setIndeterminate(false);
        changeText(TextChange);
        SwingUtilities.invokeLater(() -> {
            actualProgress += increase;
            MainBar.setValue((int)actualProgress);
        });
    }

    // completly gets rid of window
    public void destroy() {
        dispose();
    }

    public void increaseProgressBySteps(@Nullable String TextChange) throws IllegalAccessException {
        if (Steps == null)
            throw new IllegalArgumentException("Total steps not set yet.");
        if (MainBar.isIndeterminate())
            SwingUtilities.invokeLater(() -> {
                MainBar.setIndeterminate(false);
            });

        changeText(TextChange);

        SwingUtilities.invokeLater(() -> {
            actualProgress += (100 / (double)Steps);
            //System.out.println(actualProgress);
            MainBar.setValue((int)actualProgress);
        });
    }

    public RLoadingScreen(Window Parent) {
        super(Parent, DO_NOTHING_ON_CLOSE, "Loading", new Dimension(600, 150));

        // setUndecorated(true);

        int titleImgW = 374;
        int titleImageH = 80;

        var BG = new JLabel(ImageUtilites.ResizeImageByURL(getClass().getResource("/ui/BG.png"), 600, 600));

        var Branding = new JLabel(ImageUtilites
                .ResizeImageByURL(getClass().getResource("/ui/BrandingFullWShadow.png"), titleImgW, titleImageH));
        Branding.setPreferredSize(new Dimension(titleImgW, titleImageH));

        MainBar.setPreferredSize(new Dimension(600, 20));
        MainBar.setBackground(getForeground());
        MainBar.setOrientation(JProgressBar.HORIZONTAL);
        // MainBar.setForeground(new Color(36, 252, 3));
        MainBar.setBorder(new LineBorder(getForeground(), 3));

        MainBar.setValue(0);
        MainBar.setIndeterminate(true);
        // MainBar.setStringPainted(true);
        // MainBar.setString("Loading...");

        MainText.setFont(RFonts.RegMinecraftFont.deriveFont(2, 18));
        MainText.setForeground(Color.WHITE);

        // bg
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, BG, 0, SpringLayout.HORIZONTAL_CENTER, getContentPane());
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, BG, 0, SpringLayout.VERTICAL_CENTER, getContentPane());
        // branding
        Lay.putConstraint(SpringLayout.WEST, Branding, 10, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.NORTH, Branding, 10, SpringLayout.NORTH, this);
        // progress bar
        Lay.putConstraint(SpringLayout.WEST, MainBar, 0, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, MainBar, 0, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, MainBar, 0, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.NORTH, MainBar, -10, SpringLayout.SOUTH, getContentPane());
        // progress text
        Lay.putConstraint(SpringLayout.SOUTH, MainText, 5, SpringLayout.NORTH, MainBar);
        Lay.putConstraint(SpringLayout.WEST, MainText, 10, SpringLayout.WEST, getContentPane());

        add(Branding);
        add(MainText);
        add(MainBar);

        add(BG); // always add last!
    }
}
