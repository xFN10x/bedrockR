package fn10.bedrockr.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
import fn10.bedrockr.addons.source.FieldFilters.RegularStringFilter;
import fn10.bedrockr.addons.source.interfaces.RMapElementProvider;
import jakarta.annotation.Nullable;

public class RAnnotation {

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface HelpMessage {
        String value();
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldDetails {
        boolean Optional();

        Class<? extends FieldFilter> Filter() default RegularStringFilter.class;

        @Nullable
        String displayName();
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ResourcePackResourceType {
        int value();
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MapFieldSelectables {
        Class<? extends RMapElementProvider> value();
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    /**
     * This annotation is used to mark a field for automatic builder window
     * creations. Don't use if not doing automatic creation.
     */
    public @interface UneditableByCreation {
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    /**
     * Used to specify if this field can be edited after the Element has been
     * created initially.
     */
    public @interface CantEditAfter {
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    /**
     * Specifies if this field needs to have a value even if drafting.
     */
    public @interface VeryImportant {
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    /**
     * This annotation is used to mark a field for automatic builder window
     * creations. Don't use if not doing automatic creation.
     */
    public @interface SpecialField {
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    /**
     * Used to specify that a String field should be a dropdown.
     * 
     * @param value  - an array of strings that are selectable
     * 
     * @param strict - Specifies if the user can write whatever into the combobox or
     *               not. Default: {@code false}
     */
    public @interface StringDropdownField {

        String[] value();

        boolean strict() default false;
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    /**
     * Used to specify that a Number; like an Integer/int, or Float/float; has
     * bounds.
     * 
     * @param min - a float being the minimum number. Casted to int if the field is
     *            one
     * 
     * @param max - a float being the maximum number. Casted to int if the field is
     *            one
     */
    public @interface NumberRange {

        float max() default 1;

        float min() default 0;
    }
}
