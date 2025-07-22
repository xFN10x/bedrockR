package fn10.bedrockr.addons.source.jsonClasses;

import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import fn10.bedrockr.addons.source.SourceResourceFile;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ErrorShower;
import fn10.bedrockr.utils.RFileOperations;

public class ResourceFile implements ElementFile {

    public Map<String, Integer> Files = new HashMap<String, Integer>();

    public static final int ITEM_TEXTURE = 0;

    /**
     * Get a resource 
     * @param doingThis
     * @param workspaceName
     * @param file
     * @param resourceType
     * @return The file that was found.
     * @throws FileNotFoundException If the resource isnt found.
     * @throws IllegalAccessError If the requested doesnt match the resource type.
     */
    public File getResource(Frame doingThis, String workspaceName, String file, int resourceType)
            throws FileNotFoundException, IllegalAccessError {
        var dest = new File(
                RFileOperations.getBaseDirectory(doingThis, File.separator + "workspace" + File.separator).getPath()
                        + File.separator + workspaceName + File.separator + "resources" + File.separator
                        + file);
        if (Files.get(file) != null) {
            if (Files.get(file) != resourceType)
                throw new IllegalAccessError(
                        "The resource, '" + file + "' is not the resource type '" + resourceType + "'");
            if (dest.exists())
                return dest;
            else
                throw new FileNotFoundException("The resource, '" + file + "' does not exist.");
        } else
            throw new FileNotFoundException("The resource, '" + file + "' does not exist.");

    }

    public boolean importTexture(Frame doingThis, int Type, String workspaceName) {
        var file = new JFileChooser();
        file.setFileSelectionMode(JFileChooser.FILES_ONLY);
        file.setFileFilter(new FileNameExtensionFilter("PNG Image Files (*.png)", "png"));

        if (file.showOpenDialog(doingThis) != JFileChooser.APPROVE_OPTION)
            return false;
        return addTexture(doingThis, file.getSelectedFile(), Type, workspaceName);
    }

    public boolean addTexture(Frame doingThis, File filePNG, int type, String workspaceName) {
        try {
            var input = JOptionPane.showInputDialog(doingThis,
                    "What do you want to name this texture? (" + filePNG.getName() + ")",
                    "Name Texture", JOptionPane.INFORMATION_MESSAGE, null, null, filePNG.getName());
            String finalName = (((String) input).contains(".png") ? input.toString() : input + ".png"); // if it
                                                                                                        // includes png,
                                                                                                        // dont add it,
                                                                                                        // if else, add
                                                                                                        // it

            var dest = new File(
                    RFileOperations.getBaseDirectory(doingThis, File.separator + "workspace" + File.separator).getPath()
                            + File.separator + workspaceName + File.separator + "resources" + File.separator
                            + finalName);
            if (dest.exists()) {
                JOptionPane.showMessageDialog(doingThis, "Resource already exist. Please rename it.", "Naming Error",
                        type);
                return addTexture(doingThis, filePNG, type, workspaceName);
            }
            FileUtils.copyFile(filePNG, dest);
            this.Files.put(finalName, type);
            build(workspaceName, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Failed to add resource.", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Class<? extends ElementSource> getSourceClass() {
        return null;
    }

    @Override
    public String getElementName() {
        return "Resource(s)";
    }

    @Override
    public void setDraft(Boolean draft) {
        return;
    }

    @Override
    public Boolean getDraft() {
        return false;
    }

    @Override
    /**
     * NOTE!!!!!!!!!
     * rootPath is the workspace name!
     */
    public void build(String rootPath, WPFile workspaceFile) throws IOException {
        var source = new SourceResourceFile(this);
        source.buildJSONFile(null, rootPath);
    }

}
