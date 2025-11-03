package fn10.bedrockr.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;
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

        @Nullable
        Class<? extends FieldFilter> Filter();

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
     * This annotation is used to mark a field for automatic builder window creations. Don't use if not doing automatic creation.
     */
    public @interface UneditableByCreation {
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CantEditAfter {
    }

     @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface VeryImportant {
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SpecialField {
    }

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface StringDropdownField {

        String[] value(); 
    }

}
