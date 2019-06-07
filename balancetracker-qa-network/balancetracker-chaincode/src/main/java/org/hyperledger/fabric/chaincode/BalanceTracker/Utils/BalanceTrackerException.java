package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

/**
 * Simplified exception handling for internal exceptions of the BalanceTracker
 *
 */
public class BalanceTrackerException extends Exception {

    protected LoggerInterface logger;
    protected Integer code = 500;

    public BalanceTrackerException(){
        super();
    }

    public BalanceTrackerException(String message, int _code){
        super(message);
        this.code = _code;
        logger = new ConsoleLogger(LogEnum.DEBUG);
        logger.Log(message + " code " + _code, LogLevelEnum.ERROR ,this);
    }


    public BalanceTrackerException(String message){
        super(message);
        logger = new ConsoleLogger(LogEnum.DEBUG);
        logger.Log(message, LogLevelEnum.ERROR ,this);
    }

    public BalanceTrackerException(Throwable ex)
    {
        super(ex.getMessage());
        logger = new ConsoleLogger(LogEnum.DEBUG);
        logger.Log(ex.getMessage(), LogLevelEnum.ERROR, ex);
        logger.LogError(ex,ex);
    }

    public Integer getErrorCode(){
        return code;
    }

}
