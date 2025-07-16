package fn10.bedrockr.addons.source.interfaces;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;

import javax.swing.ImageIcon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

public interface ElementSource {
    final GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().create();

    final Dimension defaultSize = new Dimension(800, 450);

    // final Integer Type = 0; // 0 for BP, 1 for RP

    public static ElementDetails getDetails() {
        return new ElementDetails("Element", "A cool new element, \nwhich SOME dumbass forgot to change.",
                new ImageIcon(ElementSource.class.getResource("/addons"+"/element"+"/Element.png")));
    }

    abstract String getJSONString();

    abstract ElementFile getFromJSON(String jsonString);

    abstract File buildJSONFile(Frame doingThis, String workspace);

    abstract Class<?> getSerilizedClass();

    abstract ElementFile getSerilized();

    abstract RElementEditingScreen getBuilderWindow(Frame Parent, ElementCreationListener parent);

    /**
     * You should use this instad of toString()
     * 
     * @return The name, description, and the JSON of the element file.
     */
    default String ToString() {
        try {
            ElementDetails details = ((ElementDetails) this.getClass().getMethod("getDetails").invoke(null));
            return details.Name + ", " + details.Description + "\n" + getJSONString();
        } catch (Exception e) {
            return "error";
        }
    }
}
