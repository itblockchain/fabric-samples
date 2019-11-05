package org.hyperledger.fabric.chaincode.BalanceTracker.Services;


import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.FlavorBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.FlavorBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Flavor;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Get Flavor Service
 */
public class GetFlavor  extends ServiceBase implements ServiceInterface  {
    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(GetFlavor.class.getName());

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
        return requiredServices;
    }


    /**
     * Calling getFlavor
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getFlavor()");

        // check basic config and auth
        super.callService(args, _stub);

        // input parameter validation - strict
        // 0 - flavorId

        if (args.size() != 1)
            throw new GetFlavor.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        if (!checkString(args.get(0)))
            throw new GetFlavor.ParametersNotAStringException("Invalid argument, flavorId does not have a string format");

        String flavorId = args.get(0);

        logger.config("parameter Flavor ID " + flavorId);
        logger.config("transaction id : " + _stub.getTxId());

        FlavorBLInterface flavorBL = new FlavorBL(_stub);
        Flavor retFlavor = flavorBL.getFlavor(flavorId);

        logger.exiting(this.getClass().getSimpleName(), "getFlavor()");

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

}
