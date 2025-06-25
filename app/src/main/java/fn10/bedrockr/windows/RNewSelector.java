package fn10.bedrockr.windows;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fn10.bedrockr.addons.source.SourceWPFile;
import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.SpringUtilities;
import fn10.bedrockr.windows.base.RFrame;
import fn10.bedrockr.windows.componets.RElement;

public class RNewSelector extends RFrame {

    private JPanel MainPane = new JPanel();
    private JScrollPane MainScrollPane = new JScrollPane(MainPane);

    @SuppressWarnings("unchecked")
    public static final Class<? extends ElementSource>[] ELEMENTS = new Class[] {
            SourceWPFile.class
    };

    public RNewSelector() {
        super(
                DISPOSE_ON_CLOSE,
                "New Element",
                new Dimension(400, 500),
                false,
                false);

        MainPane.setLayout(new SpringLayout());

        

        for (Class<? extends ElementSource> class1 : ELEMENTS) {
            try {
                ElementDetails details = (ElementDetails)class1.getMethod("getDetails").invoke(this);

                MainPane.add(new RElement(details, () -> {
                    for (Component c : MainPane.getComponents()) {
                        ((RElement)c).unselect();
                    }
                }));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        SpringUtilities.makeCompactGrid(MainPane, ELEMENTS.length, 1, 5, 5, 5, 5);

        Lay.putConstraint(SpringLayout.EAST, MainScrollPane, -10, SpringLayout.EAST, getContentPane());
        Lay.putConstraint(SpringLayout.WEST, MainScrollPane, 10, SpringLayout.WEST, getContentPane());
        Lay.putConstraint(SpringLayout.NORTH, MainScrollPane, 10, SpringLayout.NORTH, getContentPane());
        Lay.putConstraint(SpringLayout.SOUTH, MainScrollPane, -60, SpringLayout.SOUTH, getContentPane());

        add(MainScrollPane);
    }
}
