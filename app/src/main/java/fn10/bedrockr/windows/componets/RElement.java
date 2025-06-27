package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;

import javax.print.attribute.standard.MediaSize.NA;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.utils.ImageUtilites;
import fn10.bedrockr.utils.RFonts;

public class RElement extends JPanel implements MouseListener {

    private JLabel Icon = new JLabel();
    private SpringLayout Lay = new SpringLayout();

    private Runnable func;
    private boolean selected;
    private Class<? extends ElementSource> clasz;
    private ElementDetails details;

    public RElement(Class<? extends ElementSource> clazz, Runnable selectedFunction)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        super();

        this.func = selectedFunction;
        this.clasz = clazz;

        details = (ElementDetails) clazz.getMethod("getDetails").invoke(null);

        setLayout(Lay);

        setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white, 1, 16));
        setPreferredSize(new Dimension(354, 80));
        setMaximumSize(new Dimension(354, 80));

        Icon.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.gray, 1, 16));
        Icon.setPreferredSize(new Dimension(70, 70));
        Icon.setSize(new Dimension(70, 70));
        Icon.setIcon(ImageUtilites.ResizeIcon(details.Icon, 64, 64));
        Icon.setAlignmentX(CENTER_ALIGNMENT);
        Icon.setAlignmentY(CENTER_ALIGNMENT);

        var Name = new JLabel(details.Name);
        Name.setFont(RFonts.RegMinecraftFont.deriveFont(20));

        var Desc = new JLabel(details.Description);

        Lay.putConstraint(SpringLayout.WEST, Icon, 5, SpringLayout.WEST, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Icon, 0, SpringLayout.VERTICAL_CENTER, this);

        Lay.putConstraint(SpringLayout.NORTH, Name, 5, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.WEST, Name, 5, SpringLayout.EAST, Icon);

        Lay.putConstraint(SpringLayout.NORTH, Desc, 5, SpringLayout.SOUTH, Name);
        Lay.putConstraint(SpringLayout.WEST, Desc, 5, SpringLayout.EAST, Icon);

        add(Icon);
        add(Name);
        add(Desc);

        addMouseListener(this);
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void unselect() {
        this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white, 1, 16));
        this.selected = false;
    }
    
    public Class<? extends ElementSource> getElement() {
        return clasz;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        func.run();
        selected = true;
        this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.green, 3, 16));
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (!selected) {
            this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.green, 1, 16));
        } else
            this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.green, 4, 16));
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if (!selected) {
            unselect();
        } else {
            this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.green, 3, 16));
        }
    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

    }
}
