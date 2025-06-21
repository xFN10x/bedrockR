package fn10.bedrockr.addons.source.jsonClasses;

public class WPFile {
    public String WorkspaceName;
    public String MinimumEngineVersion;
     

    public WPFile(String WPName, String MEV) {
        this.WorkspaceName = WPName;
        this.MinimumEngineVersion = MEV;
    }
}
