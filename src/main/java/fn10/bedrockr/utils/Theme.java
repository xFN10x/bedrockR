package fn10.bedrockr.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import fn10.bedrockr.windows.laf.BedrockrDark;
import fn10.bedrockr.windows.laf.BedrockrLight;

import java.util.ArrayList;

public enum Theme {
    BedrockRDark("bedrockR Dark", BedrockrDark::setup),
    BedrockRLight("bedrockR Light",BedrockrLight::setup),
    Dark("Dark", FlatDarkLaf::setup),
    Light("Light", FlatLightLaf::setup);

    public static Theme getThemeFromName(String name) {
        for (Theme theme : values()) {
            if (theme.name().equalsIgnoreCase(name)) {return theme;}
        }
        return Theme.BedrockRDark;
    }

    public static String[] getNames() {
        ArrayList<String> list = new ArrayList<>();
        for (Theme theme : values()) {
            list.add(theme.name);
        }
        return list.toArray(new String[0]);
    }

    private final Runnable setup;
    private final String name;

    public String getName() {
        return name;
    }

    public void setupTheme() {
        setup.run();
    }

    Theme(String name, Runnable setup) {
        this.name = name;
        this.setup = setup;
    }
}
