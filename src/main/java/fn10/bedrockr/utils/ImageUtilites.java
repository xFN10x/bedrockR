package fn10.bedrockr.utils;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.*;

public class ImageUtilites {

    public static ImageIcon ResizeIcon(ImageIcon OG, int width, int height, int scalingMode) {
        return new ImageIcon(OG.getImage().getScaledInstance(width, height, scalingMode));
    }

    public static ImageIcon ResizeIcon(ImageIcon OG, int width, int height) {
        return new ImageIcon(OG.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public static Image ResizeImage(BufferedImage OG, int width, int height) {
        return OG.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static ImageIcon ResizeImageByURL(URL OG, int width, int height, int scalingMode) {
        return new ImageIcon(new ImageIcon(OG).getImage().getScaledInstance(width, height, scalingMode));
    }

    public static ImageIcon ResizeImageByURL(URL OG, int width, int height) {
        ImageIcon image = new ImageIcon(OG);

        return new ImageIcon(image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public static Point getScreenCenter(Component target) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(((int)((size.getWidth() - target.getWidth()) * 0.5)), ((int) ((size.getHeight() - target.getHeight()) * 0.5)));
    }

    public static Point getCenter(Component target) {
        Dimension size = target.getParent().getSize();
        return new Point(((int)((size.getWidth() - target.getWidth()) * 0.5)), ((int) ((size.getHeight() - target.getHeight()) * 0.5)));
    }

    // credit: https://stackoverflow.com/a/7603815, and github copilot
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rounded rectangle as the clipping mask
        g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // Draw the image only where the mask is set
        g2.drawImage(image, 0, 0, null);

        g2.dispose();
        return output;
    }
}
