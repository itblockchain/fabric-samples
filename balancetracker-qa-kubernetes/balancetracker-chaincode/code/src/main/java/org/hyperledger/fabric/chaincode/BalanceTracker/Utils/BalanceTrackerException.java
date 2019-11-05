package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;


/**
 * Simplified exception handling for internal exceptions of the BalanceTracker
 *
 */
public class BalanceTrackerException extends Exception {

    //Each exception message will be held here
    protected String message;
    protected Integer code = 5000;

    public BalanceTrackerException(String _msg)
    {
        super(_msg);
        this.message = _msg;
    }

    public BalanceTrackerException(String _msg, Exception _cause)
    {
        super(_msg, _cause);
        this.message = _msg;
    }


    public BalanceTrackerException(String _msg, Integer _code)
    {
        super(_msg);
        this.message = _msg;
        this.code = _code;
    }

    public BalanceTrackerException(String _msg, Integer _code, Exception _cause)
    {
        super(_msg, _cause);
        this.message = _msg;
        this.code = _code;

    }


    //Message can be retrieved using this accessor method
    public String getMessage() {
        return message;
    }

    //Message can be retrieved using this accessor method
    public Integer getErrorCode() {
        return code;
    }
}
