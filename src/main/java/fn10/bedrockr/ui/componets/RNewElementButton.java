package fn10.bedrockr.ui.componets;

import fn10.bedrockr.addons.element.interfaces.ElementDetails;
import fn10.bedrockr.addons.element.interfaces.ElementSource;
import fn10.bedrockr.ui.laf.BedrockrDark;
import fn10.bedrockr.ui.util.ImageUtilities;

public class RNewElementButton extends RDetailedButton {

    private final ElementSource<?> elementSource;

    public RNewElementButton(ElementSource<?> source, Runnable selectedFunction) {
        super(selectedFunction, BedrockrDark.BEDROCKR_GREEN, true);
        this.elementSource = source;
        ElementDetails details = source.getDetails();
        
        Icon.setIcon(ImageUtilities.ResizeIcon(ImageUtilities.getElementIcon(source), 64, 64));
        Name.setText(details.Name);
        Desc.setText(details.Description);
    }

    public ElementSource<?> getElement() {
        return elementSource;
    }

}
