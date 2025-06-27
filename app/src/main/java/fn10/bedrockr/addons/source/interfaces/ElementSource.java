package fn10.bedrockr.addons.source.interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;

import javax.swing.ImageIcon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fn10.bedrockr.windows.RElementCreationScreen;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public interface ElementSource {
    final GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().create();

    final Dimension defaultSize = new Dimension(800, 450);

    final Integer Type = 0; // 0 for BP, 1 for RP

    public static ElementDetails getDetails() {
        return new ElementDetails("Element", "A cool new element, \nwhich SOME dumbass forgot to change.",
                new ImageIcon(ElementSource.class.getResource("/addons/element/Element.png")));
    }


    abstract String getJSONString();

    abstract Object getFromJSON(String jsonString);

    abstract File buildJSONFile(Component doingThis, String workspace);

    abstract Class getSerilizedClass();

    abstract Object getSerilized();

    abstract RElementCreationScreen getBuilderWindow(Frame Parent, ElementCreationListener parent);
}
