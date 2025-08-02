package fn10.bedrockr.addons;

public class RMapElement {
    public String ID;
    public Class<?> Type;
    public String DisplayName;
    public String HelpDescription;

    public RMapElement(String ID, Class<?> Type) {
        this(ID,ID,Type, "No help is available for this element.");
    }

    public RMapElement(String Name, String ID, Class<?> Type) {
        this(Name,ID,Type, "No help is available for this element.");
    }

    public RMapElement(String ID, Class<?> Type, String Help) {
        this(ID,ID,Type, Help);
    }

    public RMapElement(String Name, String ID, Class<?> Type,String Help) {
        this.Type = Type;
        this.ID = ID;

        this.DisplayName = Name;
        this.HelpDescription = Help;
    }
}
