package fn10.bedrockr.windows;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.jsonClasses.WPFile;
import fn10.bedrockr.windows.base.RFrame;

public class RWorkspace extends RFrame {

    public RWorkspace(SourceWPFile WPF) {
        super(
                EXIT_ON_CLOSE,
                ((WPFile) WPF.getSerilized()).WorkspaceName,
                Toolkit.getDefaultToolkit().getScreenSize(),
                true,
                false);

        
    }
}
