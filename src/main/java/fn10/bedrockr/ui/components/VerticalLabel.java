package fn10.bedrockr.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class VerticalLabel extends JLabel {
    private final boolean clockwise;

    public VerticalLabel(String text) {
        this(text, false);
    }

    public VerticalLabel(String text, boolean clockwise) {
        super(text);
        this.clockwise = clockwise;
//        Graphics g = getGraphics();
//        FontMetrics metrics = getFontMetrics(g.getFont());
//        Rectangle2D bounds = metrics.getStringBounds(getText(), g);
//        w = bounds.getWidth();
//        h = bounds.getHeight();
    }

//    @Override
//    public int getWidth() {
//        return super.getHeight();
//    }
//
//    // ^ v returning oppisite things cause its rotated.
//    @Override
//    public int getHeight() {
//        return super.getWidth();
//    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D gx = (Graphics2D) g.create();

        gx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        FontMetrics metrics = getFontMetrics(gx.getFont());
        int textWid = metrics.stringWidth(getText());
        int textHei = metrics.getAscent();
        float halfX = getWidth() / 2f;
        float halfY = getHeight() / 2f;
        gx.setClip(null);
        
        gx.translate(halfX,0);
        gx.rotate(Math.toRadians(clockwise ? 90 : -90));
        float x = -textHei / 2f;
        float y = textWid / 2f;

        if (clockwise) {
            y = -y;
        }

        gx.drawString(getText(), 
                x, 
                y
        );
        //gx.drawString(getText(), 0,0);
        // gx.drawLine(0, 0, halfX, halfY);
       // super.paintComponent(gx);
        gx.dispose();
    }
}
