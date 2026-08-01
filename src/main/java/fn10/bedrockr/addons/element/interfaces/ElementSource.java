package fn10.bedrockr.addons.element.interfaces;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import fn10.bedrockr.utils.RFileOperations;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import static fn10.bedrockr.utils.RFileOperations.gson;

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
    @Nonnull
    private final T serialized;
    
    public abstract ElementDetails getDetails();

    public ElementSource(@Nonnull T serialized) {
        this.serialized = serialized;
    }

    public String getJSONString() {
        return gson.toJson(getSerialized());
    }

    /**
     * @param jsonString the string, which is a json, that is serialized
     * @return the ElementFile.
     */
    public static <E extends ElementFile<? extends ElementSource<E>>> E getFromJSON(String jsonString, Class<E> clazz) {
        return gson.fromJson(jsonString, clazz);
    }

    /**
     * @param jsonString the string, which is a json, that is serialized
     * @return the ElementFile.
     */
    public static <E extends ElementFile<? extends S>, S extends ElementSource<E>> S getSourceFromJSON(String jsonString, Class<S> clazz, Class<E> elementClass) {
        try {
            return clazz.getConstructor(elementClass).newInstance(getFromJSON(jsonString, elementClass));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the location of where the ElementFile linked to this will save. This
     * path doesn't need to exist.
     * 
     * @param workspace The workspace that this element is in
     * @return the File, which may or may not exist, being that of where the
     *         ElementFile will save.
     */
    public @Nonnull File getLocation(String workspace) throws IOException {
        return RFileOperations.getFileFromWorkspace(workspace,
                "elements", getSerialized().getElementName() + getFileExtension());
    }

    /**
     * Get the file extension of the serialized on disk.
     * @return The extension in full. e.g. ".biomeref"
     */
    public abstract String getFileExtension();

    public File saveJSONFile(String workspace) throws IOException {
        File saveLoc = getLocation(workspace);
        return Files.writeString(saveLoc.toPath(), getJSONString(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE).toFile();
    }

    public abstract Class<T> getSerilizedClass();

    /**
     * Gets the ElementFile linked to this ElementSource object.
     * 
     * @return the ElementFile
     */
    public @NonNull T getSerialized() {
        return serialized;
    }

    public String toString() {
        try {
            ElementDetails details = ((ElementDetails) this.getClass().getMethod("getDetails").invoke(null));
            return details.Name + ", " + details.Description + "\n" + getJSONString();
        } catch (Exception e) {
            return "error";
        }
    }
}
