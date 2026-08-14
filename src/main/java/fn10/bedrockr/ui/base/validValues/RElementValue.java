package fn10.bedrockr.ui.base.validValues;

import fn10.bedrockr.addons.element.ValidatableValue;
import fn10.bedrockr.addons.element.elementSources.SourceBiomeElement;
import fn10.bedrockr.addons.element.interfaces.SourcelessElementFile;
import fn10.bedrockr.addons.element.supporting.block.BlockTexture;
import fn10.bedrockr.addons.resource.Resource;
import fn10.bedrockr.addons.resource.ResourcePointer;
import fn10.bedrockr.ui.components.RHelpButton;
import fn10.bedrockr.ui.components.elementValues.*;
import fn10.bedrockr.utils.RAnnotation;
import fn10.bedrockr.utils.RAnnotation.CantEditAfter;
import fn10.bedrockr.utils.RAnnotation.FieldDetails;
import fn10.bedrockr.utils.RAnnotation.HelpMessage;
import fn10.bedrockr.utils.RLogUtils;
import fn10.bedrockr.utils.Theme;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A component used to easily edit fields
 *
 * @param <T> The type this element value is based on.
 * @param <I> The type of the input component
 */
public abstract class RElementValue<T, I extends JComponent> extends JPanel implements ValidatableValue {

    public interface ChangedStatusListener {
        void changed(boolean to);
    }

    //private final static String No_Path_Chosen_Text = "(Click to set path.)";
    public final SpringLayout Lay = new SpringLayout();
    protected final JLabel Name = new JLabel();
    public RHelpButton Help = new RHelpButton();
    public I Input;
    protected final JCheckBox EnableCheckbox = new JCheckBox();

    protected @Nullable Field Target;
    protected @Nullable String WorkspaceName;
    protected @Nullable SourcelessElementFile TargetFile;

    protected final Class<T> type;

    private T initValue;

    public boolean Required;
    public String Problem = "Not checked...";
    public boolean Changed = false;
    private ChangedStatusListener changedListener = _ -> {
    };

    public void setChangedStatusChangedListener(ChangedStatusListener lis) {
        this.changedListener = lis;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final String editedPrefix = "(*) ";
        if (initValue == null) return;
        Object val = getValue(false);
        if (!Objects.equals(val, initValue) && !Changed) {
            if (!Name.getText().startsWith(editedPrefix)) {
                Name.setText(editedPrefix + Name.getText());
            }
            Name.setForeground(Color.red);
            Changed = true;
            changedListener.changed(true);
        } else if (Changed && Objects.equals(val, initValue)) {
            if (Name.getText().startsWith(editedPrefix)) {
                Name.setText(Name.getText().replace(editedPrefix, ""));
            }
            Name.setForeground(getForeground());
            Changed = false;
            changedListener.changed(false);
        }
    }

    @Nonnull
    public abstract I createInput();

    protected boolean problem(boolean check, String fals, String tru) {
        Problem = check ? tru : fals;
        return check;
    }

    protected boolean problem(boolean check, String fals) {
        return problem(check, fals, "Passed!");
    }

    protected static boolean are(Field field, Class<?> cls) {
        return are(field.getType(), cls);
    }

    protected static boolean are(@Nonnull Class<?> cls0, @Nonnull Class<?> cls1) {
        return cls1.isAssignableFrom(cls0);
    }

    public static <Y> RElementValue<Y, ?> ofClass(
            @Nonnull Class<Y> type) {
        return ofField(null, type, null, null);
    }

    public static RElementValue<?, ?> ofField(@Nonnull Field field,
                                              @Nullable SourcelessElementFile TargetFile,
                                              @Nullable String WorkspaceName) {
        return ofField(field, field.getType(), TargetFile, WorkspaceName);
    }

    @SuppressWarnings("unchecked")
    public static <T, L, V, R extends Resource> RElementValue<T, ?> ofField(@Nullable Field field,
                                                                            @Nonnull Class<T> type,
                                                                            @Nullable SourcelessElementFile TargetFile,
                                                                            @Nullable String WorkspaceName) {
        FieldDetails anno = null;
        Type[] typeArgs = null;
        if (field != null) {
            anno = field.getAnnotation(FieldDetails.class);
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType ptype)
                typeArgs = ptype.getActualTypeArguments();
        }
        RElementValue<T, ?> returning = null;

        if (are(type, String.class)) {
            if (field != null && field.getAnnotation(RAnnotation.StringDropdownField.class) != null) {
                returning = (RElementValue<T, ?>) new REDropdownStringValue(field, ((Class<String>) type), TargetFile, WorkspaceName, anno);
            } else
                returning = (RElementValue<T, ?>) new REStringValue(field, ((Class<String>) type), TargetFile, WorkspaceName, anno);
        } else if (are(type, Path.class)) {
            returning = (RElementValue<T, ?>) new REPathValue(field, ((Class<Path>) type), TargetFile, WorkspaceName, anno);
        } else if (are(type, List.class)) {
            if (field != null) {
                returning = (RElementValue<T, ?>) new REListValue<>(field, (Class<List<L>>) type, ((Class<L>) typeArgs[0]), TargetFile, WorkspaceName, anno);
            }
        } else if (are(type, Integer.class) || are(type, Float.class)) {
            RAnnotation.NumberRange range = new RAnnotation.NumberRange() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return RAnnotation.NumberRange.class;
                }

                @Override
                public float max() {
                    return Float.MAX_VALUE;
                }

                @Override
                public float min() {
                    return Float.MIN_VALUE;
                }

                @Override
                public float step() {
                    return 0.1f;
                }
            };
            if (field != null) {
                RAnnotation.NumberRange prop = field.getAnnotation(RAnnotation.NumberRange.class);
                if (prop != null)
                    range = prop;
            }
            returning = (RElementValue<T, ?>) new RENumberScrollValue(field, ((Class<Float>) type), TargetFile, WorkspaceName, anno, range.min(), range.max(), range.step(), are(type, Integer.class));
        } else if (are(type, BlockTexture.class)) {
            returning = (RElementValue<T, ?>) new REBlockTexturesValue(field, ((Class<BlockTexture>) type), TargetFile, WorkspaceName, anno);
        } else if (are(type, Boolean.class) || are(type, boolean.class)) {
            returning = (RElementValue<T, ?>) new REBooleanValue(field, ((Class<Boolean>) type), TargetFile, WorkspaceName, anno);
        } else if (are(type, Map.class)) {
            if (typeArgs != null)
                returning = (RElementValue<T, ?>) new REMapValue<>(((Class<V>) typeArgs[1]), field, (Class<Map<String, V>>) type, TargetFile, WorkspaceName, anno);
        } else if (are(type, ResourcePointer.class)) {
            if (typeArgs != null)
                returning = (RElementValue<T, ?>) new REResourceValue<>((Class<R>) typeArgs[0], field, ((Class<ResourcePointer<R>>) type), TargetFile, WorkspaceName, anno);
        }
        if (returning == null)
            returning = empty(field == null ? "" : field.getName(), type);

        if (field != null)
            try {
                Object v = field.get(TargetFile);
                if (v != null && returning.getType().isAssignableFrom(v.getClass())) {
                    returning.setValue(returning.getType().cast(v));
                }
            } catch (IllegalAccessException e) {
                RLogUtils.exception("This field doesn't belong to the target file.", e);
            }

        return returning;
    }

    private static <T> RElementValue<T, JLabel> empty(String name, Class<T> type) {
        return new RElementValue<>(null, type, name, false, null, null) {
            @Override
            public @NonNull JLabel createInput() {
                return new JLabel("Unsupported type: " + super.type.getName());
            }

            @Override
            public void setValueInternal(T value) {

            }

            @Override
            protected T getValueInternal(boolean shouldLog) {
                return null;
            }

            @Override
            public boolean valid(boolean strict, boolean log0) {
                return true;
            }
        };
    }

    public RElementValue(@Nullable Field TargetField,
                         @Nonnull Class<T> type,
                         @Nullable SourcelessElementFile TargetFile,
                         @Nullable String WorkspaceName,
                         @Nullable FieldDetails details) {
        String name;
        boolean op;
        if (details == null) {
            op = false;
            if (TargetField != null)
                name = TargetField.getName();
            else
                name = "";
        } else {
            op = details.Optional();
            name = details.displayName();
        }
        this(TargetField, type, name, op, TargetFile, WorkspaceName);
    }

    @SuppressWarnings({"unchecked", "null"})
    private RElementValue(@Nullable Field TargetField,
                          @Nonnull Class<T> type,
                          String DisplayName,
                          boolean Optional,
                          @Nullable SourcelessElementFile TargetFile,
                          @Nullable String WorkspaceName
    ) {
        this.TargetFile = TargetFile;
        this.Target = TargetField;
        this.Required = !Optional;
        //this.Filter = Filter;
        //this.InputType = InputType;
        this.WorkspaceName = WorkspaceName;
        boolean FromEmpty = TargetFile == null;
        this.type = type;
        final Dimension Size;
        Size = new Dimension(350, 40);

        setMaximumSize(Size);
        setPreferredSize(Size);
        setBorder(new LineBorder(getBackground()));
        setLayout(Lay);

        this.Input = createInput();

        if (Optional) // stop the enable check affecting non-optional things
            EnableCheckbox.addItemListener(new ItemListener() {
                {
                    Input.setEnabled(EnableCheckbox.isSelected());
                }

                @Override
                public void itemStateChanged(ItemEvent e) {
                    Input.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                }

            });
        Help.setTitle("Help for: " + DisplayName);
        HelpMessage helpMsg = getAnno(HelpMessage.class);

        Name.setText(DisplayName);

        try {
            if (!FromEmpty) {
                if (Target.get(TargetFile) != null) {
                    EnableCheckbox.setSelected(true);
                }
            }
        } catch (Exception e) {
            RLogUtils.exception("Exception thrown", e);
        }

        if (Optional)
            Lay.putConstraint(SpringLayout.EAST, Input, -3, SpringLayout.WEST, EnableCheckbox);
        else
            Lay.putConstraint(SpringLayout.EAST, Input, -3, SpringLayout.WEST, Help);

        Lay.putConstraint(SpringLayout.EAST, Help, 0, SpringLayout.EAST, this);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Help, 0, SpringLayout.VERTICAL_CENTER, this);
        Lay.putConstraint(SpringLayout.EAST, EnableCheckbox, -3, SpringLayout.WEST, Help);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, EnableCheckbox, 0, SpringLayout.VERTICAL_CENTER, this);

//        // don't do this if its set manually
//        if (Input == null)
//            // do corrisponding actions depending on the type
//            try {
//               
//                
//               
//                
//                
//                else if (Map.class.isAssignableFrom(InputType)) {
//                    Input = new JScrollPane(HashMapInnerPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//                    ((JScrollPane) Input).getVerticalScrollBar().setUnitIncrement(18);
//                    /*
//                     * HashMapInnerScroll is the pane that is inside input, IT IS A JPANEL, NOT A
//                     * JSCROLLPANE!!!!
//                     */
//
//                    // do things to the panels
//                    HashMapInnerPane.setLayout(new BoxLayout(HashMapInnerPane, BoxLayout.Y_AXIS));
//                    Input.setBorder(new LineBorder(Color.DARK_GRAY));
//                    Input.setBackground(getBackground().brighter());
//                    // get the RMapProvider
//
//                    // finally, get the annotation after getting the field
//                    final MapFieldSelectables anno;
//                    if (field != null) {
//                        anno = field.getAnnotation(MapFieldSelectables.class);
//                    } else {
//                        anno = null;
//                    }
//                    List<RMapElement> picked = new ArrayList<RMapElement>();
//                    if (!FromEmpty && field != null) {
//                        try {
//                            for (Map.Entry<String, Object> entry : ((Map<String, Object>) field.get(TargetFile))
//                                    .entrySet()) {
//                                RElementMapValue ToAdd = new RElementMapValue(parentFrame,
//                                        RMapElement.LookupMap.get(entry.getKey()));
//                                ToAdd.setVal(entry.getValue());
//
//                                picked.add(RMapElement.LookupMap.get(entry.getKey()));
//
//                                HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
//                                HashMapInnerPane.add(ToAdd);
//                            }
//                        } catch (Exception e) {
//                            RLogUtils.exception("Exception thrown",
//                                    e);
//                            ErrorShower.showError(parentFrame, e.getMessage(), WorkspaceName, e);
//                        }
//                    }
//
//                    // add the button
//                    HashMapAdd.addActionListener((e) -> {
//                        if (field != null && anno != null)
//                            try {
//                                RMapElement select = RMapValueAddingSelector.openSelector(parentFrame,
//                                        ((RMapElement[]) anno.value().getMethod("getPickable")
//                                                .invoke(anno.value().getConstructor().newInstance())),
//                                        picked);
//                                if (select == null)
//                                    return;
//                                var toAdd = new RElementMapValue(parentFrame, select);
//                                toAdd.setSize(HashMapInnerPane.getWidth() - 5,
//                                        Double.valueOf(toAdd.getSize().getHeight()).intValue());
//                                toAdd.setAlignmentX(0.5f);
//
//                                picked.add(select);
//
//                                HashMapInnerPane.add(Box.createRigidArea(new Dimension(100, 10)));
//                                HashMapInnerPane.add(toAdd);
//
//                                // finish, and why this wasnt working before
//                                HashMapInnerPane.revalidate();
//                                HashMapInnerPane.repaint();
//
//                            } catch (Exception e1) {
//                                RFileOperations.LOG.log(Level.SEVERE,
//                                        "Exception thrown", e1);
//                                ErrorShower.showError(parentFrame, "Failed to add a map element.", e1.getMessage(), e1);
//                            }
//                    });
//                    add(HashMapAdd);
//
//                    Lay.putConstraint(SpringLayout.EAST, HashMapAdd, -5, SpringLayout.WEST, Input);
//                    Lay.putConstraint(SpringLayout.NORTH, HashMapAdd, 5, SpringLayout.SOUTH, Name);
//                } 
//                
//            } catch (Exception e) {
//                RLogUtils.exception("Exception thrown", e);
//            }

//        if (Optional) // stop the enable check affecting non-optional things
//            EnableCheckbox.addItemListener(new ItemListener() {
//                {
//                    Input.setEnabled(EnableCheckbox.isSelected());
//                }
//
//                @Override
//                public void itemStateChanged(ItemEvent e) {
//                    Input.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
//                }
//
//            });

        //Name.setText(DisplayName);
        Lay.putConstraint(SpringLayout.VERTICAL_CENTER, Name, 0, SpringLayout.VERTICAL_CENTER, this);
        //}

        Lay.putConstraint(SpringLayout.WEST, Name, 0, SpringLayout.WEST, this);


        Lay.putConstraint(SpringLayout.WEST, Input, 3, SpringLayout.EAST, Name);
        Lay.putConstraint(SpringLayout.NORTH, Input, 3, SpringLayout.NORTH, this);
        Lay.putConstraint(SpringLayout.SOUTH, Input, -3, SpringLayout.SOUTH, this);

        if (!Optional)
            EnableCheckbox.setEnabled(false);

        add(Name);
        add(Input);

        if (!Optional)
            EnableCheckbox.setEnabled(false);
        try {
            // if you cant edit a value after its created,
            if (getAnno(CantEditAfter.class) != null) {
                //and the input isn't null,
                if (TargetFile != null && Target.get(TargetFile) != null) {
                    //you cant edit it.
                    Input.setEnabled(false);
                }
            }
        } catch (Exception e1) {
            RLogUtils.exception("Exception thrown", e1);
        }
        if (Optional)
            add(EnableCheckbox);
        if (Target != null) {
            if (helpMsg != null) {
                Help.setMessage(helpMsg.value());
                add(Help);
            } else {
                Help.setMessage("No help provided.");
            }
            try {
                setValue((T) Target.get(TargetFile));
            } catch (Exception e) {
                RLogUtils.exception("Exception", e);
            }
        }
    }

    /**
     * @return A bool, indicating if this field should be read.
     */
    public Boolean getOptionallyEnabled() {
        if (Required)
            return true;
        return EnableCheckbox.isSelected();
    }

    public void setValue(T value) {
        if (value == null) return;
        initValue = value;
        setValueInternal(value);
    }

    public abstract void setValueInternal(T value);

    public T getValue() {
        return getValue(true);
    }

    public T getValue(boolean shouldLog) {
        if (valid(true, shouldLog)) {
            return getValueInternal(shouldLog);
        } else {

            return null;
        }
    }

    protected abstract T getValueInternal(boolean shouldLog);

    public boolean valid(boolean strict) {
        return valid(strict, true);
    }

    public abstract boolean valid(boolean strict, boolean log0);


    @Override
    public String getProblemMessage() {
        return Problem;
    }

    @Override
    public String getValueName() {
        return getDisplayName();
    }

    public String getDisplayName() {
        return Name.getText();
    }

    public Class<T> getType() {
        return type;
    }

    @Nullable
    public <A extends Annotation> A getAnno(Class<A> annotation) {
        return getAnno(annotation, null);
    }

    public <A extends Annotation> A getAnno(Class<A> annotation, A defaul) {
        if (getTarget() == null || Target == null) return defaul;
        A anno = Target.getAnnotation(annotation);
        if (anno == null) return defaul;
        return anno;
    }

    @Nullable
    public Field getTarget() {
        return Target;
    }

    /**
     * Passes a string through to substitute it if it can be.
     *
     * @param input The array of strings, if the first element matches a subsitution, well the whole array is overwritten.
     * @return The input if nothing matched.
     */
    public String[] substituteArray(String[] input) {
        return switch (input[0]) {
            case "_VANILLABIOMES" -> SourceBiomeElement.getVanillaBiomeNames();
            case "_PREFIXEDVANILLABIOMES" -> SourceBiomeElement.getPrefixedVanillaBiomeNames();
            case "_THEMENAMES" -> Theme.getNames();
            default -> input;
        };
    }
}
