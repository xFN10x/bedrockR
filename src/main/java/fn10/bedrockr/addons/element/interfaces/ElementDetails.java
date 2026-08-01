package fn10.bedrockr.addons.element.interfaces;

public class ElementDetails {

    public String Description;
    public String Name;
    public String Icon;

    public ElementDetails(String Name, String Desciption) { 
        this(Name,Desciption,Name);
    }
    public ElementDetails(String Name, String Desciption, String Icon) {
        this.Name = Name;
        this.Description = Desciption;
        this.Icon = Icon;
    }
}
