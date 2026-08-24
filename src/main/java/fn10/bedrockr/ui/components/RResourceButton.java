package fn10.bedrockr.ui.components;

import fn10.bedrockr.addons.resource.interfaces.Resource;
import fn10.bedrockr.ui.base.RDetailedButton;

import javax.swing.*;

public class RResourceButton extends RDetailedButton {
    public final Runnable onClick = () -> {
    };

    public RResourceButton(Resource res) {
        super();
        Name.setText(res.Name);
        setIcon(new ImageIcon(res.getResourceIcon()), false);
    }
}
