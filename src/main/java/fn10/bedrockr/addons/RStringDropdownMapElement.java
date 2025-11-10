package fn10.bedrockr.addons;

public class RStringDropdownMapElement extends RMapElement {

    private final String[] choices;

    public RStringDropdownMapElement(String Name, String ID, String Help, String... choices) {
        super(Name, ID, RStringDropdownMapElement.class, Help);
        this.choices = choices;
    }

    public String[] getChoices() {
        return choices;
    }

}
