package fn10.bedrockr.installer;

import com.google.gson.*;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

import java.nio.file.Path;
import java.util.zip.InflaterInputStream;

public record InstallInfo(String ver, String semVer, int numVer, int jdkLevel) {
    private static final Gson gson = new GsonBuilder().create();    
    public static InstallInfo get() {
        JsonObject json = JsonParser.parseString(RIFileUtils.readResAsString("data.json")).getAsJsonObject();
        
        return new InstallInfo(json.get("version").getAsString(), json.get("winVer").getAsString(), json.get("numVer").getAsInt() ,Integer.parseInt(json.get("jdkVer").getAsString().substring(0,2)));
    }
}