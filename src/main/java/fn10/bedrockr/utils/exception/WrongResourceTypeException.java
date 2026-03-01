package fn10.bedrockr.utils.exception;

public class WrongResourceTypeException extends Exception {
    /**
     * Contructs a WrongResourceTypeException
     * 
     * @param expected the resource type expected
     * @param got      the resource type supplied
     */
    public WrongResourceTypeException(int got, int... expected) {
        StringBuilder sb = new StringBuilder("Expected Resource type: (");
        for (int expect : expected) {
            sb.append(expect + ",");
        }
        sb.append(") got: " + got);
        super(sb.toString());
    }
}
