package fn10.bedrockr.addons.source;

import java.util.Locale;

import fn10.bedrockr.utils.RFileOperations;

public class FieldFilters {

    public interface FieldFilter {

        Boolean getValid(String subject);

        public static boolean isEmptyString(String subject) {
            return subject.trim().equals("");
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
            // Launcher.LOG.info("message is \\\""+ subject.trim()+"\\\"\n"+
            // String.valueOf(subject.trim().equals("")));
            if (FieldFilter.isEmptyString(subject)) {
                return false;
            } else
                return true;
        }

    }

    public static class IDStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            //check if there are any spaces, or uppercases
            if (subject.chars().anyMatch(Character::isUpperCase) || subject.chars().anyMatch(Character::isWhitespace))
                return false;
                //check if the id is valid
            else if (!RFileOperations.validFolderName(subject) || FieldFilter.isEmptyString(subject))
                return false;
            else
                return true;
        }

    }

    public static class FileNameLikeStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            //check if there are any spaces, or uppercases
            //if (subject.chars().anyMatch(Character::isUpperCase) || subject.chars().anyMatch(Character::isWhitespace))
            //    return false;
            //    //check if the id is valid
            //else
            if (!RFileOperations.validFolderName(subject) || FieldFilter.isEmptyString(subject))
                return false;
            else
                return true;
        }

    }

}
