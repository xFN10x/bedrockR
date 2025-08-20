package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;

import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.elementFiles.WPFile;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFileOperations;

public class RAddon extends JPanel implements MouseListener {

    private final static Color BGC = ColorFunctions.darken(new Color(30, 30, 30), 0.01f);
    private SpringLayout Lay = new SpringLayout();
    protected JLabel Icon = new JLabel();
    protected JSeparator Div = new JSeparator();
    protected JLabel Name;
    protected JLabel Version;
    protected JLabel LoadText;
    protected SourceWPFile WPFile;
    protected WPFile WPF;
    protected JFrame ancestor;

    public RAddon(String WPName, JFrame Ancestor) {
        this.ancestor = Ancestor;

        BufferedImage BI;
        Image resizedImage = null;
        int step = 0;
        try {
            // dirty line of code coming up... "varibles? never hear of 'er"
            WPFile = new SourceWPFile(Files.readString(RFileOperations
                    .getFileFromWorkspace((Frame) getParent(), WPName, File.separator + RFileOperations.WPFFILENAME,
                            true)
                    .toPath()));
            WPF = (WPFile) WPFile.getSerilized();
            step = 1;
            var iconFile = RFileOperations.getFileFromWorkspace((Frame) getParent(), WPName,
                    File.separator + "icon." + WPF.IconExtension, true);
            iconFile.setReadable(true);
            BI = ImageIO.read(iconFile);
            resizedImage = ImageUtilites.ResizeImage(BI, 88, 88); // resize

        } catch (Exception e) {
            e.printStackTrace();
            if (step == 0) {
                return;
            } else if (step == 1) {
                WPF = (WPFile) WPFile.getSerilized();
                try {
                    BI = ImageIO.read(getClass().getResourceAsStream("/addons/NotFound.png"));
                    var op = JOptionPane.showConfirmDialog(this, "Woah! The addon " + WPName
                            + ", is missing an icon. Would you like to add one?  (this message will go away ;) )",
                            "Addon Error", JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        var bic = new JFileChooser();
                        bic.setFileFilter(
                                new FileNameExtensionFilter("Addon's Support Image Files", WPF.IconExtension));
                        if (bic.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                            BI = ImageIO.read(bic.getSelectedFile());
                            ImageIO.write(BI, WPF.IconExtension, RFileOperations
                                    .getFileFromWorkspace((Frame) getParent(), WPName,
                                            File.separator + "icon." + WPF.IconExtension));
                        }

                    }
                    resizedImage = ImageUtilites.ResizeImage(BI, 88, 88); // resize

                } catch (Exception e2) {
                    e2.printStackTrace();
                    BI = null;
                    resizedImage = null;
                    return;
                }
            } else {
                BI = null;
                resizedImage = null;
                return;
            }
        }

        setLayout(Lay);
        setPreferredSize(new Dimension(90, 90));
        setBackground(BGC);
        setBorder(new FlatLineBorder(new Insets(2, 2, 2, 2), Color.WHITE, 1, 16));

        BufferedImage BI2 = new BufferedImage(resizedImage.getWidth(null), // back to bufferedimage
                resizedImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        BI2.getGraphics().drawImage(resizedImage, 0, 0, null);

        Icon.setIcon(
                new ImageIcon(ImageUtilites.makeRoundedCorner(BI2, 14)));
        Icon.setAlignmentX(CENTER_ALIGNMENT);
        Icon.setAlignmentY(CENTER_ALIGNMENT);
        Icon.setPreferredSize(new Dimension(100, 100));
        // Icon.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Color.GRAY, 1,
        // 16));

        Name = new RoundedLabel(WPName, 16);
        Name.setAlignmentX(CENTER_ALIGNMENT);
        Name.setPreferredSize(new Dimension(90, 40));
        Name.setBackground(Color.DARK_GRAY);
        Name.setForeground(Color.white);
        Name.setHorizontalAlignment(SwingConstants.CENTER);
        Name.setVerticalAlignment(SwingConstants.BOTTOM);

        Div.setPreferredSize(new Dimension(80, 3));
        Div.setForeground(Color.white);

        Version = new JLabel(WPF.MinimumEngineVersion) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D gx = (Graphics2D) g;
                gx.rotate(-1.5708, getX() + getWidth() / 2, getY() + getHeight() / 2);
                super.paintComponent(g);
            }
        };
        Version.setPreferredSize(new Dimension(150, 150));

        LoadText = new JLabel("Load this?");

        // Div.setAlignmentX(CENTER_ALIGNMENT);

        // icon contraint
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Icon, 6, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Icon, 0, SpringLayout.VERTICAL_CENTER, this);
        // others
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Div, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Div, 20, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Name, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.SOUTH, Name, 1, SpringLayout.SOUTH, this);

        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, LoadText, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.SOUTH, LoadText, 18, SpringLayout.SOUTH, this);

        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Version, 21, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Version, -14, SpringLayout.VERTICAL_CENTER, this);

        add(Name);
        add(Div);
        add(Version);
        add(LoadText);

        add(Icon);// have last

        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (arg0.getButton() == MouseEvent.BUTTON1)
            RFileOperations.openWorkspace(ancestor, WPFile);
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        SwingUtilities.invokeLater(() -> {
            setSize(new Dimension(110, 110));
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        SwingUtilities.invokeLater(() -> {
            setSize(new Dimension(90, 90));
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        return; // dont need this
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        return; // dont need this
    }

}
