package fn10.bedrockr.ui.components.elementValues;

import fn10.bedrockr.addons.element.RMapElement;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.ui.RMapValueAddingSelector;
import fn10.bedrockr.ui.base.RElementValue;
import fn10.bedrockr.ui.components.RElementMapValue;
import fn10.bedrockr.ui.util.ErrorShower;
import fn10.bedrockr.ui.util.ImageUtilities;
import fn10.bedrockr.utils.RAnnotation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class REMapValue<K, V> extends RElementValue<Map<K, V>, JScrollPane> {
    protected final JPanel HashMapInnerPane;
    protected final JButton HashMapAdd;

    public REMapValue(Class<K> keyType, Class<V> valueType, @Nullable Field TargetField, @NonNull Class<Map<K, V>> type, @Nullable SourcelessElementFile TargetFile, @Nullable String WorkspaceName, RAnnotation.@Nullable FieldDetails details) {
        HashMapInnerPane = new JPanel();
        HashMapAdd = new JButton(ImageUtilities.getIcon("/addons/workspace/New.png"));
        super(TargetField, type, TargetFile, WorkspaceName, details);
    }

    @Override
    public @NonNull JScrollPane createInput() {
        var input = new JScrollPane(HashMapInnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        input.getVerticalScrollBar().setUnitIncrement(18);

        // do things to the panels
        HashMapInnerPane.setLayout(new BoxLayout(HashMapInnerPane, BoxLayout.Y_AXIS));
        input.setBorder(new LineBorder(Color.DARK_GRAY));
        input.setBackground(getBackground().brighter());
        // get the RMapProvider

        // finally, get the annotation after getting the field
        final RAnnotation.MapFieldSelectables anno = getAnno(RAnnotation.MapFieldSelectables.class);

        List<RMapElement> picked = new ArrayList<>();

        // add the button
        HashMapAdd.addActionListener(_ -> {
            if (Target != null && anno != null)
                try {
                    RMapElement select = RMapValueAddingSelector.openSelector(null,
                            ((RMapElement[]) anno.value().getMethod("getPickable")
                                    .invoke(anno.value().getConstructor().newInstance())),
                            picked);
                    if (select == null)
                        return;
                    var toAdd = new RElementMapValue(null, select);
                    toAdd.setSize(HashMapInnerPane.getWidth() - 5,
                            Double.valueOf(toAdd.getSize().getHeight()).intValue());
                    toAdd.setAlignmentX(0.5f);

                    picked.add(select);

                    addREMV(toAdd);

                    // finish, and why this wasnt working before
                    HashMapInnerPane.revalidate();
                    HashMapInnerPane.repaint();

                } catch (Exception e1) {
                    ErrorShower.exception(this, "Failed to add a map element.", e1);
                }
        });
        add(HashMapAdd);

        Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, input);
        Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
        
        revalidate();
        repaint();
        return input;
    }

    @Override
    public void setValueInternal(Map<K, V> value) {
        try {
                for (Map.Entry<K, V> entry : value.entrySet()) {
                    RElementMapValue ToAdd = new RElementMapValue(null,
                            RMapElement.LookupMap.get(entry.getKey().toString()));
                    ToAdd.setVal(entry.getValue());

                    addREMV(ToAdd);
                }
            } catch (Exception e) {
                ErrorShower.exception(null, e.getMessage(), e);
            }
    }

    private void addREMV(RElementMapValue ToAdd) {
        HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
        HashMapInnerPane.add(ToAdd);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map<K, V> getValueInternal(boolean shouldLog) {
        HashMap<K, V> building = new HashMap<>();
        for (Component component : HashMapInnerPane.getComponents()) {
            if (component instanceof RElementMapValue remv) {
                Map.Entry<K, V> entry = (Map.Entry<K, V>) remv.getKeyAndVal();
                building.put(entry.getKey(), entry.getValue());
            }
        }
        return building;
    }

    @Override
    public boolean valid(boolean strict, boolean log0) {
        Problem = "This is valid";
        return true;
    }
}
