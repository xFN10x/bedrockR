package fn10.bedrockr.addons.source.jsonClasses;

import fn10.bedrockr.utils.RAnnotation.HelpMessage;

public class WPFile {
    @HelpMessage(message = "You should never be able to see this")
    public String WorkspaceName;
    @HelpMessage(message = "You should never be able to see this")
    public String MinimumEngineVersion;
    @HelpMessage(message = "You should never be able to see this")
    public String Description;
    @HelpMessage(message = "You should never be able to see this")
    public String IconExtension;
    
     

    public WPFile(String WPName, String MEV, String DES, String IE) {
        this.WorkspaceName = WPName;
        this.MinimumEngineVersion = MEV;
        this.Description = DES;
        this.IconExtension = IE;
    }
}
