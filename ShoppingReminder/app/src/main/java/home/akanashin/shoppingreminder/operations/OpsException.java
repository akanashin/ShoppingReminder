package home.akanashin.shoppingreminder.operations;

/**
 * Exception used to handle database operation errors
 */
public class OpsException extends Throwable {
    // messages for errors
    public static String MSG_INVALID = "Something is wrong in data";
    public static String MSG_EMPTY_NAME = "Name cannot be empty";
    public static String MSG_PLACE_NAME_IS_NOT_UNIQUE = "column name is not unique (code 19)"; // this is message from SQLite
    public static String MSG_PLACE_TYPE_NAME_IS_NOT_UNIQUE = "column note is not unique (code 19)"; // this is message from SQLite
    public static String MSG_EMPTY_LIST_OF_TYPES = "List of typeIds cannot be empty";
    public static String MSG_EITHER_PLACE_OR_TYPE = "Task only can have either set of places or typeIds";

    private final String message;

    public OpsException(String a_message) {
        message = a_message;
    }

    public String getMessage() {
        return message;
    }
}
