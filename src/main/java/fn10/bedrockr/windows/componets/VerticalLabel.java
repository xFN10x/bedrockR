package fn10.bedrockr.windows.componets;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

public class VerticalLabel extends JLabel {

    public VerticalLabel(String minimumEngineVersion) {
        super(minimumEngineVersion);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D gx = (Graphics2D) g;
        gx.rotate(-1.5708, getX() + getWidth() / 2, getY() + getHeight() / 2);
        super.paintComponent(g);
    }
}
