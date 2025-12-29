package fn10.bedrockr.addons.source.interfaces;

import java.io.File;

import com.google.gson.Gson;

/**
 * the interface used for Source Classes of Elements. Element Sources are
 * responceable for most things that an element does. Like building to source,
 * and giving info to the UI.
 * <br/>
 * <br/>
 * Notice: You should add the method,
 * {@code public static ElementDetails getDetails()} if making a ElementSource
 * that is meant to be added as a workspace element.
 */
public abstract class ElementSource<T extends ElementFile<? extends ElementSource<T>>> {

    public static final Gson gson = ElementFile.gson;

    public String getJSONString() {
        return gson.toJson(getSerilized());
    }

    /**
     * THIS SHOULD NOT SET SERILIZED
     * 
     * @param jsonString the string, which is a json, that is serilized
     * @return the ElementFile.
     */
    public abstract T getFromJSON(String jsonString);

    public abstract File buildJSONFile(String workspace);

    public abstract Class<T> getSerilizedClass();

    /**
     * Gets the ElementFile linked to this ElementSource object.
     * 
     * @return the ElementFile
     */
    public abstract T getSerilized();

    public String toString() {
        try {
            ElementDetails details = ((ElementDetails) this.getClass().getMethod("getDetails").invoke(null));
            return details.Name + ", " + details.Description + "\n" + getJSONString();
        } catch (Exception e) {
            return "error";
        }
    }
}
