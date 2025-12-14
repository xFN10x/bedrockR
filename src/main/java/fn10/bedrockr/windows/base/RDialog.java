package fn10.bedrockr.windows.base;

import java.awt.*;
import javax.swing.*;

import fn10.bedrockr.windows.util.ImageUtilites;

public class RDialog extends JDialog {

    public SpringLayout Lay = new SpringLayout();

    // public RDialog(JFrame Parent,int CloseOperation,String WindowTitle,Dimension
    // Size) {
    // this(Parent,CloseOperation, WindowTitle, Size);
    // }

    public RDialog(Window Parent, int CloseOperation, String WindowTitle, Dimension Size) {
        this(Parent, CloseOperation, WindowTitle, Size, true);
    }

    public RDialog(Window Parent, int CloseOperation, String WindowTitle, Dimension Size,
            Boolean IDKWhatToCallItSoItsNameIsWeridNameBit) {
        super(Parent, WindowTitle + (IDKWhatToCallItSoItsNameIsWeridNameBit ? " - bedrockR" : ""));
        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(Color.GREEN);
        bottomBar.setPreferredSize(new Dimension(Size.width, 40));

        int titleImgW = 187;
        int titleImageH = 40;
        ImageIcon titleImgIcon = ImageUtilites.ResizeImageByURL(getClass().getResource("/ui/BrandingFullWShadow.png"),
                titleImgW, titleImageH); // Full Icon
        JLabel titleImg = new JLabel(titleImgIcon);
        titleImg.setSize(new Dimension(titleImgW, titleImageH));
        titleImg.setHorizontalAlignment(SwingConstants.LEFT);
        titleImg.setVerticalAlignment(SwingConstants.CENTER);

        // bottomBar.setLayout(new SpringLayout());
        // bottomBar.add(titleImg,SpringLayout.WEST);

        Lay.putConstraint(SpringLayout.SOUTH, bottomBar, -40, SpringLayout.SOUTH, this);

        // if (BottomBar)
        // add(bottomBar,SpringLayout.SOUTH);

        setLayout(Lay);
        setResizable(false);
        setPreferredSize(Size);

        setDefaultCloseOperation(CloseOperation);

        pack();
        setModal(true); // the only thing changed
        setLocation(ImageUtilites.getScreenCenter(this));

    }
}
