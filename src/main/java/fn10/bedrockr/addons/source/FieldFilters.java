package fn10.bedrockr.addons.source;

import fn10.bedrockr.utils.RFileOperations;

public class FieldFilters {

    public interface FieldFilter {

        Boolean getValid(String subject);

        public static boolean isEmptyString(String subject) {
            return subject.isBlank();
        }

        public static boolean containsAnOccurance(String Target, String... chars) {
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
            if (FieldFilter.isEmptyString(subject)) {
                return false;
            } else
                return true;
        }

    }

    public static class IDStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            // check if there are any spaces, or uppercases
            if (subject.chars().anyMatch(Character::isUpperCase) || subject.chars().anyMatch(Character::isWhitespace) || subject.chars().anyMatch(Character::isAlphabetic) )
                return false;
            // check if the id is valid
            else if (!RFileOperations.validFolderName(subject) || FieldFilter.isEmptyString(subject))
                return false;
            else
                return true;
        }

    }

    public static class FileNameLikeStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            // check if there are any spaces, or uppercases
            if (!RFileOperations.validFolderName(subject) || FieldFilter.isEmptyString(subject))
                return false;
            else
                return true;
        }

    }

    /**
     * This filter is a FileNameLikeStringFilter, but it allowed <code>(none)</code>
     */
    public static class CommonFilter1 implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            // check if there are any spaces, or uppercases
            if (subject.equals("(none)")) // none is allowed
                return true;

            if (!RFileOperations.validFolderName(subject) || FieldFilter.isEmptyString(subject))
                return false;
            else
                return true;
        }
    }

}
