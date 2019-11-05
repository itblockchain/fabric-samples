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
 * Create Flavor Service
 */
public class CreateFlavor extends ServiceBase implements ServiceInterface  {
    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(CreateFlavor.class.getName());

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
        return requiredServices;
    }

    /**
     * Calling createFlavor
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "createFlavor()");

        // check basic config and auth
        super.callService(args, _stub);

        // INPUT PARAMETER VALIDATION
        // 0 - flavorId
        // 1 - isFungible
        // 2 - quorum
        // 3 - tags
        // 4 - keyIds

        String flavorId;
        boolean isFungible;
        int quorum;
        HashMap<String,String> tags;
        List<String> keyIds;

        if (args.size() != 5)
            throw new CreateFlavor.ParametersIncorrectNumberException("Incorrect number of arguments, expecting 5");

        if (!checkString(args.get(0)))
            throw new CreateFlavor.ParametersNotAStringException("Invalid argument, flavorId does not have a string format");

        if (!checkString(args.get(1)))
            throw new CreateFlavor.ParametersNotAStringException("Invalid argument, isFungible does not have a string format");

        if (!checkString(args.get(2)))
            throw new CreateFlavor.ParametersNotAStringException("Invalid argument, quorum does not have a string format");

        if (!checkString(args.get(3)))
            throw new CreateFlavor.ParametersNotAStringException("Invalid argument, tags does not have a string format");

        if (!checkString(args.get(4)))
            throw new CreateFlavor.ParametersNotAStringException("Invalid argument, keyIds does not have a string format");

        flavorId = args.get(0);

        try{

            isFungible = Boolean.parseBoolean(args.get(1));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, isFungible can not be converted to a boolean", ex);
        }

        try{

            quorum = Integer.parseInt(args.get(2));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, quorum can not be converted to an integer", ex);
        }

        try{

            tags = JSONHelper.createHashMapFromString(args.get(3));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, tags can not be converted to a value key store", ex);
        }

        try{

            keyIds = JSONHelper.createArrayFromString(args.get(4));

        } catch (Exception ex){
            throw new CreateFlavor.ParameterNotCompatibleException("Invalid argument, keyIds can not be converted to a string array", ex);
        }

        logger.config("parameter Flavor ID " + flavorId);
        logger.config("parameter Quorum " + quorum);
        logger.config("parameter Tags Size " + tags.size());
        logger.config("parameter Key IDs " + keyIds.size());
        logger.config("transaction id : " + _stub.getTxId());


        // BUSINESS LOGIC

        FlavorBLInterface flavorBL = new FlavorBL(_stub);
        Flavor retFlavor = flavorBL.createFlavor(flavorId, isFungible, tags, quorum, keyIds);

        logger.exiting(this.getClass().getSimpleName(), "createFlavor()");

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
