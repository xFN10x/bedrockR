package fn10.bedrockr.ui.components;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.ScaledImageIcon;
import fn10.bedrockr.addons.element.ValidatableValue;
import fn10.bedrockr.addons.element.elementFiles.RecipeFile;
import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.addons.mcjson.behav.Recipe;
import fn10.bedrockr.addons.mcjson.behav.Recipe.Item;
import fn10.bedrockr.addons.resource.WorkspaceResources;
import fn10.bedrockr.ui.RItemSelector;
import fn10.bedrockr.ui.laf.BedrockrDark;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.ui.util.WrongItemValueTypeException;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.exception.IncorrectWorkspaceException;
import org.apache.commons.lang3.ArrayUtils;
import org.intellij.lang.annotations.MagicConstant;

import javax.naming.NameNotFoundException;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import static fn10.bedrockr.utils.RFileOperations.gson;

public class RItemValue extends JPanel implements ValidatableValue {

//    public enum Type {
//        CraftingTable,
//        Single,
//        ListOfItems,
//        SingleBlock,
//        ListOfBlocks,
//    }
    public final static int
            TYPE_CRAFTING_TABLE = 0b1000,
            TYPE_SINGLE = 0b0000,
            TYPE_SINGLE_BLOCK = 0b0001,
            TYPE_LIST_BLOCK = 0b0011,
            TYPE_LIST = 0b0010;
    
    private final static int
            BIT_BLOCK = 0b0001,
            BIT_LIST = 0b0010;
    
    @MagicConstant(intValues = {
            TYPE_CRAFTING_TABLE,
    TYPE_SINGLE,
    TYPE_SINGLE_BLOCK,
    TYPE_LIST_BLOCK,
    TYPE_LIST
    })
    private final int type;

    protected boolean typeBlock() {
        return typeBit(BIT_BLOCK);
    }
    protected boolean typeList() {
        return typeBit(BIT_LIST);
    }
    protected boolean typeBit(int bit) {
        return (type & bit) == bit;
    }
    
    @SuppressWarnings("FieldCanBeLocal")
    public class ListElement extends JPanel {
        private final JButton RemoveButton = new JButton("-");
        private final RItemValue ItemVal;
        private final SpringLayout lay = new SpringLayout();

        private final static Dimension size = new Dimension(115, 75);

        public ListElement(JComponent parent, String workspace) {

            this.ItemVal = new RItemValue(workspace, type, true);

            setMinimumSize(size);
            setPreferredSize(size);
            setMaximumSize(size);

            setLayout(lay);
            setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.GRAY));

            RemoveButton.addActionListener(_ -> {
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

        public ListElement setItem(ItemInfo info) {
            try {
                ItemVal.setButtonToItem(0, info);
            } catch (WrongItemValueTypeException e) {
                RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
            return this;
        }

        public ArrayList<Item> getItems() {
            try {
                return ItemVal.getItems();
            } catch (WrongItemValueTypeException e) {
                RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                return null;
            }
        }
    }

    public static class ShapedOutput {
        /**
         * key is the pattern letter
         * <p>
         * value is the item id
         */
        public Map<String, String> key = new HashMap<>();
        public String[] pattern;

        public ShapedOutput() {
        }

        public ShapedOutput(RecipeFile from) {
            if (from == null || from.ShapedKey == null || from.ShapedPattern == null || from.ShapedKey.isEmpty()
                    || from.ShapedPattern.length == 0) {
                return;
            }
            this.key = from.ShapedKey;
            this.pattern = from.ShapedPattern;
        }
    }

    private static final Dimension SIZE = new Dimension(200, 200);
    private static final Dimension SIZE_SINGLE = new Dimension(69, 69);
    private static final ScaledImageIcon bg = ImageUtilities.toScaled(RFileOperations.readAllOfResource("/ui/CraftingGrid.png"));

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
            ImageUtilities.toScaled(RFileOperations.readAllOfResource("/addons/workspace/New.png")));

    public final Vector<JButton> buttons = new Vector<>(9);

    public static ItemInfo copied = null;
    private final boolean needsItems;
    private final String name;

    public void addListElements(String workspace, ItemInfo... item) throws WrongItemValueTypeException {
        assertList();
        ListInnerScroll.removeAll();
        for (ItemInfo ite : item) {
            ListInnerScroll.add(new ListElement(ListInnerScroll, workspace).setItem(ite));
        }
        ListInnerScroll.revalidate();
        ListInnerScroll.repaint();
    }

    private void assertList() throws WrongItemValueTypeException {
        if (!typeList()) {
            throw new WrongItemValueTypeException("Can't set list elements from this type!", BIT_LIST,
                    type);
        }
    }

    public ArrayList<ListElement> getListElements() throws WrongItemValueTypeException {
        assertList();
        ArrayList<ListElement> building = new ArrayList<>();
        for (Component comp : ListInnerScroll.getComponents()) {
            if (comp instanceof ListElement) {
                building.add((ListElement) comp);
            }
        }
        return building;
    }
    
    public Item getItem() throws WrongItemValueTypeException {
        ArrayList<Item> items = getItems();
        if (items.isEmpty()) return null;
        else 
            return items.getFirst();
    }
    
    public ArrayList<Item> getItems() throws WrongItemValueTypeException {
        ArrayList<Item> building = new ArrayList<>();
        if (typeList()) {
            for (ListElement ele : getListElements()) {
                if (!ele.getItems().isEmpty()) {
                    building.addAll(ele.getItems());
                }
            }
        } else {
            for (Component comp : ButtonGrid.getComponents()) {
                if (comp instanceof JButton) {
                    ItemInfo info = gson.fromJson(comp.getName(), ItemInfo.class);
                    if (info == null)
                        continue;
                    building.add(info.toRecipeItem());
                }
            }
        }
        return building;
    }

    public void empty() throws WrongItemValueTypeException {
        assertNotList();

        if (!typeList()) {
            try {
                setButtonToItem(0, Recipe.NULL_RETURN_ITEM);
            } catch (WrongItemValueTypeException e) {
                RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
            }
        } else if (type == TYPE_CRAFTING_TABLE) {
            for (int i = 0; i < 9; i++) {
                try {
                    setButtonToItem(i, Recipe.NULL_RETURN_ITEM);
                } catch (WrongItemValueTypeException e) {
                    RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
                }
            }
        }
    }

    private void assertNotList() throws WrongItemValueTypeException {
        if (typeList()) {
            throw new WrongItemValueTypeException("Can't set list elements from this type!", 0,
                    type);
        }
    }

    public void setItem(ItemInfo info) {
        try {
            setButtonToItem(0, info);
        } catch (WrongItemValueTypeException e) {
            RFileOperations.LOG.log(java.util.logging.Level.SEVERE, "Exception thrown", e);
        }
    }

    public void setButtonToItem(JButton button, ItemInfo item) throws WrongItemValueTypeException {
        if (item == null)
            return;
        assertNotList();

        if (item.equals(Recipe.NULL_RETURN_ITEM)) {
            button.setIcon(null);
            button.setText("");
            button.setToolTipText("");
            button.setName("");
            return;
        }
        if (item.Texture != null) {
            button.setFont(button.getFont().deriveFont(16f));
            button.setIcon(ImageUtilities.toScaled(ArrayUtils.toPrimitive(item.Texture),48,48));
            button.setText("");
        } else {
            button.setFont(button.getFont().deriveFont(8f));
            button.setText(item.Name);
            button.setIcon(null);
        }
        button.setToolTipText(item.Name + " (" + item.Id + ")");
        button.setName(gson.toJson(item));
    }

    public void setButtonToItem(int buttonIndex, ItemInfo item) throws WrongItemValueTypeException {
        setButtonToItem(buttons.get(buttonIndex), item);
    }

    public void setShapedRecipe(Window parent, ShapedOutput value, String workspace)
            throws WrongItemValueTypeException {
        assertCraftingTable();

        if (value.pattern != null)
            for (int i = 0; i < value.pattern.length; i++) { // go for each vertical row
                // 'i' is the index in the array
                String row = value.pattern[i];
                for (int j = 0; j < row.length(); j++) { // go for the 3 buttons to set the string
                    String itemString = String.valueOf(row.charAt(j));
                    if (itemString.isBlank()) {
                        continue;
                    }
                    ItemInfo item;
                    try {
                        item = ItemInfo.getItemById(
                                value.key.get(itemString), workspace, ImageUtilities.ImgHandler);
                    } catch (IncorrectWorkspaceException | NameNotFoundException | IOException |
                             WorkspaceResources.WorkspaceUnsupportedException e) {
                        ErrorShower.exception(parent, e);
                        continue;
                    }
                    setButtonToItem((i * 3) + j, item);
                }
            }
    }

    private void assertCraftingTable() throws WrongItemValueTypeException {
        if (type != TYPE_CRAFTING_TABLE)
            throw new WrongItemValueTypeException("Can't set shaped recipe from this item value!", TYPE_CRAFTING_TABLE,
                    type);
    }

    public ShapedOutput getShapedRecipe() throws WrongItemValueTypeException {
        assertCraftingTable();
        
        ShapedOutput output = new ShapedOutput();
        String[] patternKeys = new String[]{
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

        //key is prefix:id, value is the letter
        HashMap<String, String> patternKey = new HashMap<>();

        List<String> patternRows = new ArrayList<>();

        for (int i = 0; i < 3; i++) { // go for each vertical row
            StringBuilder string = new StringBuilder();
            for (int j = 0; j < 3; j++) { // go for the 3 buttons to make the string
                JButton button = buttons.get((i * 3) + j); // times the vertical row by 3 to get the buttons (e.g., row
                // 0, button 0 is button 1, row 2 gbutton 0 is 7.)
                if (button.getName().isBlank()) {
                    string.append(' ');
                    continue;
                }
                ItemInfo info = gson.fromJson(button.getName(), ItemInfo.class);
                if (!patternKey.containsKey(info.Prefix + ":" + info.Id)) { // if this item doesn't already has a pattern
                    // key
                    patternKey.put(info.Prefix + ":" + info.Id, patternKeys[currentKey]);
                    currentKey++;
                }
                // get the pattern key
                string.append(patternKey.get(info.Prefix + ":" + info.Id));
            }
            String str = string.toString().stripTrailing();
            if (!str.isBlank() || i == 1) {
                patternRows.add(str);
            }
        }

//        if (patternRows.size() >= 3) { // if there are 3 rows
//            if (patternRows.get(0).isBlank() || patternRows.get(2).isBlank()) {
//                // if one has nothing, get rid of it
//                patternRows.remove(1);
//            }
//                // if row 1 and 3 have something in it, don't get rid of middle
//        } else if (patternRows.size() == 2) { // if the last, or the first is gone, then get rid of all of the blank
//                                              // ones (idk which one is the last index 1; 0, or 1)
//            ArrayList<String> building = new ArrayList<>();
//            for (String string : patternRows) {
//                if (!string.isBlank())
//                    building.add(string);
//            }
//            patternRows = building;
//        }

        for (Entry<String, String> set : patternKey.entrySet()) {
            output.key.put(set.getValue(), set.getKey());
        }
        output.pattern = patternRows.toArray(new String[]{});
        return output;
    }

    public RItemValue(String WorkspaceName) {
        this(WorkspaceName, TYPE_CRAFTING_TABLE);
    }

    public RItemValue(String WorkspaceName, int type) {
        this(WorkspaceName, type, false);
    }

    public RItemValue(String WorkspaceName, int type, boolean needsToHaveItems) {
        this("Unnamed Item Selector", WorkspaceName, type, needsToHaveItems);
    }

    public RItemValue(String name, String WorkspaceName, int type, boolean needsToHaveItems) {
        super();
        this.name = name;
        this.type = type;
        this.needsItems = needsToHaveItems;
        setLayout(Layout);
        if (typeList()) {
            setMinimumSize(new Dimension(200, 300));
            setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.GRAY));
            ListScroll.setBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Color.GRAY.darker()));
            ListInnerScroll.setLayout(ListInnerLayout);

            ListAddButton.addActionListener(_ -> {
                ListInnerScroll.add(new ListElement(ListInnerScroll, WorkspaceName));
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
        } else {
            if (type != TYPE_CRAFTING_TABLE) {
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

            for (int i = 0; i < ((type != TYPE_CRAFTING_TABLE) ? 1 : 9); i++) {

                JButton building = new JButton("");
                building.setBorderPainted(false);
                building.setName("");
                building.setBackground(new Color(28, 56, 17));
                buttons.add(i, building);
                if (type != TYPE_CRAFTING_TABLE) {
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
                paste.addActionListener(_ -> {
                    if (copied != null) {
                        try {
                            setButtonToItem(building, copied);
                        } catch (WrongItemValueTypeException e1) {
                            RFileOperations.LOG.log(Level.SEVERE, "Exception thrown", e1);
                        }
                    }
                });
                copy.addActionListener(_ -> {
                    copied = gson.fromJson(building.getName(), ItemInfo.class);
                    paste.setEnabled(copied != null);
                });

                JMenuItem remove = buttonPopup.add("Remove");
                remove.addActionListener(_ -> {
                    building.setIcon(null);
                    building.setText("");
                    building.setToolTipText("");
                    building.setName("");
                    copy.setEnabled(!building.getName().isBlank());
                    paste.setEnabled(copied != null);
                });

                building.setComponentPopupMenu(buttonPopup);
                building.setMargin(new Insets(1, 1, 1, 1));
                building.addActionListener(_ -> {
                    try {
                        ItemInfo itemInfo;
                            itemInfo = RItemSelector.openSelector(null, WorkspaceName, typeBlock() ? RItemSelector.FILTER_BLOCKS : RItemSelector.FILTER_ITEMS);
                        if (itemInfo != null) {
                            setButtonToItem(building, itemInfo);
                        }
                    } catch (InterruptedException | WrongItemValueTypeException e) {
                        RFileOperations.LOG.log(Level.SEVERE, "Exception thrown", e);
                    }
                });
                ButtonGrid.add(building);
            }
            ButtonGrid.setBackground(BedrockrDark.BEDROCKR_GREEN);

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
        }
    }

    @Override
    public boolean valid(boolean strict) {
        if (!typeList()) {
            if (needsItems && strict) {
                try {
                    if (getListElements().isEmpty())
                        return false;
                } catch (WrongItemValueTypeException e) {
                    RFileOperations.LOG.log(Level.SEVERE, "Exception thrown", e);
                }
            }
            try {
                for (ListElement ele : getListElements()) {
                    if (!ele.ItemVal.valid(strict))
                        return false;
                }
            } catch (WrongItemValueTypeException e) {
                RFileOperations.LOG.log(Level.SEVERE, "Exception thrown", e);
            }
        } else {
            if (needsItems && strict) {
                try {
                    return !getItems().isEmpty();
                } catch (WrongItemValueTypeException e) {

                    RFileOperations.LOG.log(Level.SEVERE, "Exception thrown", e);
                }
            } else {
                return true;
            }
        }
        return true;
    }


    @Override
    public String getProblemMessage() {
        return "This grid needs to have items.";
    }

    @Override
    public String getValueName() {
        return name;
    }
}
