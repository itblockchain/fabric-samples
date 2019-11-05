package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.ActionBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.ActionBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Action;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Get Action Service
 */
public class GetAction extends ServiceBase implements ServiceInterface  {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(GetKey.class.getName());

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
        requiredServices.add("CreateAccount");
        requiredServices.add("GetAccount");
        requiredServices.add("UpdateAccount");
        return requiredServices;
    }

    /**
     * Getting action by action id
     *
     * @param  args arguments of the call
     * @param  _stub Chaincode stub
     * @return       chaincode response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        // INPUT PARAMETER VALIDATION
        // 0 - actionId

        logger.entering(this.getClass().getSimpleName(), "getAction()");

        // check basic config and auth
        super.callService(args, _stub);

        if (args.size() != 1)
            throw new GetAction.ParametersIncorrectNumberException("Incorrect number of arguments, expecting 1");

        String actionId = args.get(0);

        logger.config("parameter Action ID " + actionId);
        logger.config("transaction id : " + _stub.getTxId());


        if (!checkString(actionId))
            throw new GetAction.ParametersNotAStringException("Invalid argument, actionId does not have a string format");

        ActionBLInterface actionBL = new ActionBL(_stub);
        Action action = actionBL.getAction(actionId);

        logger.exiting(this.getClass().getSimpleName(), "getAction()");

        return action;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5012;

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
        protected Integer code = 5022;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

}
