package fn10.bedrockr.windows;

import java.awt.*;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import fn10.bedrockr.windows.base.RDialog;

public class RMapValueAddingSelector extends RDialog {

    protected JScrollPane selector = new JScrollPane();

    public RMapValueAddingSelector(Frame parent) {
        super(
                parent,
                JDialog.DISPOSE_ON_CLOSE,
                "New Item",
                new Dimension(330, 400));
        

    }

}
