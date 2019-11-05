package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.MasterBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Config;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Create service, setting up the whole service level
 */
public class InitServices extends ServiceBase implements ServiceInterface  {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(InitServices.class.getName());

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
     * Creating the service configuration
     * Key id has to be externally generated
     *
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "createService()");

        // check basic config and auth
        super.callService(args, _stub);

        // INPUT PARAMETER VALIDATION
        // 0 - Service list
        // 1 - Storage model

        List<String> services;
        HashMap<String,String> storageModel;

        if (args.size() != 2)
            throw new InitServices.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 2");

        if (!checkString(args.get(0)))
            throw new InitServices.ParametersNotAStringException("Invalid argument, service list does not have a string format");

        if (!checkString(args.get(1)))
            throw new InitServices.ParametersNotAStringException("Invalid argument, storage model does not have a string format");

        try{

            storageModel = JSONHelper.createHashMapFromString(args.get(1));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, storage model can not be converted to a value key store", ex);
        }

        try{

            services = JSONHelper.createArrayFromString(args.get(0));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, services can not be converted to a string array", ex);
        }

        MasterBL masterBL = new MasterBL(_stub);
        Config ret = masterBL.createConfig(services,storageModel);

        logger.exiting(this.getClass().getSimpleName(), "createService()");
        return ret;
    }

    /**
     * Create service can run if it is still not set
     */
    @Override
    public boolean isConfiguredToRun(ChaincodeStub _stub) throws BalanceTrackerException{
        MasterBL masterBL = new MasterBL(_stub);
        Config config = masterBL.getConfig();
        if (config == null){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5010;

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
        protected Integer code = 5020;

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
        protected Integer code = 5030;

        public ParameterNotCompatibleException(String msg, Exception _cause) {
            super(msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

}
