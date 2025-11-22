package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/*
you suck past me, im gonna get rid of this
*/

@Deprecated(since = "a1.4", forRemoval = true)
//fully ai made (someday ill learn the graphics stuff)
public class RoundedLabel extends JLabel {
    private final int arc;

    public RoundedLabel(String text, int arc) {
        super(text);
        this.arc = arc;
        setOpaque(false); // We'll paint the background ourselves
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Example: vertical gradient from dark gray to light gray
        java.awt.GradientPaint gp = new java.awt.GradientPaint(
            0, 0, new Color(0, 0, 0,0), // top color
            0, getHeight(), new Color(60, 60, 60) // bottom color
        );

        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
