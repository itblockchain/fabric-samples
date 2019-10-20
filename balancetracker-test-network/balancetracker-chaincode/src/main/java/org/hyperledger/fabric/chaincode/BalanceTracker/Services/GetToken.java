package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.TokenBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.TokenBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.TokenEx;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * GetToken service
 */
public class GetToken extends ServiceBase implements ServiceInterface  {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(GetToken.class.getName());

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
        requiredServices.add("CreateFlavor");
        requiredServices.add("GetFlavor");
        requiredServices.add("UpdateFlavor");
        return requiredServices;
    }


    /**
     * Getting token by id
     *
     * @param  args arguments of the call
     * @param  _stub chaincode stub
     * @return       Key response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "getToken()");

        // check basic config and auth
        super.callService(args, _stub);

        // INPUT PARAMETER VALIDATION
        // 0 - tokenId

        if (args.size() != 1)
            throw new GetToken.ParametersIncorrectNumberException("Incorrect number of arguments, expecting 1");

        if (!checkString(args.get(0)))
            throw new GetToken.ParametersNotAStringException("Invalid argument, tokenId does not have a string format");

        String tokenId = args.get(0);

        logger.config("parameter Token ID " + tokenId);
        logger.config("transaction id : " + _stub.getTxId());

        TokenBLInterface tokenBL = new TokenBL(_stub);
        TokenEx retToken = tokenBL.getTokenEx(tokenId);

        logger.exiting(this.getClass().getSimpleName(), "getToken()");

        return retToken;
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5018;

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
        protected Integer code = 5028;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

}
