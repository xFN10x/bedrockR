package fn10.bedrockr.installer;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.SystemInfo;
import com.sun.jna.platform.win32.Advapi32Util;
import fn10.bedrockr.installer.laf.BedrockrDark;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Installer extends JFrame {

    private final Dimension size;
    public static InstallInfo info;

    static void main(String[] args) throws IOException {
        
        List<String> argList = List.of(args);
        if (argList.contains("-dev")) {
            info = new InstallInfo("a3.0PRE2", "0.9.3", 10, 25);
        } else {
            info = InstallInfo.get();
        }
 
        RFonts.init();
        FlatLaf.registerCustomDefaultsSource("laf");
        if (SystemInfo.isLinux) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        BedrockrDark.setup();

        Installer frame = new Installer();
        frame.setVisible(true);
        if (!SystemInfo.isWindows) {
            JOptionPane.showMessageDialog(frame, "The bedrockR Installer is designed for Windows. Please try to use your package manager to install bedrockR on linux. Also, bedrockR is not supported on Mac aswell.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else 
        if (!Advapi32Util.isCurrentProcessElevated()) {
            JOptionPane.showMessageDialog(frame, "You need to run the bedrockR installer as admin.", "Admin required", JOptionPane.ERROR_MESSAGE);
            frame.setVisible(false);
        }
    }


    public Installer() throws IOException {
        super("Install bedrockR");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        size = new Dimension(475, 250);
        setSize(size);
        setIconImage(ImageIO.read(getClass().getResource("/ui/Icon_huge.png")));
        SpringLayout lay = new SpringLayout();
        setLayout(lay);

        ScaledImageIcon titleIcon = new ScaledImageIcon(new ImageIcon(getClass().getResource("/title.png")));
        JLabel titileLabel = new JLabel(titleIcon);

        JButton expressInstallButton = new JButton("Express Install...");
        JButton installButton = new JButton("Install...");
        
        expressInstallButton.addActionListener(ac -> {
            try {
                InstallManager.install(info, Path.of("C:","Program Files","bedrockR"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        
        lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, titileLabel, 0, SpringLayout.HORIZONTAL_CENTER, getContentPane());
        lay.putConstraint(SpringLayout.NORTH, titileLabel, 5, SpringLayout.NORTH, getContentPane());
        
        
        add(titileLabel);
        add(expressInstallButton);
        add(installButton);
        
        
        GraphicsDevice mouseScreen = MouseInfo.getPointerInfo().getDevice();
        GraphicsConfiguration config = mouseScreen.getDefaultConfiguration();

        Rectangle bounds = config.getBounds();
        Dimension size = bounds.getSize();

        setLocation(
                (int) ((size.getWidth() - getWidth()) / 2) + bounds.x,
                (int) ((size.getHeight() - getHeight()) / 2) + bounds.y
        );
    }
}
