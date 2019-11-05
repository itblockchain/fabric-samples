package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.TransactionBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.TransactionBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Transaction;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * CreateTransaction service
 */
public class CreateTransaction extends TransactionServiceBase implements ServiceInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(CreateTransaction.class.getName());

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
        requiredServices.add("GetTransaction");
        return requiredServices;
    }

    /**
     * Creating Transaction
     * Key id has to be externally generated
     *
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "createTransaction()");

        // check basic config and auth
        super.callService(args, _stub);

        if (args.size() != 1)
            throw new CreateTransaction.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        String command = args.get(0);

        if (!checkString(command))
            throw new CreateTransaction.ParametersNotAStringException("Invalid argument, transaction command does not have a string format");

        logger.config("parameter Command " + command);
        logger.config("transaction id : " + _stub.getTxId());


        // CREATING MAIN TRANSACTION OBJECT

        // analyzing header data

        JsonObject jsonObject;
        JsonArray actionArray;
        String transactionId;
        Integer sequence;
        HashMap<String,String> tags;

        try {
            jsonObject = new JsonParser().parse(command).getAsJsonObject();
            actionArray = jsonObject.getAsJsonArray("actions");
        } catch (Exception _ex){
            throw new CreateTransaction.ParameterNotCompatibleException("Error at parsing actions of the header data of the transaction", _ex);
        }

        try {
            transactionId = jsonObject.get("transactionId").getAsString();
        } catch (Exception _ex){
            throw new CreateTransaction.ParameterNotCompatibleException("Error at parsing transaction id of the header data of the transaction", _ex);
        }

        try {
            sequence = Integer.parseInt(jsonObject.get("sequence").getAsString());
        } catch (Exception _ex){
            throw new CreateTransaction.ParameterNotCompatibleException("Error at parsing transaction sequence of the header data of the transaction", _ex);
        }

        try {
            String tagString = jsonObject.get("tags").toString();
            tags = JSONHelper.createHashMapFromString(tagString);
        } catch (Exception _ex){
            throw new CreateTransaction.ParameterNotCompatibleException("Error at parsing transaction tags of the header data of the transaction", _ex);
        }

        if (actionArray.size() ==  0){
            throw new CreateTransaction.ParameterNotCompatibleException("Number of actions is zero for the transaction");
        }

        // creating action id-s
        KeyGenerationHelper keyGen = new KeyGenerationHelper();
        List<String> actionIds = new ArrayList<String>();
        for (int i = 0; i < actionArray.size(); i++){
            String actionId = transactionId + KeyGenerationHelper.actionPrefix + i;
            actionIds.add(actionId);
        }

        TransactionBLInterface transactionBL = new TransactionBL(_stub);
        Transaction retTransaction = transactionBL.createTransaction(transactionId,sequence,tags,actionIds);

        // forking an creating actions
        for (int i = 0; i < actionArray.size(); i++) {

            String operatorType = actionArray.get(i).getAsJsonObject().get("type").getAsString();

            // issue transaction
            if (operatorType.equals("issue")){

                this.issueToken(transactionId, actionArray.get(i), _stub, tags);

                // transfer transaction
            } else if (operatorType.equals("transfer")){

                this.transferToken(transactionId, actionArray.get(i), _stub, tags);

            } else if (operatorType.equals("retire")) {

                this.retireToken(transactionId, actionArray.get(i), _stub, tags);

            } else if (operatorType.equals("merge")) {

                this.mergeToken(transactionId, actionArray.get(i), _stub);
            }
        }
        logger.exiting(this.getClass().getSimpleName(), "createTransaction()");

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
