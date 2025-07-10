package fn10.bedrockr.windows.base;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.utils.ImageUtilites;

import java.awt.*;
import javax.swing.*;

public class RFrame extends JFrame {

    public SpringLayout Lay = new SpringLayout();

    public RFrame(int CloseOperation, String WindowTitle, Dimension Size, boolean Resizeable) {
        this(CloseOperation, WindowTitle, Size, Resizeable, !Resizeable);
    }

    public RFrame(int CloseOperation, String WindowTitle, Dimension Size, boolean Resizeable, boolean BottomBar) {
        super(WindowTitle + " - bedrockR");

        setIconImage(Launcher.ICON);
        //setIconImages(Launcher.ICONS);

        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(Color.GREEN);
        bottomBar.setPreferredSize(new Dimension(Size.width, 40));

        int titleImgW = 187;
        int titleImageH = 40;
        ImageIcon titleImgIcon = ImageUtilites
                .ResizeImageByURL(getClass().getResource("/ui/BrandingFullWShadow.png"), titleImgW, titleImageH); // Full
                                                                                                                        // Icon
        JLabel titleImg = new JLabel(titleImgIcon);
        titleImg.setSize(new Dimension(titleImgW, titleImageH));
        titleImg.setHorizontalAlignment(SwingConstants.LEFT);
        titleImg.setVerticalAlignment(SwingConstants.CENTER);

        bottomBar.setLayout(new SpringLayout());
        bottomBar.add(titleImg, SpringLayout.WEST);

        Lay.putConstraint(SpringLayout.SOUTH, bottomBar, -35, SpringLayout.SOUTH, this);

        if (BottomBar)
            add(bottomBar, SpringLayout.SOUTH);

        setLayout(Lay);
        setResizable(Resizeable);
        setPreferredSize(Size);

        setDefaultCloseOperation(CloseOperation);

        pack();
        setLocation(ImageUtilites.getScreenCenter(this));
    }
}
