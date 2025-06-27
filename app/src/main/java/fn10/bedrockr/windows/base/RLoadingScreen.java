package fn10.bedrockr.windows.base;

import javax.annotation.Nullable;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFonts;

import java.awt.*;

public class RLoadingScreen extends RDialog {

    protected JProgressBar MainBar = new JProgressBar();
    protected JLabel MainText = new JLabel("Loading...");;

    public Integer Steps = null;

    public void changeText(String text) {
        SwingUtilities.invokeLater(() -> {
            MainText.setText(text);
        });
    }

    public void increaseProgress(int increase,@Nullable String TextChange) {

        changeText(TextChange);
        SwingUtilities.invokeLater(() -> {
            MainBar.setValue(MainBar.getValue() + increase);
        });
    }

    // completly gets rid of window
    public void destroy() {
        dispose();
    }

    public void increaseProgressBySteps(@Nullable String TextChange) throws IllegalAccessException {
        if (Steps == null)
            throw new IllegalArgumentException("Total steps not set yet.");

        changeText(TextChange);

        SwingUtilities.invokeLater(() -> {
            MainBar.setValue(MainBar.getValue() + 100 / Steps);
        });
    }

    public RLoadingScreen(Frame Parent) {
        super(Parent, DO_NOTHING_ON_CLOSE, "Loading", new Dimension(600, 150));

        // setUndecorated(true);

        int titleImgW = 374;
        int titleImageH = 74;

        var BG = new JLabel(ImageUtilites.ResizeImageByURL(getClass().getResource("/ui/BG.png"), 600, 600));

        var Branding = new JLabel(ImageUtilites
                .ResizeImageByURL(getClass().getResource("/ui/BrandingFullWShadow.png"), titleImgW, titleImageH));
        Branding.setPreferredSize(new Dimension(titleImgW, titleImageH));

        
        MainBar.setPreferredSize(new Dimension(600, 20));
        MainBar.setBackground(getForeground());
        MainBar.setOrientation(JProgressBar.HORIZONTAL);
        // MainBar.setForeground(new Color(36, 252, 3));
        MainBar.setBorder(new LineBorder(getForeground(),3));

        MainBar.setValue(0);
        MainBar.setIndeterminate(true);
        // MainBar.setStringPainted(true);
        // MainBar.setString("Loading...");

        
        MainText.setFont(RFonts.RegMinecraftFont.deriveFont(2, 18));
        MainText.setForeground(Color.WHITE);

        // bg
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, BG, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, BG, 0, SpringLayout.VERTICAL_CENTER, this);
        // branding
        Lay.putConstraint(SpringLayout.WEST, Branding, 10, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.NORTH, Branding, 10, SpringLayout.NORTH, this);
        // progress bar
        Lay.putConstraint(SpringLayout.SOUTH, MainBar, -30, SpringLayout.SOUTH, this);
        // progress text
        Lay.putConstraint(SpringLayout.SOUTH, MainText, 5, SpringLayout.NORTH, MainBar);
        Lay.putConstraint(SpringLayout.WEST, MainText, 10, SpringLayout.WEST, this);

        add(Branding);
        add(MainText);
        add(MainBar);

        add(BG); // always add last!
    }
}
