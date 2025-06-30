package fn10.bedrockr.addons.source;

import fn10.bedrockr.Launcher;

public class FieldFilters {

    public interface FieldFilter {
        
        Boolean getValid(String subject);
        
    }

    public static class RegularStringFilter implements FieldFilter {

        @Override
        public Boolean getValid(String subject) {
            Launcher.LOG.info("message is \\\""+ subject.trim()+"\\\"\n"+ String.valueOf(subject.trim().equals("")));
            if (subject.trim().equals("")) {
                return false;
            } else 
                return true;
        }
    
        
    }

}
