package fn10.bedrockr.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nullable;

import fn10.bedrockr.addons.source.FieldFilters.FieldFilter;

public class RAnnotation {

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface HelpMessage {
        String message();
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
    public @interface UneditableByCreation {

    }

}
