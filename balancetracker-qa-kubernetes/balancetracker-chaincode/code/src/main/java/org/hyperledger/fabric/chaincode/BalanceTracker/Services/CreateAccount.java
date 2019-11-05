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

/**
 * Create Account Service
 */
public class CreateAccount extends ServiceBase implements ServiceInterface {
    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(CreateAccount.class.getName());

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
        requiredServices.add("GetAccount");
        requiredServices.add("GetKey");
        requiredServices.add("CreateKey");
        return requiredServices;
    }

    /**
     * Calling createAccount
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException{
        logger.entering(this.getClass().getSimpleName(), "createAccount()");

        // check basic config and auth
        super.callService(args, _stub);

        // INPUT PARAMETER VALIDATION
        // 0 - accountId
        // 2 - quorum
        // 3 - tags
        // 4 - keyIds

        String accountId;
        int quorum;
        HashMap<String,String> tags;
        List<String> keyIds;

        if (args.size() != 4)
            throw new CreateAccount.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 5");

        if (!checkString(args.get(0)))
            throw new CreateAccount.ParametersNotAStringException("Invalid argument, accountId does not have a string format");

        if (!checkString(args.get(1)))
            throw new CreateAccount.ParametersNotAStringException("Invalid argument, quorum does not have a string format");

        if (!checkString(args.get(2)))
            throw new CreateAccount.ParametersNotAStringException("Invalid argument, tags does not have a string format");

        if (!checkString(args.get(3)))
            throw new CreateAccount.ParametersNotAStringException("Invalid argument, keyIds does not have a string format");

        accountId = args.get(0);

        try {
            quorum = Integer.parseInt(args.get(1));
        } catch (Exception ex){
            throw new CreateAccount.ParameterNotCompatibleException("Invalid argument, quorum can not be converted to an integer ", ex);
        }

        try {
            tags = JSONHelper.createHashMapFromString(args.get(2));
        } catch (Exception ex){
            throw new CreateAccount.ParameterNotCompatibleException("Invalid argument, tags can not be converted to a value key store", ex);
        }

        try {
            keyIds = JSONHelper.createArrayFromString(args.get(3));
        } catch (Exception ex){
            throw new CreateAccount.ParameterNotCompatibleException("Invalid argument, keyIds can not be converted to a string array", ex);
        }

        logger.config("parameter Account Id " + accountId);
        logger.config("parameter Quorum  " + quorum);
        logger.config("parameter Tags size " + tags.size());
        logger.config("parameter Keys size " + keyIds.size());
        logger.config("transaction id : " + _stub.getTxId());


                // Business Logic

        AccountBLInterface accountBL = new AccountBL(_stub);
        Account retAccount = accountBL.createAccount(accountId, tags, quorum, keyIds);

        logger.exiting(this.getClass().getSimpleName(), "createAccount()");

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
