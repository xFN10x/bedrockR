package fn10.bedrockr.ui.components;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.SystemFileChooser;
import fn10.bedrockr.addons.element.elementFiles.WorkspaceFile;
import fn10.bedrockr.ui.RLaunchPage;
import fn10.bedrockr.ui.RWorkspace;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RFileOperations;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

@SuppressWarnings("FieldCanBeLocal")
public class RAddon extends JPanel implements MouseListener {

    private final static Color BGC = ColorFunctions.darken(new Color(30, 30, 30), 0.01f);
    private final SpringLayout Lay = new SpringLayout();
    protected JLabel Icon = new JLabel();
    protected JSeparator Div = new JSeparator();
    protected JPopupMenu Popup = new JPopupMenu();
    protected JLabel Name;
    protected VerticalLabel Version;
    protected JLabel LoadText;
    protected WorkspaceFile WPF;
    protected JFrame ancestor;

    public RAddon(RLaunchPage parent, String WPName) {
        this.ancestor = parent;

        BufferedImage BI;
        Image resizedImage;
        int step = 0;
        try {
            WPF = RFileOperations.getWorkspaceFile(WPName);
            step = 1;
            File iconFile = RFileOperations.getFileFromWorkspace(WPName, true, "icon." + WPF.IconExtension);
            BufferedImage readIcon = ImageIO.read(iconFile);
            if (WPF.Format < RFileOperations.CURRENT_WORKSPACE_FORMAT) {
                BI = new BufferedImage(readIcon.getWidth(), readIcon.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                BI.createGraphics().drawImage(readIcon, 0, 0, null);
            } else {
                BI = readIcon;
            }
            resizedImage = ImageUtilities.ResizeImage(BI, 88, 88); // resize

        } catch (Exception e) {
            RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            if (step == 0) {
                return;
            } else {
                try {
                    BI = ImageIO.read(RFileOperations.streamAllOfResource("/addons/NotFound.png"));
                    var op = JOptionPane.showConfirmDialog(this, "Woah! The addon " + WPName
                                    + ", is missing an icon. Would you like to add one?  (this message will go away ;) )",
                            "Addon Error", JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        SystemFileChooser bic = new SystemFileChooser(RFileOperations.getFileChooserDefaultPath());
                        bic.setFileFilter(
                                new SystemFileChooser.FileNameExtensionFilter("Addon's Supported Image Files", WPF.IconExtension));
                        if (bic.showOpenDialog(this) == SystemFileChooser.APPROVE_OPTION) {
                            BI = ImageIO.read(bic.getSelectedFile());
                            ImageIO.write(BI, WPF.IconExtension, RFileOperations
                                    .getFileFromWorkspace(WPName,
                                            File.separator + "icon." + WPF.IconExtension));
                        }

                    }
                    resizedImage = ImageUtilities.ResizeImage(BI, 88, 88); // resize

                } catch (Exception e2) {
                    RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e2);
                    return;
                }
            }
        }

        setLayout(Lay);
        setPreferredSize(new Dimension(90, 90));
        setBackground(BGC);
        setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.WHITE, 1, 16));

        Icon.setIcon(new ImageIcon(ImageUtilities.makeRoundedCorner(resizedImage, 16)));
        Icon.setAlignmentX(CENTER_ALIGNMENT);
        Icon.setAlignmentY(CENTER_ALIGNMENT);
        Icon.setPreferredSize(new Dimension(88, 88));

        Name = new JLabel(WPName) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0,
                        new Color(0, 0, 0, 0), // top color
                        0, getHeight(),
                        new Color(60, 60, 60) // bottom color
                );

                g2.setPaint(gp);

                Rectangle2D oldClip = g2.getClip().getBounds();
                g2.setClip(new Rectangle2D.Double(oldClip.getX() - 10, oldClip.getY(), oldClip.getWidth() + 20, oldClip.getHeight()));
                g2.fillRoundRect(-4, 0, getWidth() + 9, getHeight() - 1, 15, 15);

                super.paintComponent(g2);
            }
        };
        Name.setAlignmentX(CENTER_ALIGNMENT);
        Name.setPreferredSize(new Dimension(80, 20));
        Name.setForeground(Color.white);
        Name.setHorizontalAlignment(SwingConstants.CENTER);
        Name.setVerticalAlignment(SwingConstants.BOTTOM);

        Div.setPreferredSize(new Dimension(80, 3));
        Div.setForeground(Color.white);

        Version = new VerticalLabel(WPF.MinimumEngineVersion);
        //Version.setPreferredSize(new Dimension(150, 150));

        LoadText = new JLabel("Load this?");

        // Div.setAlignmentX(CENTER_ALIGNMENT);

        // icon contraint
        Lay.putConstraint(SpringLayout.NORTH, Icon, 0, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.WEST, Icon, 0, SpringLayout.WEST, this);
        // others
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Div, 0, SpringLayout.HORIZONTAL_CENTER, Icon);
        Lay.putConstraint(SpringLayout.NORTH, Div, 0, SpringLayout.NORTH, Name);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Name, 0, SpringLayout.HORIZONTAL_CENTER, Icon);
        Lay.putConstraint(SpringLayout.SOUTH, Name, 1, SpringLayout.SOUTH, Icon);

        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, LoadText, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.SOUTH, LoadText, 0, SpringLayout.SOUTH, this);

        Lay.putConstraint(SpringLayout.EAST, Version, -2, SpringLayout.EAST, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Version, 15, SpringLayout.VERTICAL_CENTER, this);

        add(Name);
        add(Div);

        add(Version);
        add(LoadText);
        Version.setVisible(false);
        LoadText.setVisible(false);

        add(Icon);// have last

        addMouseListener(this);

        Popup.add("Delete Addon").addActionListener(_ -> {
            if (JOptionPane.showConfirmDialog(parent,
                    "Are you sure you want to delete this addon? (it will be gone for a while!)", "Confirm Deletion?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                try {
                    RFileOperations.LOG.info(RFileOperations.getWorkspace(WPF.WorkspaceName).getAbsolutePath());
                    FileUtils.deleteDirectory(RFileOperations.getWorkspace(WPF.WorkspaceName));
                    JOptionPane.showMessageDialog(parent,
                            "The Addon " + WPF.WorkspaceName + " has been deleted.");
                    parent.refresh();
                } catch (Exception e) {
                    RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                }
            }
        });

        tweenTimer.setRepeats(true);
        tweenTimer.start();
        setComponentPopupMenu(Popup);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (arg0.getButton() == MouseEvent.BUTTON1)
            RWorkspace.openWorkspace(ancestor, WPF.getNewSource());
    }

    private float currentSize = 0;
    private float targetSize = 90;
    private final Timer tweenTimer = new Timer(16, _ -> {
        currentSize = lerp(currentSize, targetSize, 0.2f);
        int intsize = Math.round(currentSize);
        setPreferredSize(new Dimension(intsize, intsize));
        revalidate();
        repaint();
    });

    /// <https://www.sourcetrail.com/java/java-lerp/>
    public static float lerp(float point1, float point2, float fraction) {
        return (1 - fraction) * point1 + fraction * point2;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        SwingUtilities.invokeLater(() -> {
            LoadText.setVisible(true);
            Version.setVisible(true);
            targetSize = 110;
            revalidate();
            repaint();
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        SwingUtilities.invokeLater(() -> {
            LoadText.setVisible(false);
            Version.setVisible(false);
            targetSize = 90;
            revalidate();
            repaint();

        });
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // dont need this
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // dont need this
    }

}
