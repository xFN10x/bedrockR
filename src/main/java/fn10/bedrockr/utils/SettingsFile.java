package fn10.bedrockr.utils;

import com.google.gson.JsonSyntaxException;
import fn10.bedrockr.addons.source.elementFiles.GlobalBuildingVariables;
import fn10.bedrockr.addons.source.elementFiles.WorkspaceFile;
import fn10.bedrockr.addons.source.interfaces.SourcelessElementFile;
import jakarta.annotation.Nonnull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class SettingsFile implements SourcelessElementFile {

    public String comMojangPath = "";
    public List<String> currentBPSynced = new ArrayList<>();
    public List<String> currentRPSynced = new ArrayList<>();
    public List<String> ignored = new ArrayList<>();

    public Long LastTimeBlockTexturesCachedPrismarineJSMCDataVersionID = null;

    @RAnnotation.HelpMessage("Whether or not you want to share the name of each element, you make. Along with the name of your workspace, the date, and the bedrockR version used to create the element.")
    @RAnnotation.FieldDetails(Optional = false, displayName = "Share Element & Workspace Data")
    @RAnnotation.Order(1)
    @RAnnotation.SettingsCategory(RAnnotation.SettingsCategory.SettingsCategorys.Network)
    public Boolean shareElementAndWorkspaceData = null;
    @RAnnotation.HelpMessage("If you want to share each workspace you create, and more data about the elements you make. Planned to be used for examples on the bedrockR Wiki.")
    @RAnnotation.FieldDetails(Optional = false, displayName = "Share Extra Data")
    @RAnnotation.Order(2)
    @RAnnotation.SettingsCategory(RAnnotation.SettingsCategory.SettingsCategorys.Network)
    public Boolean shareExtraData = null;

    public void save() {

        var json = gson.toJson(this);
        var path = new File(RFileOperations.getBaseDirectory().getPath() + File.separator + "settings.json").toPath();
        try {
            Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }

    }

    public static @Nonnull SettingsFile load() {
        var file = new File(RFileOperations.getBaseDirectory().getPath() + File.separator + "settings.json").toPath();
        try {
            if (!file.toFile().exists()) {
                new SettingsFile().save();
            }
            return gson.fromJson(new String(Files.readAllBytes(file)), SettingsFile.class);
        } catch (JsonSyntaxException | IOException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return new SettingsFile();
        }
    }

    @Override
    public void build(String rootPath, WorkspaceFile workspaceFile, String rootResPackPath, GlobalBuildingVariables globalResVaribles) throws IOException {

    }

    @Override
    public void setDraft(Boolean draft) {
    }

    @Override
    public Boolean getDraft() {
        return false;
    }
}
