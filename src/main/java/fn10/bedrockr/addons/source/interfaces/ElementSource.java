package fn10.bedrockr.addons.source.interfaces;

import java.awt.Dimension;
import java.awt.Window;
import java.io.File;

import javax.swing.ImageIcon;
import com.google.gson.Gson;
import fn10.bedrockr.windows.RElementEditingScreen;
import fn10.bedrockr.windows.interfaces.ElementCreationListener;

/**
 * the interface used for Source Classes of Elements. Element Sources are responceable for most things that an element does. Like building to source, and giving info to the UI.
 * 
 * @apiNote You should add the method, {@code public static ElementDetails getDetails()} if making a ElementSource that is meant to be added as a workspace element.
 */
public interface ElementSource {

    public static final Gson gson = ElementFile.gson;

    final Dimension defaultSize = new Dimension(800, 450);

    // final Integer Type = 0; // 0 for BP, 1 for RP

    public static ElementDetails getDetails() {
        return new ElementDetails("Element", "A cool new element, \nwhich SOME dumbass forgot to change.",
                new ImageIcon(ElementSource.class.getResource("/addons"+"/element"+"/Element.png")));
    }

    abstract String getJSONString();

    /**
     * THIS SHOULD NOT SET SERILIZED
     * @param jsonString the string, which is a json, that is serilized
     * @return the ElementFile.
     */
    abstract ElementFile getFromJSON(String jsonString);

    abstract File buildJSONFile(Window doingThis, String workspace);

    abstract Class<?> getSerilizedClass();

    /**
     * Gets the ElementFile linked to this ElementSource object.
     * @return the ElementFile
     */
    abstract ElementFile getSerilized();

    abstract RElementEditingScreen getBuilderWindow(Window Parent, ElementCreationListener parent, String Workspace);

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
