package fn10.bedrockr.installer;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;
import fn10.bedrockr.installer.laf.BedrockrDark;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Installer extends JFrame {

    private final Dimension size;

    static void main(String[] args) throws IOException {
        List<String> argList = List.of(args);
        if (argList.contains("-dev")) {
            
        }
        
        RFonts.init();
        FlatLaf.registerCustomDefaultsSource("laf");
        if (SystemInfo.isLinux) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        BedrockrDark.setup();
        
        new Installer().setVisible(true);
    }
    
    
    
    public Installer() throws IOException {
        super("Install bedrockR");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        size = new Dimension(900, 500);
        setSize(size);
        setIconImage(ImageIO.read(getClass().getResource("/ui/Icon_huge.png")));

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
