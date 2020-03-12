package errorhandling;

public class NoSuchNodeException extends Exception{
    public NoSuchNodeException() {}

    public NoSuchNodeException(String msg) {
    	super(msg);
    }
}
