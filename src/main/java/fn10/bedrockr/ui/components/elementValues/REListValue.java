package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.validValues.RElementValue;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RLogUtils;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class REListValue<L> extends RElementValue<List<L>, JScrollPane> {
    protected JPanel InnerPane;
    protected JButton AddButton;
    private final Class<L> listType;
    private final ArrayList<RElementValue<L, ?>> innerList = new ArrayList<>();

    public REListValue(@Nullable Field TargetField, Class<List<L>> type, @Nonnull Class<L> listType, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.FieldDetails details) {
        InnerPane = new JPanel();
        AddButton = new JButton(ImageUtilities.toScaled(RFileOperations.readAllOfResource("/addons/workspace/New.png")));
        super(TargetField, type, TargetFile, WorkspaceName, details);
        this.listType = listType;
    }

    @Override
    public @NonNull JScrollPane createInput() {
        /*
         * I'm just stealing most of the hash map stuff, since it is basicly already a
         * list view.
         */
        JScrollPane input = new JScrollPane(InnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        input.getVerticalScrollBar().setUnitIncrement(18);

        /*
         * HashMapInnerScroll is the pane that is inside input, IT IS A JPANEL, NOT A
         * JSCROLLPANE!!!!
         */

        // do things to the panels
        InnerPane.setLayout(new BoxLayout(InnerPane, BoxLayout.Y_AXIS));
        input.setBorder(new LineBorder(Color.DARK_GRAY));
        input.setBackground(getBackground().brighter());

        //final RAnnotation.StringDropdownField anno = type.getAnnotation(RAnnotation.StringDropdownField.class);
        // add the button
        AddButton.addActionListener((_) -> {
            try {
//                if (anno != null) {
//                    toAdd.remove(toAdd.Input);
//                    JComboBox<String> newInput = new JComboBox<>(substituteArray(anno.value()));
//
//                    toAdd.Lay.putConstraint(SpringLayout.WEST, newInput, 3, SpringLayout.EAST, toAdd.Name);
//                    toAdd.Lay.putConstraint(SpringLayout.NORTH, newInput, 3, SpringLayout.NORTH, toAdd);
//                    toAdd.Lay.putConstraint(SpringLayout.SOUTH, newInput, -3, SpringLayout.SOUTH, toAdd);
//                    toAdd.Lay.putConstraint(SpringLayout.EAST, newInput, -3, SpringLayout.WEST, toAdd.Help);
//                    toAdd.add(newInput);
//                    toAdd.Input = newInput;
//
//                    if (anno.strict()) {
//                        newInput.setEditable(false);
//                        newInput.setSelectedIndex(0);
//                    }
//                }
                RElementValue<L, ?> toAad = RElementValue.ofField(Target, listType, TargetFile, WorkspaceName, null);
                addInnerValue(toAad);

            } catch (Exception e1) {
                RLogUtils.exception("Exception thrown",
                        e1);
                ErrorShower.showError(this, "Failed to add a map element.", e1.getMessage(), e1);
            }
        });
        add(AddButton);

        Lay.putConstraint(SpringLayout.EAST, AddButton, -5, SpringLayout.WEST, input);
        Lay.putConstraint(SpringLayout.NORTH, AddButton, 5, SpringLayout.SOUTH, Name);
        return input;
    }

    private void addInnerValue(RElementValue<L, ?> toAdd) {
        JButton removeButton = new JButton("-");
        innerList.add(toAdd);
        toAdd.Name.setText("");
        //toAdd.setMaximumSize(new Dimension(getSize().width - 20,toAdd.getMaximumSize().height));

        toAdd.Lay.putConstraint(SpringLayout.VERTICAL_CENTER, removeButton, 0,
                SpringLayout.VERTICAL_CENTER, toAdd);
        toAdd.Lay.putConstraint(SpringLayout.WEST, toAdd.Input, 3, SpringLayout.EAST, removeButton);

        toAdd.add(removeButton);
        removeButton.addActionListener(_ -> {
            InnerPane.remove(toAdd);
            innerList.remove(toAdd);
            InnerPane.repaint();
            InnerPane.revalidate();
        });

        toAdd.setAlignmentX(0.5f);

        InnerPane.add(Box.createVerticalStrut(10));
        InnerPane.add(toAdd);

        InnerPane.revalidate();
        InnerPane.repaint();
    }

    @Override
    public void setValueInternal(List<L> value) {
        for (L l : value) {
            RElementValue<L, ?> toAad = RElementValue.ofField(Target, listType, TargetFile, WorkspaceName, null);
            toAad.setValue(l);
            addInnerValue(toAad);
        }
        InnerPane.revalidate();
        InnerPane.repaint();
    }

    @Override
    protected List<L> getValueInternal(boolean shouldLog) {
        return innerList.stream().map(RElementValue::getValue).toList();
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        for (RElementValue<L, ?> lrElementValue : innerList) {
            if (!lrElementValue.valid(strict)) return false; 
        }
        return true;
    }
}
