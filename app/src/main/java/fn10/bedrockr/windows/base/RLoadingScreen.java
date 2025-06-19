package fn10.bedrockr.windows.base;

import javax.swing.JLabel;
import javax.swing.SpringLayout;
import fn10.bedrockr.windows.utils.ImageUtilites;

import java.awt.*;

public class RLoadingScreen extends RDialog {

    public RLoadingScreen(Frame Parent) {
        super(Parent, DO_NOTHING_ON_CLOSE, "Loading", new Dimension(600, 150));
        
        //setUndecorated(true);

        int titleImgW = 374;
        int titleImageH = 80;

        var BG = new JLabel(ImageUtilites.ResizeImageByURL(getClass().getResource("/branding/BG.png"), 600, 600));

        var Branding = new JLabel(ImageUtilites
                .ResizeImageByURL(getClass().getResource("/branding/BrandingFullWShadow.png"), titleImgW, titleImageH));
        Branding.setPreferredSize(new Dimension(titleImgW,titleImageH));

        var progress 

        //bg
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, BG, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, BG, 0, SpringLayout.VERTICAL_CENTER, this);
        //branding
        Lay.putConstraint(SpringLayout.WEST, Branding, 15, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.NORTH, Branding, 15, SpringLayout.NORTH, this);

        add(Branding);
        add(BG);
    }
}
