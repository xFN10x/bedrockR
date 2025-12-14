package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
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
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ColorFunctions;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.SourceWorkspaceFile;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RLaunchPage;
import fn10.bedrockr.windows.RWorkspace;
import fn10.bedrockr.windows.util.ImageUtilites;

public class RAddon extends JPanel implements MouseListener {

    private final static Color BGC = ColorFunctions.darken(new Color(30, 30, 30), 0.01f);
    private SpringLayout Lay = new SpringLayout();
    protected JLabel Icon = new JLabel();
    protected JSeparator Div = new JSeparator();
    protected JPopupMenu Popup = new JPopupMenu();
    protected JLabel Name;
    protected VerticalLabel Version;
    protected JLabel LoadText;
    protected SourceWorkspaceFile WPFile;
    protected WorkspaceFile WPF;
    protected JFrame ancestor;

    public RAddon(RLaunchPage parent, String WPName) {
        this.ancestor = parent;

        BufferedImage BI;
        Image resizedImage = null;
        int step = 0;
        try {
            // dirty line of code coming up... "varibles? never hear of 'er"
            WPFile = new SourceWorkspaceFile(Files.readString(RFileOperations
                    .getFileFromWorkspace(WPName, File.separator + RFileOperations.WPFFILENAME,
                            true)
                    .toPath()));
            WPF = (WorkspaceFile) WPFile.getSerilized();
            step = 1;
            File iconFile = RFileOperations.getFileFromWorkspace(WPName,
                    File.separator + "icon." + WPF.IconExtension, true);
            iconFile.setReadable(true);
            BI = ImageIO.read(iconFile);
            resizedImage = ImageUtilites.ResizeImage(BI, 88, 88); // resize

        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            if (step == 0) {
                return;
            } else if (step == 1) {
                WPF = (WorkspaceFile) WPFile.getSerilized();
                try {
                    BI = ImageIO.read(getClass().getResourceAsStream("/addons/NotFound.png"));
                    var op = JOptionPane.showConfirmDialog(this, "Woah! The addon " + WPName
                            + ", is missing an icon. Would you like to add one?  (this message will go away ;) )",
                            "Addon Error", JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        var bic = new JFileChooser();
                        bic.setFileFilter(
                                new FileNameExtensionFilter("Addon's Supported Image Files", WPF.IconExtension));
                        if (bic.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                            BI = ImageIO.read(bic.getSelectedFile());
                            ImageIO.write(BI, WPF.IconExtension, RFileOperations
                                    .getFileFromWorkspace(WPName,
                                            File.separator + "icon." + WPF.IconExtension));
                        }

                    }
                    resizedImage = ImageUtilites.ResizeImage(BI, 88, 88); // resize

                } catch (Exception e2) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e2);
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
        setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.WHITE, 1, 16));

        Icon.setIcon(new ImageIcon(ImageUtilites.makeRoundedCorner(resizedImage, 16)));
        Icon.setAlignmentX(CENTER_ALIGNMENT);
        Icon.setAlignmentY(CENTER_ALIGNMENT);
        Icon.setPreferredSize(new Dimension(88, 88));

        Name = new JLabel(WPName) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0), // top color
                        0, getHeight(), new Color(60, 60, 60) // bottom color
                );

                g2.setPaint(gp);
                g2.fillRoundRect(1, 0, getWidth()-1, getHeight()-1, 15, 15);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        Name.setAlignmentX(CENTER_ALIGNMENT);
        Name.setPreferredSize(new Dimension(90, 20));
        Name.setForeground(Color.white);
        Name.setHorizontalAlignment(SwingConstants.CENTER);
        Name.setVerticalAlignment(SwingConstants.BOTTOM);

        Div.setPreferredSize(new Dimension(80, 3));
        Div.setForeground(Color.white);

        Version = new VerticalLabel(WPF.MinimumEngineVersion);
        Version.setPreferredSize(new Dimension(150, 150));

        LoadText = new JLabel("Load this?");

        // Div.setAlignmentX(CENTER_ALIGNMENT);

        // icon contraint
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Icon, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Icon, 0, SpringLayout.VERTICAL_CENTER, this);
        // others
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Div, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.NORTH, Div, 0, SpringLayout.NORTH, Name);
        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Name, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.SOUTH, Name, 1, SpringLayout.SOUTH, this);

        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, LoadText, 0, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.SOUTH, LoadText, 18, SpringLayout.SOUTH, this);

        Lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, Version, 21, SpringLayout.HORIZONTAL_CENTER, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Version, -10, SpringLayout.VERTICAL_CENTER, this);

        add(Name);
        add(Div);
        add(Version);
        add(LoadText);

        add(Icon);// have last

        addMouseListener(this);

        Popup.add("Delete Addon").addActionListener(ac -> {
            if (JOptionPane.showConfirmDialog(parent,
                    "Are you sure you want to delete this addon? (it will be gone for a while!)", "Confirm Deletion?",
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                try {
                    Launcher.LOG.info(RFileOperations.getWorkspace(WPFile.workspaceName()).getAbsolutePath());
                    FileUtils.deleteDirectory(RFileOperations.getWorkspace(WPFile.workspaceName()));
                    JOptionPane.showMessageDialog(parent,
                            "The Addon " + WPFile.workspaceName() + " has been deleted.");
                    parent.refresh();
                } catch (Exception e) {
                    fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                }
            }
        });

        setComponentPopupMenu(Popup);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (arg0.getButton() == MouseEvent.BUTTON1)
            RWorkspace.openWorkspace(ancestor, WPFile);
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
