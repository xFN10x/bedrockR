package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.time.LocalTime;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import fn10.bedrockr.utils.ImageUtilites;

public class RSplashScreen extends JWindow {

    private final JLabel image = new JLabel();
    // private final JProgressBar prog = new JProgressBar(JProgressBar.VERTICAL);

    private final SpringLayout lay = new SpringLayout();

    public RSplashScreen() {

        LocalTime now = LocalTime.now();
        LocalTime sunset = LocalTime.of(7, 0);
        LocalTime sunrise = LocalTime.of(19, 0);

        int compareSunSet = now.compareTo(sunset);
        int compareSunRise = sunrise.compareTo(now);

        if (compareSunSet > 0 && compareSunRise < 0) // if its after sun set, but before sun rise
        {
            image.setIcon(new ImageIcon(getClass().getResource("/splash_night.png")));

        } else {

            image.setIcon(new ImageIcon(getClass().getResource("/splash.png")));
        }
        
        setLayout(lay);

        setSize(new Dimension(800, 500));
        setLocation(ImageUtilites.getScreenCenter(this));

        add(image);

        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }

}
