package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class TransactionBL extends BusinessLogicBase implements TransactionBLInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(TransactionBL.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * TokenService logic constructor
     * Chaincode stub has to be initialized
     */
    public TransactionBL(ChaincodeStub _stub) throws TransactionBL.StubIsNullException {
        if (_stub == null){
            throw new TransactionBL.StubIsNullException("Stub can not be null at initializing the transaction business logic");
        }
        this.stub = _stub;
    }

    /**
     * Getting transaction by transaction id
     *
     * @param  transactionId arguments of the call
     * @return       chaincode response
     */
    public Transaction getTransaction(String transactionId) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getTransaction()");
        logger.finer("Parameters : transactionId : " + transactionId);
        logger.finer("transaction id : " + this.getStub().getTxId());

            String transactionString = this.getStringState(transactionId, TRANSACTION);

            if (transactionString == null)
                return null;

            if(!checkString(transactionString))
                return null;

            Transaction tr = Transaction.createTransaction(transactionString);

        logger.exiting(this.getClass().getSimpleName(), "getTransaction()");
        logger.finer("result : " + tr.toJSONString());

        return tr;
    }

    /**
     * Creating a new token
     *
     * @param  _transactionId arguments of the call
     * @param  _sequence arguments of the call
     * @param  _tags arguments of the call
     * @param  _actionIds arguments of the call
     * @return       chaincode response
     */
    public Transaction createTransaction(String _transactionId, Integer _sequence, HashMap<String,String> _tags, List<String> _actionIds)  throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "createTransaction()");
        logger.finer("Parameters : transactionId : " + _transactionId +
                "sequence : " + _sequence);

        // Business logic error handling
            // TransactionId error handling
            if ((_transactionId == null) || (_transactionId.length() == 0) ){
                throw new TransactionBL.ParameterIsNullException("Cannot create Transaction, Transaction Id is null");
            }

            if (this.getTransaction(_transactionId) != null){
                throw new TransactionBL.IdTakenException("Cannot create Transaction, Transaction Id is already taken");
            }

            // checking if actionIds exist

            // checking if action keys id references exist
            // at transaction creation, the action id-s are still not available

 //           ActionBL actionBL = new ActionBL(this.getStub());
 //           for (String _id : _actionIds){
 //               if (actionBL.getAction(_id) == null){
 //                   throw new BalanceTrackerException("Referred action id does not exist at the createTransaction call of the Transaction service, actionID: " + _id);
 //               }
 //           }

            // creating the timestamp at creation
             Instant issuedAtI = this.getStub().getTxTimestamp();
             long _timestamp = issuedAtI.getEpochSecond();

            // create new Transaction and write it into the blockchain
            Transaction transactionToStore = new Transaction(_transactionId,_sequence,_timestamp, _tags,_actionIds);

            try {

                this.putState(_transactionId, (new ObjectMapper()).writeValueAsBytes(transactionToStore), TRANSACTION);
                this.getStub().setEvent(Constants.EventNames.TRANSACTION_CREATED, (new ObjectMapper()).writeValueAsBytes(transactionToStore));

            } catch (JsonProcessingException _ex){
                throw new Transaction.SerializationException("Error at writing Transaction into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "createTransaction()");
        logger.finer("result : " + transactionToStore.toJSONString());

        return transactionToStore;

    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Input is not a string
     */
    public static class ParameterIsNullException extends InputParameterInvalidException
    {
        protected Integer code = 5029;

        public ParameterIsNullException(String msg) {
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

    /**
     * Account does not exsit -> can not be queried, can not be updated
     */
    public static class NotExistException extends InputParameterInvalidException
    {
        protected Integer code = 5049;

        public NotExistException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Initialization error : Stub is null
     */
    public static class StubIsNullException extends BalanceTrackerException
    {
        protected Integer code = 5099;

        public StubIsNullException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Id is already taken
     */
    public static class IdTakenException extends BalanceTrackerException
    {
        protected Integer code = 5089;

        public IdTakenException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }



}
