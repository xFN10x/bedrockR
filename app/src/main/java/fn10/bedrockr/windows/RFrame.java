package fn10.bedrockr.windows;

import fn10.bedrockr.windows.laf.BedrockrDark;
import fn10.bedrockr.windows.utils.ImageUtilites;

import java.awt.*;
import javax.swing.*;

import com.formdev.flatlaf.util.SwingUtils;

public class RFrame extends JFrame {
    
    public RFrame(int CloseOperation,String WindowTitle,Dimension Size,boolean Resizeable) {

        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(new BedrockrDark());
        } catch (Exception e) {
            // TODO: handle exception
        }

        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(Color.GREEN);
        bottomBar.setPreferredSize(new Dimension(Size.width, 40));

        int titleImgW = 187;
        int titleImageH = 40;
        ImageIcon titleImgIcon = ImageUtilites.ResizeImageByURL(getClass().getResource("/branding/BrandingFullWShadow.png"), titleImgW, titleImageH); // Full Icon
        JLabel titleImg = new JLabel(titleImgIcon);
        titleImg.setSize(new Dimension(titleImgW,titleImageH));
        titleImg.setHorizontalAlignment(SwingConstants.LEFT);
        titleImg.setVerticalAlignment(SwingConstants.CENTER);
        
        bottomBar.setLayout(new SpringLayout());
        bottomBar.add(titleImg,SpringLayout.WEST);

        if (!Resizeable)
        add(bottomBar,BorderLayout.SOUTH);

        setResizable(Resizeable);
        setPreferredSize(Size);

        setDefaultCloseOperation(CloseOperation);
        
        pack();
    
    }
}
