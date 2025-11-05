package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fn10.bedrockr.utils.typeAdapters.ImageIconSerilizer;
import fn10.bedrockr.windows.RItemSelector;
import fn10.bedrockr.windows.RItemSelector.ReturnItemInfo;

public class RCraftingGridValue extends JPanel {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ImageIcon.class, new ImageIconSerilizer()).create();
    private static final Dimension SIZE = new Dimension(200, 200);
    private static final ImageIcon bg = new ImageIcon(RCraftingGridValue.class.getResource("/ui/CraftingGrid.png"));

    public final JLabel Background = new JLabel(bg);

    public final GridLayout ButtonLayout = new GridLayout(3, 3, 6, 6);
    public final SpringLayout Layout = new SpringLayout();

    public final JPanel ButtonGrid = new JPanel(ButtonLayout);

    public static ReturnItemInfo copied = null;

    public RCraftingGridValue(String WorkspaceName) {
        super();

        setLayout(Layout);
        setBackground(Color.DARK_GRAY);

        setMinimumSize(SIZE);
        setMaximumSize(SIZE);

        for (int i = 0; i < 9; i++) {

            JButton building = new JButton("");
            building.setName("");

            JPopupMenu buttonPopup = new JPopupMenu();
            JMenuItem copy = buttonPopup.add("Copy");
            copy.setEnabled(!building.getName().isBlank());
            copy.addActionListener(ac -> {
                copied = gson.fromJson(building.getName(), ReturnItemInfo.class);
            });
            JMenuItem paste = buttonPopup.add("Paste");
            paste.setEnabled(copied != null);
            paste.addActionListener(ac -> {
                if (copied != null) {
                    if (copied.Texture != null) {
                        building.setFont(building.getFont().deriveFont(16f));
                        building.setIcon(copied.Texture);
                        building.setText("");
                    } else {
                        building.setFont(building.getFont().deriveFont(8f));
                        building.setText(copied.Name);
                        building.setIcon(null);
                    }
                    building.setToolTipText(copied.Name + " (" + copied.Id + ")");
                    building.setName(gson.toJson(copied));
                    copy.setEnabled(!building.getName().isBlank());
                    paste.setEnabled(copied != null);
                }
            });
            JMenuItem remove = buttonPopup.add("Remove");
            remove.addActionListener(ac -> {
                building.setIcon(null);
                building.setText("");
                building.setToolTipText("");
                building.setName("");
                copy.setEnabled(!building.getName().isBlank());
                paste.setEnabled(copied != null);
            });

            
            building.setComponentPopupMenu(buttonPopup);
            building.setBorderPainted(false);
            building.setMargin(new Insets(1, 1, 1, 1));
            building.addActionListener(ac -> {
                try {
                    ReturnItemInfo itemInfo = RItemSelector.openSelector(null, WorkspaceName);
                    if (itemInfo != null) {
                        if (itemInfo.Texture != null) {
                            building.setFont(building.getFont().deriveFont(16f));
                            building.setIcon(itemInfo.Texture);
                            building.setText("");
                        } else {
                            building.setFont(building.getFont().deriveFont(8f));
                            building.setText(itemInfo.Name);
                            building.setIcon(null);
                        }
                        building.setToolTipText(itemInfo.Name + " (" + itemInfo.Id + ")");
                        building.setName(gson.toJson(copied));
                        copy.setEnabled(!building.getName().isBlank());
                        paste.setEnabled(copied != null);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            ButtonGrid.add(building);
        }

        Layout.putConstraint(SpringLayout.EAST, Background, 0, SpringLayout.EAST, this);
        Layout.putConstraint(SpringLayout.WEST, Background, 0, SpringLayout.WEST, this);
        Layout.putConstraint(SpringLayout.NORTH, Background, 0, SpringLayout.NORTH, this);
        Layout.putConstraint(SpringLayout.SOUTH, Background, 0, SpringLayout.SOUTH, this);

        Layout.putConstraint(SpringLayout.EAST, ButtonGrid, -6, SpringLayout.EAST, this);
        Layout.putConstraint(SpringLayout.WEST, ButtonGrid, 6, SpringLayout.WEST, this);
        Layout.putConstraint(SpringLayout.NORTH, ButtonGrid, 6, SpringLayout.NORTH, this);
        Layout.putConstraint(SpringLayout.SOUTH, ButtonGrid, -6, SpringLayout.SOUTH, this);

        add(Background);
        add(ButtonGrid);
    }

}
