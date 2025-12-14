package fn10.bedrockr.windows.util;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

public class RFonts {

    public static Font RegMinecraftFont = SetupFont("/ui/font.otf", 14);
    public static Font BoldMinecraftFont = SetupFont("/ui/font.otf", 14);

    protected static Font SetupFont(@Nonnull String Path, int Size) {
        InputStream font_file;
        try {
            font_file = RFonts.class.getResourceAsStream(Path);
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(null, "Failed to open font file", "Font Error", e);
            return null;
        }

        Font font = null;
        try { // try setting font
            font = Font.createFont(Font.TRUETYPE_FONT, font_file).deriveFont(1, Size);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font); // reg font if not null
        } catch (IOException | FontFormatException e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(null, "Failed to load font.", "Font Error", e);
        }

        return font;
    }

}
