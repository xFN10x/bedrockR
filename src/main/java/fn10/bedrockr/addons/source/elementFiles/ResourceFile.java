package fn10.bedrockr.addons.source.elementFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.source.SourceResourceElement;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.utils.RFileOperations;

public class ResourceFile implements ElementFile<SourceResourceElement> {

    /**
     * Key is the file name.
     * Value is the type
     */
    public Map<String, Integer> ResourceTypes = new HashMap<String, Integer>();
    /**
     * Key is the file name.
     * Value is the UUID.
     */
    public Map<String, String> ResourceIDs = new HashMap<String, String>();

    public static final int ITEM_TEXTURE = 0;
    public static final int BLOCK_TEXTURE = 1;
    public static final int STRUCTURE_FILE = 2;

    public File getFileOfResource(String workspaceName, String file, int resourceType)
            throws FileNotFoundException, IllegalAccessError {
        File dest = new File(
                RFileOperations.getBaseDirectory(File.separator + "workspace" + File.separator).getPath()
                        + File.separator + workspaceName + File.separator + "resources" + File.separator
                        + file);
        if (ResourceTypes.get(file) != null && dest.exists()) {
            if (ResourceTypes.get(file) != resourceType)
                throw new IllegalAccessError(
                        "The resource, '" + file + "' is not the resource type '" + resourceType + "'");
                return dest;
        } else
            throw new FileNotFoundException("The resource, '" + file + "' does not exist.");
    }

    /**
     * Get a resource
     * 
     * @param workspaceName
     * @param file
     * @param resourceType
     * @return A string, which is the (UU)ID of the resource.
     * @throws FileNotFoundException If the resource isnt found.
     * @throws IllegalAccessError    If the requested doesnt match the resource
     *                               type.
     */
    public String getResource(String workspaceName, String file, int resourceType)
            throws FileNotFoundException, IllegalAccessError {
        var dest = new File(
                RFileOperations.getBaseDirectory(File.separator + "workspace" + File.separator).getPath()
                        + File.separator + workspaceName + File.separator + "resources" + File.separator
                        + file);
        if (ResourceTypes.get(file) != null) {
            if (ResourceTypes.get(file) != resourceType)
                throw new IllegalAccessError(
                        "The resource, '" + file + "' is not the resource type '" + resourceType + "'");
            if (dest.exists())
                return ResourceIDs.get(file);
            else
                throw new FileNotFoundException("The resource, '" + file + "' does not exist.");
        } else
            throw new FileNotFoundException("The resource, '" + file + "' does not exist.");

    }

    public boolean importTexture(File file, int Type, String workspaceName) {
        return addTexture(file.getName(), file, Type, workspaceName);
    }

    public boolean addTexture(String name, File filePNG, int type, String workspaceName) {
        if (type != ITEM_TEXTURE || type != BLOCK_TEXTURE)
            throw new IllegalAccessError("Resource Type: " + type + " is not a texture ")
        try {
            if (name == null)
                return false;
            String finalName = (((String) name).contains(".png") ? name.toString() : name + ".png");

            File dest = java.nio.file.Paths.get(RFileOperations.getBaseDirectory("workspace").getPath(), workspaceName,
                    "resources", finalName).toFile();

            FileUtils.copyFile(filePNG, dest);
            this.ResourceTypes.put(finalName, type);
            this.ResourceIDs.put(finalName, UUID.randomUUID().toString());
            build(workspaceName, null, null, null);
            return true;
        } catch (Exception e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return false;
        }
    }

    @Override
    /**
     * NOTE!!!!!!!!!
     * rootPath is the workspace name!
     * THIS IS ALSO NOT MEANT FOR BUILDING TO PACKS
     */
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath,
            GlobalBuildingVariables globalResVaribles) throws IOException {
        /*
         * if (ActiveWorkspace != null)
         * ActiveWorkspace.refreshResources();
         */

        var source = new SourceResourceElement(this);
        source.saveJSONFile(rootPath);
    }

    @Override
    public Class<SourceResourceElement> getSourceClass() {
        return SourceResourceElement.class;
    }

    @Override
    public String getElementName() {
        return "Resouces";
    }

    @Override
    public void setDraft(Boolean draft) {
        return;
    }

    @Override
    public Boolean getDraft() {
        return false;
    }

}
