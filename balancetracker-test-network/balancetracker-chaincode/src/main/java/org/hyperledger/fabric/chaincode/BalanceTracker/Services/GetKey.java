package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.KeyBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.KeyBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Key;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Getkey service
 */
public class GetKey extends ServiceBase implements ServiceInterface {

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
        return requiredServices;
    }

    /**
     * Getting key by key id
     *
     * @param  args arguments of the call
     * @param  _stub chaincode stub
     * @return       Key response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getKey()");

        // check basic config and auth
        super.callService(args, _stub);

        // input parameter validation
        // 0 - keyId

        if (args.size() != 1)
            throw new GetKey.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        String keyId = args.get(0);

        logger.config("parameter Key ID " + keyId);
        logger.config("transaction id : " + _stub.getTxId());

        if (!checkString(keyId))
            throw new GetKey.ParametersNotAStringException("Invalid argument, keyId does not have a string format");

        KeyBLInterface keyBL = new KeyBL(_stub);
        Key retKey = keyBL.getKey(keyId);

        logger.exiting(this.getClass().getSimpleName(), "getKey()");

        return retKey;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5014;

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
        protected Integer code = 5024;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }
}
