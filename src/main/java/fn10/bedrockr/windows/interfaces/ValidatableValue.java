package fn10.bedrockr.windows.interfaces;

public interface ValidatableValue {
    /**
     * 
     * @param strict - Specifies if this needs to be fully valid, even for drafting
     * @return a bool indicating if the value is allowed.
     */
    boolean valid(boolean strict);
    
    boolean valid();

    String getProblemMessage();

    String getName();
}
