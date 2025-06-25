package fn10.bedrockr.addons.source;

import javax.swing.ImageIcon;

public class ElementDetails {

    public String Description;
    public String Name;
    public ImageIcon Icon;
     
    public ElementDetails(String Name, String Desciption, ImageIcon Icon) {
        this.Name = Name;
        this.Description = Desciption;
        this.Icon = Icon;
    }
}
