package org.hyperledger.fabric.chaincode.BalanceTracker.Services;


import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.FlavorBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.FlavorBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Flavor;
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
 * Update Flavor Service
 */
public class UpdateFlavor extends ServiceBase implements ServiceInterface  {
    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(UpdateFlavor.class.getName());

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
        requiredServices.add("GetKey");
        requiredServices.add("CreateKey");
        requiredServices.add("GetFlavor");
        requiredServices.add("UpdateFlavor");
        return requiredServices;
    }

    /**
     * Calling updateFlavor
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "updateFlavor()");

        // check basic config and auth
        super.callService(args, _stub);

        // INPUT PARAMETER VALIDATION
        // 0 - flavorId
        // 1 - tags

        String flavorId;
        HashMap<String,String> tags;

        if (args.size() != 2)
            throw new UpdateFlavor.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 2");

        if (!checkString(args.get(0)))
            throw new UpdateFlavor.ParametersNotAStringException("Invalid argument, flavorId does not have a string format");

        if (!checkString(args.get(1)))
            throw new UpdateFlavor.ParametersNotAStringException("Invalid argument, tags does not have a string format");

        flavorId = args.get(0);

        try
        {

            tags = JSONHelper.createHashMapFromString(args.get(1));

        } catch (Exception ex){
            throw new UpdateFlavor.ParameterNotCompatibleException("Invalid argument, tags can not be converted to a key value store", ex);
        }

        logger.config("parameter Flavor ID " + flavorId);
        logger.config("parameter Tags size " + tags.size());
        logger.config("transaction id : " + _stub.getTxId());

        // BUSINESS LOGIC

        FlavorBLInterface flavorBL = new FlavorBL(_stub);
        Flavor retFlavor = flavorBL.updateFlavor(flavorId, tags);

        logger.exiting(this.getClass().getSimpleName(), "updateFlavor()");

        return retFlavor;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5013;

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
        protected Integer code = 5023;

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
        protected Integer code = 5033;

        public ParameterNotCompatibleException(String msg, Exception _cause) {
            super(msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }



}
