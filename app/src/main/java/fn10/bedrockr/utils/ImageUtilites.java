package fn10.bedrockr.utils;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

public class ImageUtilites {


    public static ImageIcon ResizeIcon(ImageIcon OG,int width,int height) {
        return new ImageIcon(OG.getImage().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));
    }

    public static ImageIcon ResizeImageByURL(URL OG,int width,int height) {
        var image = new ImageIcon(OG);

        return new ImageIcon(image.getImage().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));
    }
}
