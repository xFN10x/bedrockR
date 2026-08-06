package fn10.bedrockr.addons.element.interfaces;

import com.google.gson.*;
import fn10.bedrockr.addons.element.elementFiles.*;
import fn10.bedrockr.utils.RAnnotation.UneditableByCreation;
import fn10.bedrockr.utils.RFileOperations;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

import static fn10.bedrockr.utils.RFileOperations.gson;

public abstract class SourcelessElementFile {
    @UneditableByCreation
    public int ElementVersion = 0;
    @UneditableByCreation
    public boolean Draft = false;

    @UneditableByCreation
    public static final HashMap<Class<? extends SourcelessElementFile>, Integer> highestVersions = new HashMap<>(Map.of(
            BiomeFile.class, 0,
            BlockFile.class, 2,
            FoodFile.class, 1,
            ItemFile.class, 1,
            RecipeFile.class, 0,
            ScriptFile.class, 0
    ));

    public static SourcelessElementFile upToDate(String workspace, JsonObject fileJson, Class<? extends SourcelessElementFile> elementType) {
        JsonElement elementVerJElement = fileJson.get("ElementVersion");
        int oldver;
        if (elementVerJElement == null) {
            oldver = 0;
        } else {
            oldver = elementVerJElement.getAsInt();
        }
        return upToDate(workspace, fileJson, elementType, oldver, highestVersions.get(elementType));
    }
    public static SourcelessElementFile upToDate(String workspace, JsonObject fileJson, Class<? extends SourcelessElementFile> elementType, int oldVer, int newVer) {
        boolean upgrading = false;
        if (oldVer != newVer) {
            upgrading = true;
            RFileOperations.LOG.info("Upgrading: " + elementType.getSimpleName() + " from: " + oldVer + ", to: " + newVer);
            
            
        }
        SourcelessElementFile sef = gson.fromJson(fileJson, elementType);
        if (upgrading && sef instanceof ElementFile<?> ef) {
            try {
                ElementSource<? extends ElementFile<?>> src = ef.getNewSource();
                src.saveJSONFile(workspace);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sef;
    }
    
    public static SourcelessElementFile upgradeElement(int oldVer, int toUpgradeTo, JsonObject obj, Class<? extends SourcelessElementFile> targetClass) {
        throw new NotImplementedException();
    }

    public final void setDraft(Boolean draft) {
        this.Draft = draft;
    }

    public final Boolean getDraft() {
        return Draft;
    }
}
