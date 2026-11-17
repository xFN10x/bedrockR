package fn10.bedrockr.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.formdev.flatlaf.util.SystemFileChooser;
import fn10.bedrockr.addons.element.FieldFilters.FieldFilter;
import fn10.bedrockr.addons.element.FieldFilters.RegularStringFilter;
import fn10.bedrockr.addons.element.interfaces.RMapElementProvider;
import jakarta.annotation.Nullable;

public class RAnnotation {

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface HelpMessage {
        String value() default "No help provided.";
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SettingsCategory {
        enum SettingsCategorys {
            Misc("Misc."),
            File("Files"),
            Appearance("Appearance"),
            Network("Network");
            public final String Name;
            SettingsCategorys(String name) {
                this.Name = name;
            }
        }
        SettingsCategorys value() default SettingsCategorys.Misc;
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CreationMenuTab {
        String value() default "";
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldDetails {
        boolean Optional() default true;

        Class<? extends FieldFilter> Filter() default RegularStringFilter.class;

        @Nullable
        String displayName();
    }
    /**
     * Used to define the order in which fields are automatically added to a creation screen.
     *
     * @since a2.0
     */
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Order {
        int value() default 0;
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MapFieldSelectables {
        Class<? extends RMapElementProvider> value();
    }
    /**
     * This annotation is used to mark a field for automatic builder window
     * creations. Don't use if not doing automatic creation.
     */
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UneditableByCreation {
    }

    /**
     * Used to specify if this field can be edited after the Element has been
     * created initially.
     */
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CantEditAfter {
    }
    
    /**
     * Specifies if this field needs to have a value even if drafting.
     */
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface VeryImportant {
    }


    /**
     * Used to specify that a String field should be a dropdown.
     *
     */
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface StringDropdownField {

        String[] value();

        boolean strict() default false;
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PathType {

        int value() default SystemFileChooser.FILES_ONLY;
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequiresRestart {
    }

    /**
     * Used to specify that a Number; like an Integer/int, or Float/float; has
     * bounds.
     *<p>
     *<b>min</b> - a float being the minimum number. Casted to int if the field is
     *            one
     *<p>
     *<b>max</b> - a float being the maximum number. Casted to int if the field is
     *            one
     */
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NumberRange {

        float max() default 1;

        float min() default 0;
        
        float step() default 0.5f;
    }
}
