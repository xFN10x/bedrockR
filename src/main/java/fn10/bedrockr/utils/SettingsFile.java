package fn10.bedrockr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public class SettingsFile {

    protected static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public String comMojangPath = "";
    public List<String> currentBPSynced = new ArrayList<String>();
    public List<String> currentRPSynced = new ArrayList<String>();
    public List<String> ignored = new ArrayList<String>();
    public Long LastTimeBlockTexturesCachedPrismarineJSMCDataVersionID = null;

    public void save() {

        var json = gson.toJson(this);
        var path = new File(RFileOperations.getBaseDirectory().getPath() +File.separator+"settings.json").toPath();
        try {
            Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }

    }

    public static SettingsFile load() {
        var file = new File(RFileOperations.getBaseDirectory().getPath() +File.separator+"settings.json").toPath();
        try {
            if (!file.toFile().exists()) {
                new SettingsFile().save();
            }
            return gson.fromJson(new String(Files.readAllBytes(file)), SettingsFile.class);
        } catch (JsonSyntaxException | IOException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            return null;
        }
    }

}
