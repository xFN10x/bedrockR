package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.formdev.flatlaf.ui.FlatLineBorder;

import fn10.bedrockr.addons.source.interfaces.ElementDetails;
import fn10.bedrockr.addons.source.interfaces.ElementSource;
import fn10.bedrockr.windows.util.ImageUtilites;
import fn10.bedrockr.windows.util.RFonts;
import jakarta.annotation.Nullable;

public class RElement extends JPanel implements MouseListener {

    public JLabel Icon = new JLabel();
    protected SpringLayout Lay = new SpringLayout();
    public JLabel Name = new JLabel();
    public JLabel Desc = new JLabel();

    private Runnable func;
    protected boolean selected;
    protected Color outlineColour = Color.green;
    private Class<? extends ElementSource<?>> clasz;
    private ElementDetails details;
    public boolean CanBeSelected = true;

    public RElement(Class<? extends ElementSource<?>> clazz, Runnable selectedFunction)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        this(clazz, selectedFunction, Color.green);
    }

    public RElement(@Nullable Class<? extends ElementSource<?>> clazz, Runnable selectedFunction, Color borderColour)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        this(clazz, selectedFunction, borderColour, true);
    }

    /**
     * A ui-component with an icon, name and description. Can be easily changed to
     * make it custom.
     * 
     * @param clazz
     *                         Used for unmodified versions of this, this can be
     *                         <code>null</code> if you want to customize it.
     * @param selectedFunction
     *                         Runs before making this selected. Used for managing
     *                         how many you can select in a list for example.
     * @param borderColour
     *                         Default is <code>Color.green</code>
     * @param icon
     *                         Declares if this has an icon.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected RElement(@Nullable Class<? extends ElementSource<?>> clazz, Runnable selectedFunction, Color borderColour,
            boolean icon)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        super();

        this.outlineColour = borderColour;
        this.func = selectedFunction;
        if (clazz != null) {
            this.clasz = clazz;

            details = (ElementDetails) clazz.getMethod("getDetails").invoke(null);
        }
        setLayout(Lay);

        setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white, 1, 16));
        setPreferredSize(new Dimension(350, 80));
        setMaximumSize(new Dimension(350, 80));

        Icon.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.gray, 1, 16));
        Icon.setPreferredSize(new Dimension(70, 70));
        Icon.setSize(new Dimension(70, 70));
        if (clazz != null)
            Icon.setIcon(ImageUtilites.ResizeIcon(details.Icon, 64, 64));
        Icon.setAlignmentX(CENTER_ALIGNMENT);
        Icon.setAlignmentY(CENTER_ALIGNMENT);
        if (clazz != null)
            Name.setText(details.Name);
        Name.setFont(RFonts.RegMinecraftFont.deriveFont(20));
        if (clazz != null)
            Desc.setText(details.Description);

        if (icon) {
            Lay.putConstraint(SpringLayout.WEST, Icon, 5, SpringLayout.WEST, this);
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Icon, 0, SpringLayout.VERTICAL_CENTER, this);

            Lay.putConstraint(SpringLayout.NORTH, Name, 5, SpringLayout.NORTH, this);
            Lay.putConstraint(SpringLayout.WEST, Name, 5, SpringLayout.EAST, Icon);

            Lay.putConstraint(SpringLayout.NORTH, Desc, 5, SpringLayout.SOUTH, Name);
            Lay.putConstraint(SpringLayout.WEST, Desc, 5, SpringLayout.EAST, Icon);

            add(Icon);
            add(Name);
            add(Desc);

        } else {

            Lay.putConstraint(SpringLayout.NORTH, Name, 5, SpringLayout.NORTH, this);
            Lay.putConstraint(SpringLayout.WEST, Name, 5, SpringLayout.WEST, this);

            Lay.putConstraint(SpringLayout.NORTH, Desc, 5, SpringLayout.SOUTH, Name);
            Lay.putConstraint(SpringLayout.WEST, Desc, 5, SpringLayout.WEST, this);

            add(Name);
            add(Desc);

        }

        addMouseListener(this);
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void unselect() {
        this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white, 1, 16));
        this.selected = false;
    }

    public Class<? extends ElementSource<?>> getElement() {
        return clasz;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (!CanBeSelected)
            return;
        if (func != null)
            func.run();
        selected = true;
        this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), outlineColour, 3, 16));
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (!selected) {
            this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), outlineColour, 1, 16));
        } else
            this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), outlineColour, 4, 16));
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if (!selected) {
            unselect();
        } else {
            this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), outlineColour, 3, 16));
        }
    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

    }
}
