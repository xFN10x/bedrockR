package fn10.bedrockr.addons.source.interfaces;

import java.io.File;

import javax.swing.ImageIcon;
import com.google.gson.Gson;

import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.windows.RElementEditingScreen;

/**
 * the interface used for Source Classes of Elements. Element Sources are responceable for most things that an element does. Like building to source, and giving info to the UI.
 * <br/><br/>
 * Notice: You should add the method, {@code public static ElementDetails getDetails()} if making a ElementSource that is meant to be added as a workspace element.
 */
public interface ElementSource<T extends ElementFile<? extends ElementSource<T>>> {

    public static final Gson gson = ElementFile.gson;

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
    abstract T getFromJSON(String jsonString);

    abstract File buildJSONFile(String workspace);

    abstract Class<T> getSerilizedClass();

    /**
     * Gets the ElementFile linked to this ElementSource object.
     * @return the ElementFile
     */
    abstract T getSerilized();

    abstract RElementEditingScreen getBuilderWindow(ElementCreationListener parent, String Workspace);

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
