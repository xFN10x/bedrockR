package fn10.bedrockr.ui.components;

import java.awt.Dimension;

import javax.swing.SpringLayout;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.ui.base.RDetailedButton;

public class RMapElementViewer extends RDetailedButton {

    protected RMapElement Element;

    public RMapElementViewer(Runnable selectedFunction, RMapElement element) {
        super(selectedFunction, false);

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
