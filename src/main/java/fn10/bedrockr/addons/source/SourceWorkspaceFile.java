package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.FileWriter;


import javax.swing.ImageIcon;

import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.interfaces.ElementCreationListener;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.windows.RElementEditingScreen;
import jakarta.annotation.Nullable;

public class SourceWorkspaceFile implements ElementSource<WorkspaceFile> {
    private final String Location = File.separator + RFileOperations.WPFFILENAME;
    private WorkspaceFile serilized;

    public String workspaceName() {
        return serilized.WorkspaceName;
    }

    public SourceWorkspaceFile(WorkspaceFile obj) {
        this.serilized = obj;
    }

    public SourceWorkspaceFile() {
        this.serilized = null;
    }

    public SourceWorkspaceFile(String jsonString) {
        this.serilized = (WorkspaceFile) getFromJSON(jsonString);
    }

    public static ElementDetails getDetails() {
        return new ElementDetails("Workspace File", "dont use this cause it will break",
                new ImageIcon(ElementSource.class.getResource("/addons"
                        + "/element" + "/Element.png")));
    }

    @Override
    public String getJSONString() {
        return gson.toJson(serilized);
    }

    @Override
    public Class<WorkspaceFile> getSerilizedClass() {
        return WorkspaceFile.class;
    }

    @Override
    public WorkspaceFile getFromJSON(String jsonString) {
        return gson.fromJson(jsonString, getSerilizedClass());
    }

    @Override
    @Nullable
    public File buildJSONFile(String workspace) {
        var string = getJSONString();
        var file = RFileOperations.getFileFromWorkspace(workspace, Location);
        file.setWritable(true);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(string);
            fileWriter.close();
            return file;
        } catch (Exception e) {
            fn10.bedrockr.Launcher.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

    @Override
    public WorkspaceFile getSerilized() {
        return this.serilized;
    }

    @Override
    public RElementEditingScreen getBuilderWindow(ElementCreationListener parent, String Workspace) {
        return null;
    }

}
