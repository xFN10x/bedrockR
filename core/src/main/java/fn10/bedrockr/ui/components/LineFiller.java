package fn10.bedrockr.ui.components;

import javax.swing.*;
import java.awt.*;

public class LineFiller extends Box.Filler {
    /**
     * Constructor to create shape with the given size ranges.
     *
     * @param min  Minimum size
     * @param pref Preferred size
     * @param max  Maximum size
     */
    public LineFiller(Dimension min, Dimension pref, Dimension max) {
        super(
                new Dimension((int) Math.max(min.getWidth(),1), (int) Math.max(min.getHeight(),1)), 
                new Dimension((int) Math.max(pref.getWidth(),1), (int) Math.max(pref.getHeight(),1)), 
                new Dimension((int) Math.max(max.getWidth(),1), (int) Math.max(max.getHeight(),1)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getForeground());
        g.drawLine(0,0,getWidth(),getWidth());
        super.paintComponent(g);
    }

    public static Component createHorizontalGlue() {
        return new LineFiller(new Dimension(0,0), new Dimension(0,0),
                new Dimension(Short.MAX_VALUE, 0));
    }
}
