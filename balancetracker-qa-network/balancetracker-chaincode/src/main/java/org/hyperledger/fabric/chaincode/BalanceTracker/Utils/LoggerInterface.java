package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

/**
 * Interface for the logger functionalities
 */
public interface LoggerInterface {

    public void Log(String _message, LogLevelEnum _logLevel, Object source);

    public void LogError(Throwable _exception, Object source);

}
