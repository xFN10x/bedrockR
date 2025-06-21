package fn10.bedrockr.addons.source.jsonClasses;

public class WPFile {
    public String WorkspaceName;
    public String MinimumEngineVersion;
    public String Description;
    public String IconExtension;
     

    public WPFile(String WPName, String MEV, String DES, String IE) {
        this.WorkspaceName = WPName;
        this.MinimumEngineVersion = MEV;
        this.Description = DES;
        this.IconExtension = IE;
    }
}
