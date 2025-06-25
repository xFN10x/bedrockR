package fn10.bedrockr.addons.source;

import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface ElementSource {
    final GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().create();

    final Integer Type = 0; // 0 for BP, 1 for RP

    public static ElementDetails getDetails() {
        return new ElementDetails("Element", "A cool new element, which SOME dumbass forgot to change.",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Element.png")));
    }

    String getJSONString();

    Object getFromJSON(String jsonString);

    File buildJSONFile(Component doingThis, String workspace);

    Class getSerilizedClass();

    Object getSerilized();
}
