package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.TransactionBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.TransactionBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Transaction;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * GetTransaction service
 */
public class GetTransaction extends TransactionServiceBase implements ServiceInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(GetTransaction.class.getName());

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
        requiredServices.add("GetAction");
        return requiredServices;
    }

    /**
     * Getting Transaction
     * Key id has to be externally generated
     *
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getTransaction()");

        // check basic config and auth
        super.callService(args, _stub);

        // input parameter validation
        // 0 - transactionId

        if (args.size() != 1)
            throw new GetTransaction.ParametersIncorrectNumberException("Incorrect number of arguments, expecting 1");

        if (!checkString(args.get(0)))
            throw new GetTransaction.ParametersNotAStringException("Invalid argument, transactionId does not have a string format");

        String transactionId = args.get(0);

        logger.config("parameter Transaction ID " + transactionId);
        logger.config("transaction id : " + _stub.getTxId());

        TransactionBLInterface transactionBL = new TransactionBL(_stub);
        Transaction retTransaction = transactionBL.getTransaction(transactionId);

        logger.exiting(this.getClass().getSimpleName(), "getTransaction()");

        return retTransaction;
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5019;

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
        protected Integer code = 5029;

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
        protected Integer code = 5039;

        public ParameterNotCompatibleException(String msg) {
            super(msg);
        }

        public ParameterNotCompatibleException(String msg, Exception _cause) {
            super(msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}
