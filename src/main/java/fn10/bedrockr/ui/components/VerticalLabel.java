package fn10.bedrockr.ui.components;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

public class VerticalLabel extends JLabel {

    public VerticalLabel(String text) {
        super(text);
    }
    
    int i = 0;

    @Override
    public void paintComponent(Graphics g) {
        FontMetrics metrics = getFontMetrics(g.getFont());
        Rectangle2D bounds = metrics.getStringBounds(getText(), getGraphics());
        Graphics2D gx = (Graphics2D) g;
        int halfX = (int) ((bounds.getWidth() / 2f));
        int halfY = (int) ((bounds.getHeight() / 2f));
        Rectangle rotatedRec = new Rectangle(halfX-halfY, halfY-halfX, (int) bounds.getHeight(), (int) bounds.getWidth());
        gx.setColor(Color.BLUE);
        gx.setClip(rotatedRec);
        
        gx.rotate(Math.toRadians(-90), halfX, halfY);
       // gx.drawLine(0, 0, halfX, halfY);
        super.paintComponent(gx);
    }
}
