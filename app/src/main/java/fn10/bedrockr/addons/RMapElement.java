package fn10.bedrockr.addons;

import java.lang.reflect.Type;

public class RMapElement {
    public String ID;
    public Type Type;
    public String DisplayName;
    public String HelpDescription;

    public RMapElement(String ID, Type Type) {
        this.Type = Type;
        this.ID = ID;

        this.DisplayName = ID;
        this.HelpDescription = "No help for this component.";
    }
}
