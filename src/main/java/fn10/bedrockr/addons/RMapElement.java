package fn10.bedrockr.addons;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.supporting.ItemComponents;

public class RMapElement {
    public String ID;
    public Class<?> Type;
    public String DisplayName;
    public String HelpDescription;
    public List<MapValueFilter> Filters = new ArrayList<MapValueFilter>();

    public static enum MapValueFilter {
        NotNegitive
    }

    public static Map<String, RMapElement> LookupMap = new HashMap<String, RMapElement>();
    static {
        for (RMapElement element : ItemComponents.getPickable()) {
            LookupMap.put(element.ID, element);
        }
    }

    public String toString() {
        return ID;
    }

    public boolean equals(RMapElement compare) {
        return this.ID.equals(compare.ID);
    }

    public boolean Valid(Object value) {
        var log = Launcher.LOG;
        log.info(">---------=--------< Checking RMapElement " + this.DisplayName + "... >---------=--------<");
        for (MapValueFilter filter : Filters) {
            switch (filter) {
                case NotNegitive:
                    log.info(DisplayName + " : Checking if negitive");

                    if ((Double) value < 0) { // if its negitive
                        log.info(DisplayName + " : This is negitive, fail");

                        return false;
                    } else {
                        log.info(DisplayName + " : This is positive, succeed");

                    }
                    break;

                default:
                    break;
            }
        }
        return true;
    }

    public RMapElement(String ID, Class<?> Type) {
        this(ID, ID, Type, "No help is available for this element.");
    }

    public RMapElement(String Name, String ID, Class<?> Type) {
        this(Name, ID, Type, "No help is available for this element.");
    }

    public RMapElement(String ID, Class<?> Type, String Help) {
        this(ID, ID, Type, Help);
    }

    public RMapElement(String Name, String ID, Class<?> Type, String Help) {
        this.Type = Type;
        this.ID = ID;

        this.DisplayName = Name;
        this.HelpDescription = Help;
    }

    public RMapElement(String Name, String ID, Class<?> Type, String Help, @Nullable MapValueFilter... filters) {
        this.Type = Type;
        this.ID = ID;

        this.DisplayName = Name;
        this.HelpDescription = Help;
        this.Filters = List.of(filters);
    }
}
