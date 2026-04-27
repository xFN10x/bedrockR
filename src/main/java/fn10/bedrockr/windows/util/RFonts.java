package fn10.bedrockr.windows.util;

import jakarta.annotation.Nonnull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;


public class RFonts {

    public static Font RegMinecraftFont = SetupFont("/ui/newFont.otf", 14);

    protected static Font SetupFont(@Nonnull String Path, int Size) {
        InputStream font_file;
        try {
            font_file = RFonts.class.getResourceAsStream(Path);
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(null, "Failed to open font file", "Font Error", e);
            return new Font(Font.SANS_SERIF, Font.PLAIN,14);
        }

        Font font;
        try { // try setting font
            font = Font.createFont(Font.TRUETYPE_FONT, font_file).deriveFont(1, Size);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font); // reg font if not null
        } catch (IOException | FontFormatException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            ErrorShower.showError(null, "Failed to load font.", "Font Error", e);
            return new Font(Font.SANS_SERIF, Font.PLAIN,14);
        }

        return font;
    }

}
