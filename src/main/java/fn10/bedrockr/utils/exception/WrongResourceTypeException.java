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
            sb.append(String.valueOf(expect) + ",");
        }
        sb.append(") got: " + String.valueOf(got));
        super(sb.toString());
    }
}
