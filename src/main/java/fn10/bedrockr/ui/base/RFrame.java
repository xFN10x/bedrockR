package fn10.bedrockr.ui.base;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RLogUtils;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.awt.event.ActionListener;
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

    public static final JLabel verText = new JLabel(RFileOperations.VERSION);

    public final JButton websiteButton = new JButton(new ImageIcon(RFileOperations.readAllOfResource("/website.png")));
    public final JButton ghButton = new JButton(
            ImageUtilities.ResizeIcon(new ImageIcon(RFileOperations.readAllOfResource("/gh.png")), 32, 32));
    public final JButton ftButton = new JButton(
            ImageUtilities.ResizeIcon(new ImageIcon(RFileOperations.readAllOfResource("/gh.png")), 32, 32));

    public final JLabel titleImg = new JLabel(ImageUtilities
            .ResizeImageByURL(RFrame.class.getResource("/ui/BrandingFullWShadow.png"), titleImgW, titleImageH,
                    Image.SCALE_AREA_AVERAGING));

    public RFrame(int CloseOperation, String WindowTitle, Dimension Size, boolean Resizeable) {
        this(CloseOperation, WindowTitle, Size, Resizeable, !Resizeable);
    }

    public RFrame(int CloseOperation, String WindowTitle, Dimension Size, boolean Resizeable, boolean hasBottomBar) {
        super(WindowTitle + " - bedrockR " + RFileOperations.VERSION);

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
        Lay2.putConstraint(SpringLayout.VERTICAL_CENTER, ftButton, 0, SpringLayout.VERTICAL_CENTER, BottomBar);
        Lay2.putConstraint(SpringLayout.VERTICAL_CENTER, websiteButton, 0, SpringLayout.VERTICAL_CENTER, BottomBar);

        Lay2.putConstraint(SpringLayout.EAST, websiteButton, -5, SpringLayout.EAST, BottomBar);
        Lay2.putConstraint(SpringLayout.EAST, ghButton, -5, SpringLayout.WEST, websiteButton);
        Lay2.putConstraint(SpringLayout.EAST, ftButton, -5, SpringLayout.WEST, ghButton);

        Lay2.putConstraint(SpringLayout.SOUTH, verText, 0, SpringLayout.SOUTH, titleImg);
        Lay2.putConstraint(SpringLayout.EAST, verText, -5, SpringLayout.WEST, ftButton);

        websiteButton.setMaximumSize(new Dimension(32, 32));
        ghButton.setMaximumSize(new Dimension(32, 32));
        ftButton.setMaximumSize(new Dimension(32, 32));

        websiteButton.setBackground(Color.white);
        ghButton.setBackground(Color.white);
        ftButton.setBackground(Color.white);

        websiteButton.setToolTipText("bedrockR's Website");
        ghButton.setToolTipText("bedrockR's Github Repository");
        ftButton.setToolTipText("bedrockR's Flavourtown Page");

        websiteButton.addActionListener(openLink("https://bedrockr.xplate.dev"));
        ghButton.addActionListener(openLink("https://github.com/xFN10x/bedrockR"));
        ftButton.addActionListener(openLink("https://flavortown.hackclub.com/projects/3844"));

        websiteButton.setBorder(new LineBorder(Color.green.darker(), 3));
        ghButton.setBorder(new LineBorder(Color.green.darker(), 3));
        ftButton.setBorder(new LineBorder(Color.green.darker(), 3));

        verText.setForeground(Color.white);

        if (hasBottomBar)
            add(BottomBar);

        BottomBar.add(websiteButton);
        BottomBar.add(ghButton);
        BottomBar.add(ftButton);
        BottomBar.add(verText);

        setLayout(Lay);
        setResizable(Resizeable);
        setPreferredSize(Size);

        setDefaultCloseOperation(CloseOperation);

        pack();
        setLocation(ImageUtilities.getScreenCenter(this));
    }

    private static @NonNull ActionListener openLink(String url) {
        return _ -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e1) {
                RLogUtils.warnException(e1);
            }
        };
    }
}
