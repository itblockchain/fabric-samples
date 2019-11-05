package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

/**
 * Ancestor class for invalid input parameters
 *
 */
public class InputParameterInvalidException extends BalanceTrackerException {

    public InputParameterInvalidException(String _msg)
    {
        super(_msg);
    }

    public InputParameterInvalidException(String _msg, Exception _cause)
    {
        super(_msg, _cause);
    }


    public InputParameterInvalidException(String _msg, Integer _code)
    {
        super(_msg);
    }

    public InputParameterInvalidException(String _msg, Integer _code, Exception _cause)
    {
        super(_msg, _cause);
    }

}
