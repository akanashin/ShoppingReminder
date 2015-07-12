package utils.datatypes;

import static utils.Utils.compare;

/**
 *  Structure used for reporting result of database operations
 *  Contains only one of its members:
 *   result  - result of operation  -or-
 *   message - error message in case of any error
 */
public class Result<ResultType> {
    public final ResultType result;
    public final String     message;

    public Result(ResultType a_result, String a_message) {
        result  = a_result;
        message = a_message;
    }

    public Result(ResultType a_result) {
        result = a_result;
        message = "";
    }

    public boolean equals(Object pt2) {
        if(!(pt2 instanceof Result))
            throw new ClassCastException("Object is not Result");

        // auto false (this object is NOT null)
        if (pt2 == null)
            return false;

        // are this object and pt2 the same object?
        if (this == pt2)
            return true;

        // now we know that pt2 is not null
        //  compare this and pt2 field by field
        //  (ignoring ID)
        return compare(result, ((Result)pt2).result)
                && compare(message, ((Result) pt2).message);
    }
}
