package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fn10.bedrockr.utils.typeAdapters.ImageIconSerilizer;
import fn10.bedrockr.windows.RItemSelector;
import fn10.bedrockr.windows.RItemSelector.ReturnItemInfo;

public class RCraftingGridValue extends JPanel {

    public static class ShapedOutput {
        /**
         * key is the pattern letter
         * 
         * value is the item id
         */
        public Map<String, String> key = new HashMap<String, String>();
        public String[] pattern;
    }

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ImageIcon.class, new ImageIconSerilizer())
            .create();
    private static final Dimension SIZE = new Dimension(200, 200);
    private static final ImageIcon bg = new ImageIcon(RCraftingGridValue.class.getResource("/ui/CraftingGrid.png"));

    public final JLabel Background = new JLabel(bg);

    public final GridLayout ButtonLayout = new GridLayout(3, 3, 6, 6);
    public final SpringLayout Layout = new SpringLayout();

    public final JPanel ButtonGrid = new JPanel(ButtonLayout);

    public final Vector<JButton> buttons = new Vector<JButton>(9);

    public static ReturnItemInfo copied = null;

    public ShapedOutput getShapedRecipe() {
        ShapedOutput output = new ShapedOutput();
        String[] patternKeys = new String[] {
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "I"
        };
        int currentKey = 0;
        /**
         * key is prefix:id
         * 
         * value is the letter
         */
        HashMap<String, String> patternKey = new HashMap<String, String>();

        List<String> patternRows = new ArrayList<String>();

        for (int i = 0; i < 3; i++) { // go for each vertical row
            StringBuilder string = new StringBuilder();
            for (int j = 0; j < 3; j++) { // go for the 3 buttons to make the string
                JButton button = buttons.get((i * 3) + j); // times the vertical row by 3 to get the buttons (e.g., row
                                                           // 0, button 0 is button 1, row 2 gbutton 0 is 7.)
                if (button.getName().isBlank()) {
                    string.append(' ');
                    continue;
                }
                ReturnItemInfo info = gson.fromJson(button.getName(), ReturnItemInfo.class);
                if (!patternKey.containsKey(info.Prefix + ":" + info.Id)) { // if this item doesnt already has a pattern key
                    patternKey.put(info.Prefix + ":" + info.Id, patternKeys[currentKey]);
                    currentKey++;
                }
                // get the pattern key
                string.append(patternKey.get(info.Prefix + ":" + info.Id));
            }
            patternRows.add(string.toString());
        }
        System.out.println(patternRows.toArray());

        for (Entry<String,String> set : patternKey.entrySet()) {
            output.key.put(set.getValue(), set.getKey());
        }
        output.pattern = patternRows.toArray(new String[] {});
        return output;
    }

    public RCraftingGridValue(String WorkspaceName) {
        super();

        setLayout(Layout);
        setBackground(Color.DARK_GRAY);

        setMinimumSize(SIZE);
        setMaximumSize(SIZE);

        for (int i = 0; i < 9; i++) {

            JButton building = new JButton("");
            building.setName("");
            buttons.add(i, building);

            JPopupMenu buttonPopup = new JPopupMenu();

            JMenuItem copy = buttonPopup.add("Copy");
            copy.setEnabled(!building.getName().isBlank());

            JMenuItem paste = buttonPopup.add("Paste");

            buttonPopup.addSeparator();
            buttonPopup.addPopupMenuListener(new PopupMenuListener() {

                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    copy.setEnabled(!building.getName().isBlank());
                    paste.setEnabled(copied != null);
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {
                }

            });

            paste.setEnabled(copied != null);
            paste.addActionListener(ac -> {
                if (copied != null) {
                    if (copied.Texture != null) {
                        building.setFont(building.getFont().deriveFont(16f));
                        building.setIcon(new ImageIcon(
                                copied.Texture.getImage().getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH)));
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
            copy.addActionListener(ac -> {
                copied = gson.fromJson(building.getName(), ReturnItemInfo.class);
                paste.setEnabled(copied != null);
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
                            building.setIcon(new ImageIcon(itemInfo.Texture.getImage().getScaledInstance(48, 48,
                                    java.awt.Image.SCALE_SMOOTH)));
                            building.setText("");
                        } else {
                            building.setFont(building.getFont().deriveFont(8f));
                            building.setText(itemInfo.Name);
                            building.setIcon(null);
                        }
                        building.setToolTipText(itemInfo.Name + " (" + itemInfo.Id + ")");
                        building.setName(gson.toJson(itemInfo));
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
