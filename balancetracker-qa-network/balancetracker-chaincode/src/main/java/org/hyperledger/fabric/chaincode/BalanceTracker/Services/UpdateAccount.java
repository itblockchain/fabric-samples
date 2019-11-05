package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.AccountBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.AccountBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
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

public class UpdateAccount extends ServiceBase implements ServiceInterface   {
    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(UpdateAccount.class.getName());

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
        return requiredServices;
    }

    /**
     * Update account specified by account id
     * Only tags can be updated
     *
     * @param  args arguments of the call
     * @param _stub chaincodestub
     * @return       chaincode response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        // INPUT PARAMETER VALIDATION
        // 0 - accountId
        // 2 - tags

        logger.entering(this.getClass().getSimpleName(), "updateAccount()");

        // check basic config and auth
        super.callService(args, _stub);

        String accountId;
        HashMap<String,String> tags;

        if (args.size() != 2)
            throw new UpdateAccount.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 2");

        if (!checkString(args.get(0)))
            throw new UpdateAccount.ParametersNotAStringException("Invalid argument, accountId does not have a string format");

        if (!checkString(args.get(1)))
            throw new UpdateAccount.ParametersNotAStringException("Invalid argument, tags does not have a string format");

        accountId = args.get(0);

        try {
            tags = JSONHelper.createHashMapFromString(args.get(1));
        }catch (Exception ex){
            throw new UpdateAccount.ParameterNotCompatibleException("Invalid argument, tags can not be converted to a string array", ex);
        }

        logger.config("parameter Account Id " + accountId);
        logger.config("parameter Tags Size " + tags.size());
        logger.config("transaction id : " + _stub.getTxId());

        // BUSINESS LOGIC

        AccountBLInterface accountBL = new AccountBL(_stub);
        Account retAccount = accountBL.updateAccount(accountId, tags);

        logger.exiting(this.getClass().getSimpleName(), "updateAccount()");

        return  retAccount;
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

    /**
     * Input can not be converted
     */
    public static class ParameterNotCompatibleException extends InputParameterInvalidException
    {
        protected Integer code = 5031;

        public ParameterNotCompatibleException(String msg, Exception _cause) {
            super(msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }
}
