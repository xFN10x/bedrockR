package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import fn10.bedrockr.addons.RMapElement;

public class RMapElementViewer extends RElement {

    protected RMapElement Element;

    public RMapElementViewer(Runnable selectedFunction, RMapElement element)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        super(null, selectedFunction, Color.green,false);

        this.Element = element;

        setPreferredSize(new Dimension(100, 50));

        Name.setText(element.DisplayName);
        Desc.setText(element.HelpDescription);
    }

    public RMapElement getMapElement() {
        return Element;
    }

}
