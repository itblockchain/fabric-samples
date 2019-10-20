package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.AccountBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.AccountBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Get Account Service
 */
public class GetAccount extends ServiceBase implements ServiceInterface {
    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(GetAccount.class.getName());

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
     * Getting account by account id
     *
     * @param  args arguments
     * @param _stub Chaincode stub
     * @return Account response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        // INPUT PARAMETER VALIDATION
        // 0 - accountId

        logger.entering(this.getClass().getSimpleName(), "getAccount()");

        // check basic config and auth
        super.callService(args, _stub);

        if (args.size() != 1)
            throw new GetAccount.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        if (!checkString(args.get(0)))
            throw new GetAccount.ParametersNotAStringException("Invalid argument, accountId does not have a string format");

        String accountId = args.get(0);

        logger.config("parameter Account ID " + accountId);
        logger.config("transaction id : " + _stub.getTxId());


        // BUSINESS LOGIC

        AccountBLInterface accountBL = new AccountBL(_stub);
        Account retAccount = accountBL.getAccount(accountId);

        logger.exiting(this.getClass().getSimpleName(), "getAccount()");

        return retAccount;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5011;

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
        protected Integer code = 5021;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }
}
