package fn10.bedrockr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.awt.*;

public class SettingsFile {

    protected static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public String comMojangPath = "";
    public java.util.List<String> currentBPSynced = new ArrayList<String>();
    public java.util.List<String> currentRPSynced = new ArrayList<String>();
    public java.util.List<String> ignored = new ArrayList<String>();

    public void buildFile(Window doingThis) {

        var json = gson.toJson(this);
        var path = new File(RFileOperations.getBaseDirectory(doingThis).getPath() +File.separator+"settings.json").toPath();
        try {
            Files.write(path, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Failed to save global settings", "IO Error", e);
        }

    }

    public static SettingsFile getSettings(Window doingThis) {
        var file = new File(RFileOperations.getBaseDirectory(doingThis).getPath() +File.separator+"settings.json").toPath();
        try {
            if (!file.toFile().exists()) {
                new SettingsFile().buildFile(doingThis);
            }
            return gson.fromJson(Files.readString(file), SettingsFile.class);
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
            ErrorShower.showError(doingThis, "Failed to get global settings", "IO Error", e);
            return null;
        }
    }

}
