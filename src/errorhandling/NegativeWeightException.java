package errorhandling;

public class NegativeWeightException extends Exception {
    public NegativeWeightException() {};

    public NegativeWeightException(String msg) {
        super(msg);
    }
}
