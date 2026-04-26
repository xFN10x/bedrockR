package fn10.bedrockr.rendering;

import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.util.ImageUtilites;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.IOException;
import java.util.Objects;

public class RenderHandler {

    public static BufferedImage makeSolidTest(Color color) {
        final BufferedImage bufferedImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(color);
        graphics2D.fillRect(0, 0, 16, 16);
        return bufferedImage;
    }

    static void main() throws IOException {
        final JFrame frame = new JFrame("render test");
        frame.setResizable(false);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setBackground(Color.BLACK);

        final Graphics graphics = frame.getContentPane().getGraphics();
        graphics.drawImage(ImageUtilites.ResizeImage(render6SideBlock(
                "test",
                missing(),
                makeSolidTest(Color.GRAY),
                makeSolidTest(Color.GREEN),
                makeSolidTest(Color.RED),
                makeSolidTest(Color.GREEN),
                makeSolidTest(Color.YELLOW)
        ), 300, 300), 0, 0, null);
    }
    public static Image render6SideBlock(String name, BufferedImage t) throws IOException {
        return render6SideBlock(name,t,t,t,t,t,t);
    }

    public static Image render6SideBlock(String name, BufferedImage t, BufferedImage d, BufferedImage side) throws IOException {
        return render6SideBlock(name,t,d,side,side,side,side);
    }

    public static BufferedImage imgToBuffered(Image img) {
        final BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        bufferedImage.createGraphics().drawImage(img,0,0,null);
        return bufferedImage;
    }

    public record BrightnessFilter(float mul) implements BufferedImageOp {
        @Override
        public BufferedImage filter(BufferedImage src, BufferedImage dest) {
            dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < src.getWidth(); x++) {
                for (int y = 0; y < src.getHeight(); y++) {
                    final int rgb = src.getRGB(x, y);
                    final Color color = new Color(rgb);
                    final int red = color.getRed();
                    final int green = color.getGreen();
                    final int blue = color.getBlue();
                    int newR = 0;
                    int newG = 0;
                    int newB = 0;

                    if (red != 0) newR = (int) Math.max(0, red * mul);
                    if (green != 0) newG = (int) Math.max(0, green * mul);
                    if (blue != 0) newB = (int) Math.max(0, blue * mul);

                    dest.setRGB(x,y, new Color(newR, newG, newB).getRGB());
                }
            }
            return dest;
        }

        @Override
        public Rectangle2D getBounds2D(BufferedImage src) {
            return src.getData().getBounds();
        }

        @Override
        public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
            return src;
        }

        @Override
        public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
            return srcPt;
        }

        @Override
        public RenderingHints getRenderingHints() {
            return null;
        }
    }

    public static BufferedImage missing() {
        final BufferedImage bufferedImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D grap = bufferedImage.createGraphics();
        grap.setColor(Color.black);
        grap.fillRect(0,0,16,16);
        grap.setColor(new Color(255,0,255));
        grap.fillRect(0,0,8,8);
        grap.fillRect(8,8,8,8);
        return bufferedImage;
    }

    public static Image render6SideBlock(String name, BufferedImage t, BufferedImage d, BufferedImage e, BufferedImage w, BufferedImage n, BufferedImage s) throws IOException {
        var top = imgToBuffered(ImageUtilites.ResizeImage(Objects.requireNonNullElse(t, missing()), 60, 60));
        var down = imgToBuffered(ImageUtilites.ResizeImage(Objects.requireNonNullElse(d, missing()), 50, 50));
        var east = imgToBuffered(ImageUtilites.ResizeImage(Objects.requireNonNullElse(e, missing()), 50, 50));
        var west = imgToBuffered(ImageUtilites.ResizeImage(Objects.requireNonNullElse(w, missing()), 50, 50));
        var north = imgToBuffered(ImageUtilites.ResizeImage(Objects.requireNonNullElse(n, missing()), 50, 50));
        var south = imgToBuffered(ImageUtilites.ResizeImage(Objects.requireNonNullElse(s, missing()), 50, 50));
        final int width = 100;
        final int height = 100;
        final BufferedImage main = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        final BufferedImage side1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D grap1 = side1.createGraphics();
        grap1.setColor(Color.BLUE);
        grap1.scale(1, .5);
        grap1.rotate(Math.toRadians(45));
        grap1.drawImage(top, null, 35, -35);

        final BufferedImage side2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D grap2 = side2.createGraphics();
        grap2.setColor(Color.BLUE);
        grap2.scale(.83, 1);
        grap2.shear(0,.41);
        grap2.drawImage(west, new BrightnessFilter(.75f), 10, 16);

        final BufferedImage side3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D grap3 = side3.createGraphics();
        grap3.setColor(Color.BLUE);
        grap3.scale(.83, 1);
        grap3.shear(0,-.42);
        grap3.drawImage(north, new BrightnessFilter(.5f), 60, 66);

        final Graphics2D maing = main.createGraphics();
        maing.translate(0,5);
        maing.drawImage(side1, null, 0, 0);
        maing.drawImage(side2, null, 0, 0);
        maing.drawImage(side3, null, 0, 0);


        ImageIO.write(main, "png", RFileOperations.getBaseDirectory("cache","renders", name+".png"));

        return main;
    }
}
