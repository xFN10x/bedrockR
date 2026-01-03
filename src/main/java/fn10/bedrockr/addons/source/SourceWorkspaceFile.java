package fn10.bedrockr.addons.source;

import java.io.File;
import java.io.IOException;

import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.RFileOperations;

public class SourceWorkspaceFile extends ElementSource<WorkspaceFile> {
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
        try {
            return new ElementDetails("Workspace File", "dont use this cause it will break",
                    RFileOperations.readAllBytes(ElementSource.class.getResource("/addons"
                            + "/element" + "/Element.png").openStream()));
        } catch (IOException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
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
    public WorkspaceFile getSerilized() {
        return this.serilized;
    }

    @Override
    public File getLocation(String workspace) {
        return RFileOperations.getFileFromWorkspace(workspace, Location);
    }
}
