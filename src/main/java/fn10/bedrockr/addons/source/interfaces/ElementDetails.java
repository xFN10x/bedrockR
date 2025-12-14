package fn10.bedrockr.addons.source.interfaces;

public class ElementDetails {

    public String Description;
    public String Name;
    public byte[] Icon;
     
    public ElementDetails(String Name, String Desciption, byte[] Icon) {
        this.Name = Name;
        this.Description = Desciption;
        this.Icon = Icon;
    }
}
