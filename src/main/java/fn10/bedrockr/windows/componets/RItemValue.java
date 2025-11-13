package fn10.bedrockr.windows.componets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.naming.NameNotFoundException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe;
import fn10.bedrockr.addons.addon.jsonClasses.BP.Recipe.Item;
import fn10.bedrockr.addons.source.elementFiles.RecipeFile;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import fn10.bedrockr.utils.exception.WrongItemValueTypeException;
import fn10.bedrockr.utils.typeAdapters.ImageIconSerilizer;
import fn10.bedrockr.windows.RBlockSelector;
import fn10.bedrockr.windows.RItemSelector;
import fn10.bedrockr.windows.RItemSelector.ReturnItemInfo;
import fn10.bedrockr.windows.interfaces.ValidatableValue;

public class RItemValue extends JPanel implements ValidatableValue {

    public static enum Type {
        CraftingTable,
        Single,
        ListOfItems,
        SingleBlock,
        ListOfBlocks,
    }

    protected static class ListElement extends JPanel {
        private final JButton RemoveButton = new JButton("-");
        private final RItemValue ItemVal;
        private final SpringLayout lay = new SpringLayout();

        private final static Dimension size = new Dimension(115, 75);

        public ListElement(JComponent parent, String workspace, boolean blocks) {

            this.ItemVal = new RItemValue(workspace, blocks ? Type.SingleBlock : Type.Single, true);

            setMinimumSize(size);
            setPreferredSize(size);
            setMaximumSize(size);

            setLayout(lay);
            setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.GRAY));

            RemoveButton.addActionListener(act -> {
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            });

            lay.putConstraint(SpringLayout.EAST, ItemVal, -5, SpringLayout.EAST, this);

            lay.putConstraint(SpringLayout.WEST, RemoveButton, 5, SpringLayout.WEST, this);
            lay.putConstraint(SpringLayout.SOUTH, RemoveButton, -5, SpringLayout.SOUTH, this);

            add(ItemVal);
            add(RemoveButton);
        }

        public ListElement setItem(ReturnItemInfo info) {
            try {
                ItemVal.setButtonToItem(0, info);
            } catch (WrongItemValueTypeException e) {
                e.printStackTrace();
            }
            return this;
        }

        public ArrayList<Item> getItems() {
            try {
                return ItemVal.getItems();
            } catch (WrongItemValueTypeException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class ShapedOutput {
        /**
         * key is the pattern letter
         * 
         * value is the item id
         */
        public Map<String, String> key = new HashMap<String, String>();
        public String[] pattern;

        public ShapedOutput() {
        }

        public ShapedOutput(RecipeFile from) {
            if (from == null || from.ShapedKey == null || from.ShapedPattern == null || from.ShapedKey.isEmpty()
                    || from.ShapedPattern.length <= 0) {
                return;
            }
            this.key = from.ShapedKey;
            this.pattern = from.ShapedPattern;
        }
    }

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ImageIcon.class, new ImageIconSerilizer())
            .create();
    private static final Dimension SIZE = new Dimension(200, 200);
    private static final Dimension SIZE_SINGLE = new Dimension(69, 69);
    private static final ImageIcon bg = new ImageIcon(RItemValue.class.getResource("/ui/CraftingGrid.png"));

    public final JLabel Background = new JLabel(bg);

    public final SpringLayout ButtonGridSingleLayout = new SpringLayout();
    public final GridLayout ButtonLayout = new GridLayout(3, 3, 6, 6);
    public final SpringLayout Layout = new SpringLayout();

    public final JPanel ButtonGrid = new JPanel();

    public final JPanel ListInnerScroll = new JPanel();
    public final BoxLayout ListInnerLayout = new BoxLayout(ListInnerScroll, BoxLayout.Y_AXIS);
    public final JScrollPane ListScroll = new JScrollPane(ListInnerScroll, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    public final JButton ListAddButton = new JButton(
            new ImageIcon(getClass().getResource("/addons/workspace/New.png")));

    public final Vector<JButton> buttons = new Vector<JButton>(9);

    public static ReturnItemInfo copied = null;
    private final boolean needsItems;
    private final Type currentType;

    public void addListElements(String workspace, ReturnItemInfo... item) throws WrongItemValueTypeException {
        if (currentType != Type.ListOfItems) {
            throw new WrongItemValueTypeException("Can't set list elements from this type!", Type.ListOfItems,
                    currentType);
        }
        ListInnerScroll.removeAll();
        for (ReturnItemInfo ite : item) {
            ListInnerScroll.add(new ListElement(ListInnerScroll, workspace,
                    (currentType == Type.ListOfBlocks || currentType == Type.SingleBlock)).setItem(ite));
        }
        ListInnerScroll.revalidate();
        ListInnerScroll.repaint();
    }

    public ArrayList<ListElement> getListElements() throws WrongItemValueTypeException {
        if (currentType != Type.ListOfItems) {
            throw new WrongItemValueTypeException("Can't get list elements from this type!", Type.ListOfItems,
                    currentType);
        }
        ArrayList<ListElement> building = new ArrayList<ListElement>();
        for (Component comp : ListInnerScroll.getComponents()) {
            if (comp instanceof ListElement) {
                building.add((ListElement) comp);
            }
        }
        return building;
    }

    public ArrayList<Item> getItems() throws WrongItemValueTypeException {
        if (currentType == Type.ListOfItems || currentType == Type.ListOfBlocks) {
            ArrayList<Item> building = new ArrayList<Item>();
            for (ListElement ele : getListElements()) {
                if (!ele.getItems().isEmpty()) {
                    building.addAll(ele.getItems());
                }
            }
            return building;
        } else {
            ArrayList<Item> building = new ArrayList<Item>();
            for (Component comp : ButtonGrid.getComponents()) {
                if (comp instanceof JButton) {
                    ReturnItemInfo info = gson.fromJson(comp.getName(), ReturnItemInfo.class);
                    if (info == null)
                        continue;
                    building.add(info.toRecipeItem());
                }
            }
            return building;
        }
    }

    public void empty() throws WrongItemValueTypeException {
        if (currentType == Type.ListOfItems || currentType == Type.ListOfBlocks)
            throw new WrongItemValueTypeException("Can't set a single button from this item value!", Type.CraftingTable,
                    currentType);

        if (currentType == Type.Single || currentType == Type.SingleBlock) {
            try {
                setButtonToItem(0, Recipe.NULL_RETURN_ITEM);
            } catch (WrongItemValueTypeException e) {
                e.printStackTrace();
            }
        } else if (currentType == Type.CraftingTable) {
            for (int i = 0; i < 9; i++) {
                try {
                    setButtonToItem(i, Recipe.NULL_RETURN_ITEM);
                } catch (WrongItemValueTypeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setButtonToItem(JButton button, ReturnItemInfo item) throws WrongItemValueTypeException {
        if (item == null)
            return;
        if (currentType == Type.ListOfItems || currentType == Type.ListOfBlocks)
            throw new WrongItemValueTypeException("Can't set a single button from this item value!", Type.CraftingTable,
                    currentType);

        if (item.equals(Recipe.NULL_RETURN_ITEM)) {
            button.setIcon(null);
            button.setText("");
            button.setToolTipText("");
            button.setName("");
            return;
        }
        if (item.Texture != null) {
            button.setFont(button.getFont().deriveFont(16f));
            button.setIcon(new ImageIcon(
                    item.Texture.getImage().getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH)));
            button.setText("");
        } else {
            button.setFont(button.getFont().deriveFont(8f));
            button.setText(item.Name);
            button.setIcon(null);
        }
        button.setToolTipText(item.Name + " (" + item.Id + ")");
        button.setName(gson.toJson(item));
    }

    public void setButtonToItem(int buttonIndex, ReturnItemInfo item) throws WrongItemValueTypeException {
        setButtonToItem(buttons.get(buttonIndex), item);
    }

    public void setShapedRecipe(Window parent, ShapedOutput value, String workspace)
            throws WrongItemValueTypeException {
        if (currentType != Type.CraftingTable)
            throw new WrongItemValueTypeException("Can't set shaped recipe from this item value!", Type.CraftingTable,
                    currentType);
        if (value.pattern != null)
            for (int i = 0; i < value.pattern.length; i++) { // go for each vertical row
                // 'i' is the index in the array
                String row = value.pattern[i];
                for (int j = 0; j < row.length(); j++) { // go for the 3 buttons to set the string
                    String itemString = String.valueOf(row.charAt(j));
                    if (itemString.isBlank() || itemString.isEmpty()) {
                        continue;
                    }
                    ReturnItemInfo item;
                    try {
                        if (currentType == Type.SingleBlock || currentType == Type.ListOfBlocks) {
                            item = RBlockSelector.getBlockById(parent,
                                    value.key.get(itemString), workspace);
                        } else {
                            item = RItemSelector.getItemById(parent,
                                    value.key.get(itemString), workspace);
                        }
                    } catch (IncorrectWorkspaceException | NameNotFoundException e) {
                        e.printStackTrace();
                        continue;
                    }
                    setButtonToItem((i * 3) + j, item);
                }
            }
    }

    public ShapedOutput getShapedRecipe() throws WrongItemValueTypeException {
        return getShapedRecipe(true);
    }

    public ShapedOutput getShapedRecipe(boolean trim) throws WrongItemValueTypeException {
        if (currentType != Type.CraftingTable)
            throw new WrongItemValueTypeException("Can't get shaped recipe from this item value!", Type.CraftingTable,
                    currentType);
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
                if (!patternKey.containsKey(info.Prefix + ":" + info.Id)) { // if this item doesnt already has a pattern
                                                                            // key
                    patternKey.put(info.Prefix + ":" + info.Id, patternKeys[currentKey]);
                    currentKey++;
                }
                // get the pattern key
                string.append(patternKey.get(info.Prefix + ":" + info.Id));
            }
            String str = string.toString().stripTrailing();
            if ((!str.isBlank() && !str.isEmpty()) || i == 1) {
                patternRows.add(str.toString());
            }
        }
        if (patternRows.size() >= 3) { // if there are 3 rows
            if (!patternRows.get(2).isBlank() && !patternRows.get(2).isBlank()) {
                // if row 1 and 3 have something in it, dont get rid of middle
            } else {
                // if one has nothing, get rid of it
                patternRows.remove(1);
            }
        } else if (patternRows.size() == 2) { // if the last, or the first is gone, then get rid of all of the blank
                                              // ones (idk which one is the last index 1; 0, or 1)
            ArrayList<String> building = new ArrayList<String>();
            for (String string : patternRows) {
                if (!string.isBlank())
                    building.add(string);
            }
            patternRows = building;
        }

        for (Entry<String, String> set : patternKey.entrySet()) {
            output.key.put(set.getValue(), set.getKey());
        }
        output.pattern = patternRows.toArray(new String[] {});
        return output;
    }

    public RItemValue(String WorkspaceName) {
        this(WorkspaceName, Type.CraftingTable);
    }

    public RItemValue(String WorkspaceName, Type type) {
        this(WorkspaceName, type, false);
    }

    public RItemValue(String WorkspaceName, Type type, boolean needsToHaveItems) {
        super();

        this.currentType = type;
        this.needsItems = needsToHaveItems;
        setLayout(Layout);
        switch (type) {
            case Type.ListOfBlocks:
            case Type.ListOfItems:
                setMinimumSize(new Dimension(200, 300));
                setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.GRAY));
                ListScroll.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.GRAY.darker()));
                ListInnerScroll.setLayout(ListInnerLayout);

                ListAddButton.addActionListener(ac -> {
                    ListInnerScroll.add(new ListElement(ListInnerScroll, WorkspaceName,
                            (currentType == Type.ListOfBlocks || currentType == Type.SingleBlock)));
                    ListInnerScroll.revalidate();
                    ListInnerScroll.repaint();
                });

                Layout.putConstraint(SpringLayout.EAST, ListScroll, -2, SpringLayout.EAST, this);
                Layout.putConstraint(SpringLayout.WEST, ListScroll, 5, SpringLayout.EAST, ListAddButton);
                Layout.putConstraint(SpringLayout.NORTH, ListScroll, 2, SpringLayout.NORTH, this);
                Layout.putConstraint(SpringLayout.SOUTH, ListScroll, -2, SpringLayout.SOUTH, this);

                Layout.putConstraint(SpringLayout.NORTH, ListAddButton, 5, SpringLayout.NORTH, this);
                Layout.putConstraint(SpringLayout.WEST, ListAddButton, 5, SpringLayout.WEST, this);

                add(ListScroll);
                add(ListAddButton);
                break;
            default:
            case Type.CraftingTable:
            case Type.SingleBlock:
            case Type.Single:

                if (type == Type.Single || type == Type.SingleBlock) {
                    setMinimumSize(SIZE_SINGLE);
                    setPreferredSize(SIZE_SINGLE);
                    setMaximumSize(SIZE_SINGLE);
                    ButtonGrid.setLayout(ButtonGridSingleLayout);
                } else {
                    setMinimumSize(SIZE);
                    setPreferredSize(SIZE);
                    setMaximumSize(SIZE);
                    ButtonGrid.setLayout(ButtonLayout);
                }

                for (int i = 0; i < ((type == Type.Single || type == Type.SingleBlock) ? 1 : 9); i++) {

                    JButton building = new JButton("");
                    building.setBorderPainted(false);
                    building.setName("");
                    building.setBackground(new Color(28, 56, 17));
                    buttons.add(i, building);
                    if (type == Type.Single || type == Type.SingleBlock) {
                        ButtonGridSingleLayout.putConstraint(SpringLayout.WEST, building, 1,
                                SpringLayout.WEST, ButtonGrid);
                        ButtonGridSingleLayout.putConstraint(SpringLayout.NORTH, building, 1,
                                SpringLayout.NORTH, ButtonGrid);
                        ButtonGridSingleLayout.putConstraint(SpringLayout.SOUTH, building, -1,
                                SpringLayout.SOUTH, ButtonGrid);
                        ButtonGridSingleLayout.putConstraint(SpringLayout.EAST, building, -1,
                                SpringLayout.EAST, ButtonGrid);
                    }

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
                            try {
                                setButtonToItem(building, copied);
                            } catch (WrongItemValueTypeException e1) {
                                e1.printStackTrace();
                            }
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
                    building.setMargin(new Insets(1, 1, 1, 1));
                    building.addActionListener(ac -> {
                        try {
                            ReturnItemInfo itemInfo;
                            if (currentType == Type.SingleBlock || currentType == Type.ListOfBlocks)
                                itemInfo = RBlockSelector.openSelector(null, WorkspaceName);
                            else
                                itemInfo = RItemSelector.openSelector(null, WorkspaceName);
                            if (itemInfo != null) {
                                setButtonToItem(building, itemInfo);
                            }
                        } catch (InterruptedException | WrongItemValueTypeException e) {
                            e.printStackTrace();
                        }
                    });
                    ButtonGrid.add(building);
                }
                ButtonGrid.setBackground(new Color(76, 255, 0));

                Layout.putConstraint(SpringLayout.EAST, Background, 0, SpringLayout.EAST, this);
                Layout.putConstraint(SpringLayout.WEST, Background, 0, SpringLayout.WEST, this);
                Layout.putConstraint(SpringLayout.NORTH, Background, 0, SpringLayout.NORTH, this);
                Layout.putConstraint(SpringLayout.SOUTH, Background, 0, SpringLayout.SOUTH, this);

                Layout.putConstraint(SpringLayout.EAST, ButtonGrid, -6, SpringLayout.EAST, this);
                Layout.putConstraint(SpringLayout.WEST, ButtonGrid, 6, SpringLayout.WEST, this);
                Layout.putConstraint(SpringLayout.NORTH, ButtonGrid, 6, SpringLayout.NORTH, this);
                Layout.putConstraint(SpringLayout.SOUTH, ButtonGrid, -6, SpringLayout.SOUTH, this);

                add(ButtonGrid);
                add(Background);

                break;

        }
    }

    @Override
    public boolean valid(boolean strict) {
        switch (currentType) {
            case Type.ListOfBlocks:
            case Type.ListOfItems:
                if (needsItems) {
                    try {
                        if (getListElements().isEmpty())
                            return false;
                    } catch (WrongItemValueTypeException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    for (ListElement ele : getListElements()) {
                        if (ele.ItemVal.valid(strict))
                            continue;
                        else
                            return false;
                    }
                } catch (WrongItemValueTypeException e) {
                    e.printStackTrace();
                }
                break;
            default:
            case Type.Single:
            case Type.SingleBlock:
            case Type.CraftingTable:
                if (strict) {
                    if (needsItems) {
                        try {
                            if (getItems().size() >= 1) {
                                return true;
                            } else {
                                return false;
                            }
                        } catch (WrongItemValueTypeException e) {

                            e.printStackTrace();
                        }
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
        }
        return true;
    }

    @Override
    public boolean valid() {
        return valid(true);
    }

    @Override
    public String getProblemMessage() {
        return "This grid needs to have items.";
    }

    @Override
    public String getName() {
        return "Item Selector";
    }
}
