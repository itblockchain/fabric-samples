package org.hyperledger.fabric.chaincode.BalanceTracker.Utils;

/**
 * Console implementation of the logger functionalities
 */
public class ConsoleLogger implements LoggerInterface {

    private LogEnum logGranuality;

    public ConsoleLogger(LogEnum _logGranuality){
        logGranuality = _logGranuality;
    }


    public void Log(String _message, LogLevelEnum _logLevel, Object source){

        if (logGranuality == LogEnum.DEBUG){
            System.out.println("@BALANCETRACKER Level: " + _logLevel + ", Message : " + _message + " Source : " + source == null ? "" : source.toString());
        }
        else if ((_logLevel == LogLevelEnum.ERROR) || (_logLevel == LogLevelEnum.CRITICAL)){
            System.out.println("@BALANCETRACKER Level: " + _logLevel + ", Message : " + _message + " Source : " + source == null ? "" : source.toString());
        }
    }

    public void LogError(Throwable _exception, Object source){
        System.out.println("@BALANCETRACKER Level: ERROR, Message : " + _exception.toString() + " Source : " + source == null ? "" : source.toString());
        _exception.printStackTrace(System.out);
    }
}
