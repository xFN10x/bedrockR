package fn10.bedrockr.ui.base;

import com.formdev.flatlaf.ui.FlatLineBorder;
import fn10.bedrockr.ui.laf.BedrockrDark;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.ui.util.RFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RDetailedButton extends JPanel implements MouseListener {
    protected final Runnable func;
    public JLabel Icon = new JLabel();
    public JLabel Name = new JLabel();
    public JLabel Desc = new JLabel();
    public boolean CanBeSelected = true;
    protected SpringLayout Lay = new SpringLayout();
    protected boolean selected;
    protected Color outlineColour;

    public RDetailedButton(Color borderColour) {
        this(borderColour, true);
    }

    public RDetailedButton(Runnable selectedFunction, boolean hasIcon) {
        this(selectedFunction, BedrockrDark.BEDROCKR_GREEN, hasIcon);
    }
    public RDetailedButton(Color borderColour, boolean hasIcon) {
        this(null, borderColour, hasIcon);
    }

    public RDetailedButton(Runnable selectedFunction, Color borderColour, boolean hasIcon) {
        super();
        this.func = selectedFunction;
        this.outlineColour = borderColour;

        Icon.setAlignmentX(CENTER_ALIGNMENT);
        Icon.setAlignmentY(CENTER_ALIGNMENT);
        Name.setFont(RFonts.RegMinecraftFont.deriveFont(20f));
        setLayout(Lay);

        setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white, 1, 16));
        setPreferredSize(new Dimension(350, 80));
        setMaximumSize(new Dimension(350, 80));

        Icon.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.gray, 1, 16));
        Icon.setPreferredSize(new Dimension(70, 70));
        Icon.setSize(new Dimension(70, 70));

        if (hasIcon) {
            Lay.putConstraint(SpringLayout.WEST, Icon, 5, SpringLayout.WEST, this);
            Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Icon, 0, SpringLayout.VERTICAL_CENTER, this);

            Lay.putConstraint(SpringLayout.NORTH, Name, 5, SpringLayout.NORTH, this);
            Lay.putConstraint(SpringLayout.WEST, Name, 5, SpringLayout.EAST, Icon);

            Lay.putConstraint(SpringLayout.NORTH, Desc, 5, SpringLayout.SOUTH, Name);
            Lay.putConstraint(SpringLayout.WEST, Desc, 5, SpringLayout.EAST, Icon);

            add(Icon);

        } else {

            Lay.putConstraint(SpringLayout.NORTH, Name, 5, SpringLayout.NORTH, this);
            Lay.putConstraint(SpringLayout.WEST, Name, 5, SpringLayout.WEST, this);

            Lay.putConstraint(SpringLayout.NORTH, Desc, 5, SpringLayout.SOUTH, Name);
            Lay.putConstraint(SpringLayout.WEST, Desc, 5, SpringLayout.WEST, this);

        }
        add(Name);
        add(Desc);

        addMouseListener(this);
    }

    public void setIcon(ImageIcon ico) {
        Icon.setIcon(ImageUtilities.ResizeIcon(ico, 64, 64));
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void unselect() {
        this.setBorder(new FlatLineBorder(new Insets(3, 3, 3, 3), Color.white, 1, 16));
        this.selected = false;
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
