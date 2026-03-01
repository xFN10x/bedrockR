package fn10.bedrockr.addons.source;

import fn10.bedrockr.utils.RFileOperations;

public class FieldFilters {

    public interface FieldFilter {

        Boolean getValid(String subject);

        static boolean isEmptyString(String subject) {
            return subject.isBlank();
        }

        static boolean containsAnOccurance(String Target, String... chars) {
            for (String string : chars) {
                if (Target.contains(string))
                    return true;
            }
            return false;
        }

    }

    public static class RegularStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            return !FieldFilter.isEmptyString(subject);
        }

    }

    public static class IDStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            // check if there are any spaces, or uppercases
            if (subject.chars().anyMatch(Character::isUpperCase))
                return false;
            // check if the id is valid
            else return RFileOperations.validFolderName(subject) && !FieldFilter.isEmptyString(subject);
        }

    }

    public static class FileNameLikeStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            // check if there are any spaces, or uppercases
            return RFileOperations.validFolderName(subject) && !FieldFilter.isEmptyString(subject);
        }

    }

    /**
     * This filter is a FileNameLikeStringFilter, but it allowed {@code (none)}
     */
    public static class CommonFilter1 implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            // check if there are any spaces, or uppercases
            if (subject.equals("(none)")) // none is allowed
                return true;

            return RFileOperations.validFolderName(subject) && !FieldFilter.isEmptyString(subject);
        }
    }

}
