package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.MasterBL;
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
 * GetStorageModel service
 */
public class GetStorageModel extends ServiceBase implements ServiceInterface  {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(GetServices.class.getName());

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
     * Getting the installed services master service
     * @param  args arguments of the call
     * @param  _stub Chaincode stub
     * @return       chaincode response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "getServices()");

        // check basic config and auth
        super.callService(args, _stub);

        if (args.size() != 0)
            throw new GetServices.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 0");

        MasterBL masterBL = new MasterBL(_stub);
        HashMap<String, String> storageModel = masterBL.getConfig().getStorageModel();

        String toRet = JSONHelper.createJsonStringFromHashMap(storageModel);

        logger.exiting(this.getClass().getSimpleName(), "getServices()");

        return toRet;
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




}