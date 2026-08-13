package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.ui.util.ErrorShower;
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
import java.util.List;

public class REListValue<L> extends RElementValue<List<L>, JScrollPane> {
    protected JPanel InnerPane = new JPanel();
    protected JButton AddButton = new JButton(new ImageIcon(RFileOperations.readAllOfResource("/addons/workspace/New.png")));
    private final Class<L> listType;

    public REListValue(@Nullable Field TargetField, Class<List<L>> type, @Nonnull Class<L> listType, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.FieldDetails details) {
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
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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

                addInnerValue(RElementValue.ofClass(listType));

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

    private void addInnerValue(RElementValue<?, ?> toAdd) {
        JButton removeButton = new JButton("-");

        toAdd.Lay.putConstraint(SpringLayout.VERTICAL_CENTER, removeButton, 0,
                SpringLayout.VERTICAL_CENTER, toAdd);
        toAdd.Lay.putConstraint(SpringLayout.WEST, toAdd.Input, 3, SpringLayout.EAST, removeButton);

        toAdd.add(removeButton);
        removeButton.addActionListener(_ -> {
            InnerPane.remove(toAdd);
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
            addInnerValue(RElementValue.ofClass(l.getClass()));
        }
    }

    @Override
    protected List<L> getValueInternal(boolean shouldLog) {
        return List.of();
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return false;
    }
}
