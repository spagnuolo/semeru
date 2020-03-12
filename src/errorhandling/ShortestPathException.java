package errorhandling;

public class ShortestPathException extends Exception {
    public ShortestPathException() {};

    public ShortestPathException(String msg) {
        super(msg);
    }
}
