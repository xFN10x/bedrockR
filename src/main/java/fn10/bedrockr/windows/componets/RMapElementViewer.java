package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SpringLayout;

import fn10.bedrockr.addons.RMapElement;

public class RMapElementViewer extends RElement {

    protected RMapElement Element;

    public RMapElementViewer(Runnable selectedFunction, RMapElement element)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        super(null, selectedFunction, Color.green, false);

        this.Element = element;

        //took from org.codehaus.plexus.util.StringUtils
        String str = element.HelpDescription;
        String sub = "<br/>";
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }

        setPreferredSize(new Dimension(100, 80));
        setMaximumSize(new Dimension(354, 35 + Desc.getFontMetrics(Desc.getFont()).getHeight()
                * (count + 1)));

        Lay.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, Desc);

        Name.setText(element.DisplayName);
        Desc.setText(element.HelpDescription);
    }

    public RMapElement getMapElement() {
        return Element;
    }

}
