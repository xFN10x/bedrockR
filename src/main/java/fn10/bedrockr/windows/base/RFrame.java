package fn10.bedrockr.windows.base;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.utils.ImageUtilites;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class RFrame extends JFrame {

    public final SpringLayout Lay = new SpringLayout();
    public final SpringLayout Lay2 = new SpringLayout();

    public final JPanel BottomBar = new JPanel();

    public static final int titleImgW = 168;
    public static final int titleImageH = 36;

    public static final JLabel verText = new JLabel(Launcher.VERSION);

    public final JButton somButton = new JButton(
            ImageUtilites.ResizeIcon(new ImageIcon(RFrame.class.getResource("/som.png")), 32, 32));
    public final JButton ghButton = new JButton(
            ImageUtilites.ResizeIcon(new ImageIcon(RFrame.class.getResource("/gh.png")), 32, 32));

    public final JLabel titleImg = new JLabel(ImageUtilites
            .ResizeImageByURL(RFrame.class.getResource("/ui/BrandingFullWShadow.png"), titleImgW, titleImageH,
                    Image.SCALE_AREA_AVERAGING));

    public RFrame(int CloseOperation, String WindowTitle, Dimension Size, boolean Resizeable) {
        this(CloseOperation, WindowTitle, Size, Resizeable, !Resizeable);
    }

    public RFrame(int CloseOperation, String WindowTitle, Dimension Size, boolean Resizeable, boolean hasBottomBar) {
        super(WindowTitle + " - bedrockR Alpha");

        setIconImage(Launcher.ICON);

        BottomBar.setBackground(Color.GREEN);
        BottomBar.setPreferredSize(new Dimension(0, 40));

        titleImg.setSize(new Dimension(titleImgW, titleImageH));

        BottomBar.setLayout(Lay2);
        BottomBar.add(titleImg);

        Lay.putConstraint(SpringLayout.SOUTH, BottomBar, 0, SpringLayout.SOUTH, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, BottomBar, 0, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.EAST, BottomBar, 0, SpringLayout.EAST, getContentPane());

        // bottom bar contraints
        Lay2.putConstraint(SpringLayout.WEST, titleImg, 5, SpringLayout.WEST, BottomBar);
        Lay2.putConstraint(SpringLayout.VERTICAL_CENTER, titleImg, 0, SpringLayout.VERTICAL_CENTER, BottomBar);
        Lay2.putConstraint(SpringLayout.VERTICAL_CENTER, ghButton, 0, SpringLayout.VERTICAL_CENTER, BottomBar);
        Lay2.putConstraint(SpringLayout.VERTICAL_CENTER, somButton, 0, SpringLayout.VERTICAL_CENTER, BottomBar);

        Lay2.putConstraint(SpringLayout.EAST, somButton, -5, SpringLayout.EAST, BottomBar);
        Lay2.putConstraint(SpringLayout.EAST, ghButton, -5, SpringLayout.WEST, somButton);

        Lay2.putConstraint(SpringLayout.SOUTH, verText, 0, SpringLayout.SOUTH, titleImg);
        Lay2.putConstraint(SpringLayout.WEST, verText, -5, SpringLayout.EAST, somButton);

        somButton.setMaximumSize(new Dimension(32, 32));
        ghButton.setMaximumSize(new Dimension(32, 32));

        somButton.setBackground(Color.white);
        ghButton.setBackground(Color.white);

        somButton.setToolTipText("bedrockR's Summer of Making Page");
        ghButton.setToolTipText("bedrockR's Github Repository");

        somButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://summer.hackclub.com/projects/703"));
            } catch (IOException e1) {

                e1.printStackTrace();
            } catch (URISyntaxException e1) {

                e1.printStackTrace();
            }

        });
        ghButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/xFN10x/bedrockR"));
            } catch (IOException e1) {

                e1.printStackTrace();
            } catch (URISyntaxException e1) {

                e1.printStackTrace();
            }
        });

        somButton.setBorder(new LineBorder(Color.green.darker(), 3));
        ghButton.setBorder(new LineBorder(Color.green.darker(), 3));

        verText.setForeground(Color.white);

        if (hasBottomBar)
            add(BottomBar);

        BottomBar.add(somButton);
        BottomBar.add(ghButton);
        BottomBar.add(verText);

        setLayout(Lay);
        setResizable(Resizeable);
        setPreferredSize(Size);

        setDefaultCloseOperation(CloseOperation);

        pack();
        setLocation(ImageUtilites.getScreenCenter(this));
    }
}
