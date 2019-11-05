package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * SetLogLevel service
 */
public class SetLogLevel extends ServiceBase implements ServiceInterface  {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(SetLogLevel.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Required services
     */
    public List<String> requiredServiceNames(){
        List<String> requiredServices = new ArrayList<String>();
        return requiredServices;
    }

    /**
     * Setting the log level master service
     * @param  args arguments of the call
     * @param  _stub Chaincode stub
     * @return       chaincode response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "setLogLevel()");

        // check basic config and auth
        super.callService(args, _stub);

        if (args.size() != 1)
            throw new SetLogLevel.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        if (!checkString(args.get(0)))
            throw new SetLogLevel.ParametersNotAStringException("Invalid argument, log level does not have a string format");

        String logLevelString = args.get(0);

        Level logLevel = Level.INFO;

        try {

            logLevel = Level.parse(logLevelString);
        }
        catch (Exception _ex){
            throw  new SetLogLevel.ParameterNotCompatibleException("Incorrect log level", _ex);
        }

        BalanceTrackerBase.logLevel = logLevel;
        // setting new log level
        Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();

        while(loggerNames.hasMoreElements()){
            String loggerName = loggerNames.nextElement();
            LogManager.getLogManager().getLogger(loggerName).setLevel(BalanceTrackerBase.logLevel);
        }

        logger.exiting(this.getClass().getSimpleName(), "setLogLevel()");

        return logLevel.toString();
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5015;

        public ParametersIncorrectNumberException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Input is not a string
     */
    public static class ParametersNotAStringException extends InputParameterInvalidException
    {
        protected Integer code = 5025;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Input can not be converted
     */
    public static class ParameterNotCompatibleException extends InputParameterInvalidException
    {
        protected Integer code = 5035;

        public ParameterNotCompatibleException(String msg) {
            super(msg);
        }

        public ParameterNotCompatibleException(String msg, Exception _cause) {
            super(msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

}
