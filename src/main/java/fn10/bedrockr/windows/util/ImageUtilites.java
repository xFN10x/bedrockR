package fn10.bedrockr.windows.util;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class ImageUtilites {

    public static byte[] ImageToBytes(Image img) {
        // convert image to bytes
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        if (width <= 0 || height <= 0) {
            return new byte[0];
        }
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bi.getGraphics().drawImage(img, 0, 0, null);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bi, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static ImageIcon ResizeIcon(ImageIcon OG, int width, int height, int scalingMode) {
        return new ImageIcon(OG.getImage().getScaledInstance(width, height, scalingMode));
    }

    public static ImageIcon ResizeIcon(ImageIcon OG, int width, int height) {
        return new ImageIcon(OG.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public static Image ResizeImage(Image OG, Dimension size) {
        return OG.getScaledInstance((int) size.getWidth(), (int) size.getHeight(), Image.SCALE_SMOOTH);
    }

    public static Image ResizeImage(BufferedImage OG, Dimension size) {
        return OG.getScaledInstance((int) size.getWidth(), (int) size.getHeight(), Image.SCALE_SMOOTH);
    }

    public static Image ResizeImage(Image OG, Dimension size, int resizeMode) {
        return OG.getScaledInstance((int) size.getWidth(), (int) size.getHeight(), resizeMode);
    }

    public static Image ResizeImage(BufferedImage OG, Dimension size, int resizeMode) {
        return OG.getScaledInstance((int) size.getWidth(), (int) size.getHeight(), resizeMode);
    }

    public static Image ResizeImage(Image OG, int width, int height) {
        return OG.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static Image ResizeImage(BufferedImage OG, int width, int height) {
        return OG.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static Image ResizeImage(Image OG, int width, int height, int resizeMode) {
        return OG.getScaledInstance(width, height, resizeMode);
    }

    public static Image ResizeImage(BufferedImage OG, int width, int height, int resizeMode) {
        return OG.getScaledInstance(width, height, resizeMode);
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
        return new Point(((int) ((size.getWidth() - target.getWidth()) * 0.5)),
                ((int) ((size.getHeight() - target.getHeight()) * 0.5)));
    }

    public static Point getCenter(Component target) {
        Dimension size = target.getParent().getSize();
        return new Point(((int) ((size.getWidth() - target.getWidth()) * 0.5)),
                ((int) ((size.getHeight() - target.getHeight()) * 0.5)));
    }

    /// totally didn't steal this from Color$brighter()
    ///<p>also factor is .7 by default
    public static Color brighter(Color colour, float FACTOR) {
        int r = colour.getRed();
        int g = colour.getGreen();
        int b = colour.getBlue();
        int alpha = colour.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/FACTOR), 255),
                Math.min((int)(g/FACTOR), 255),
                Math.min((int)(b/FACTOR), 255),
                alpha);
    }

    /// totally didn't steal this from Color$darker()
    ///<p>also factor is .7 by default
    public static Color darker(Color colour, float FACTOR) {
        return new Color(Math.max((int)(colour.getRed()  *FACTOR), 0),
                Math.max((int)(colour.getGreen()*FACTOR), 0),
                Math.max((int)(colour.getBlue() *FACTOR), 0),
                colour.getAlpha());
    }

    /**
     * credit: https://stackoverflow.com/a/7603815
     */
    public static BufferedImage makeRoundedCorner(Image image, int cornerRadius) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }
}
