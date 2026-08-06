package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.RLogUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class REListValue extends RElementValue<List<?>, JScrollPane> {
    protected JPanel HashMapInnerPane = new JPanel();
    protected JButton HashMapAdd = new JButton(new ImageIcon(RFileOperations.readAllOfResource("/addons/workspace/New.png")));

    public REListValue(@Nullable Field TargetField, @NonNull Class<List<?>> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public JScrollPane createInput() {
        /*
         * I'm just stealing most of the hash map stuff, since it is basicly already a
         * list view.
         */
        JScrollPane input = new JScrollPane(HashMapInnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        input.getVerticalScrollBar().setUnitIncrement(18);

        /*
         * HashMapInnerScroll is the pane that is inside input, IT IS A JPANEL, NOT A
         * JSCROLLPANE!!!!
         */

        // do things to the panels
        HashMapInnerPane.setLayout(new BoxLayout(HashMapInnerPane, BoxLayout.Y_AXIS));
        input.setBorder(new LineBorder(Color.DARK_GRAY));
        input.setBackground(getBackground().brighter());
        // get the RMapProvider
        final Class<?> genericType;
        if (Target == null) return null;
        Class<?> type = Target.getType();
        if (!type.isArray()) {
            if (Target.getGenericType() instanceof ParameterizedType pt) {
                genericType = (Class<?>) pt.getActualTypeArguments()[0];
            } else
                genericType = null;
            if (genericType == null) {
                throw new NullPointerException("This list/array doesn't have a type.");
            }
        } else {
            genericType = null;
        }


        //final RAnnotation.StringDropdownField anno = type.getAnnotation(RAnnotation.StringDropdownField.class);
        // add the button
        HashMapAdd.addActionListener((_) -> {
            try {
                RElementValue toAdd;
                if (type.isArray()) {
                    // fn10.bedrockr.Launcher.LOG.info("make an array value element with
                    // class: "
                    // + InputType.getComponentType().getCanonicalName());
                    toAdd = RElementValue.ofClass(type.getComponentType());
                } else {

                    // fn10.bedrockr.Launcher.LOG.info("make a list value element with
                    // class: "
                    // + genericType.getCanonicalName());
                    toAdd = RElementValue.ofClass(genericType);
                }
// have no idea why this is here
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
//
//                JButton removeButton = new JButton("-");
//
//                toAdd.Lay.putConstraint(SpringLayout.VERTICAL_CENTER, removeButton, 0,
//                        SpringLayout.VERTICAL_CENTER, toAdd);
//                toAdd.Lay.putConstraint(SpringLayout.WEST, toAdd.Input, 3, SpringLayout.EAST, removeButton);
//
//                toAdd.add(removeButton);
//                removeButton.addActionListener(ac -> {
//                    HashMapInnerPane.remove(toAdd);
//                    HashMapInnerPane.repaint();
//                    HashMapInnerPane.revalidate();
//                });
//
//                toAdd.setAlignmentX(0.5f);
//
//                HashMapInnerPane.add(Box.createVerticalStrut(10));
//                HashMapInnerPane.add(toAdd);
//
//                HashMapInnerPane.revalidate();
//                HashMapInnerPane.repaint();

            } catch (Exception e1) {
                RLogUtils.exception("Exception thrown",
                        e1);
                ErrorShower.showError(this, "Failed to add a map element.", e1.getMessage(), e1);
            }
        });
        add(HashMapAdd);

        Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, input);
        Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
        return input;
    }

    @Override
    public void setValueInternal(List<?> value) {

    }

    @Override
    protected List<?> getValueInternal(boolean shouldLog) {
        return List.of();
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        return false;
    }
}
