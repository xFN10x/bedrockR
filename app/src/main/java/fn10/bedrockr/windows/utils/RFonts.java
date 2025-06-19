package fn10.bedrockr.windows.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.google.common.base.Converter;

import fn10.bedrockr.Launcher;

public class RFonts {

    public static Font RegMinecraftFont = SetupFont("/branding/font.otf",14);
    public static Font BoldMinecraftFont = SetupFont("/branding/font.otf",14);


    protected static Font SetupFont(@Nonnull String Path,int Size) {
        File font_file = new File(Launcher.class.getResource(Path).getPath());

        Font font = null;
        try { // try setting font
            font = Font.createFont(Font.TRUETYPE_FONT, font_file).deriveFont(1,Size);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font); // reg font if not null
        return font;
    }

}
