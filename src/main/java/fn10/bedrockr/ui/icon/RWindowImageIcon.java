package fn10.bedrockr.ui.icon;

import com.formdev.flatlaf.icons.FlatWindowAbstractIcon;
import fn10.bedrockr.ui.util.ImageUtilities;

import java.awt.*;

public class RWindowImageIcon extends FlatWindowAbstractIcon {
    private final String path;

    public RWindowImageIcon(String path) {
        super(null);
        this.path = path;
    }

    @Override
    protected void paintIconAt1x(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
        int cw = (int) ((width / 2d) - (6 * scaleFactor));
        int ch = (int) ((height / 2d) - (6 * scaleFactor));
        ImageUtilities.toScaled(path).paintIcon(null, g, cw, ch);
    }
}
