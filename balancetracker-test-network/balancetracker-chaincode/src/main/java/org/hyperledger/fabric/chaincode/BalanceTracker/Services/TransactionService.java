package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.TokenEx;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Transaction;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Services related to Transactions and transaction building
 */
public class TransactionService extends ServiceBase {

    /**
     * TransactionService logic constructor
     * Chaincode stub has to be initialized
     */
    public TransactionService(ChaincodeStub _stub) {
        this.stub = _stub;
    }

    /**
     * Starting an issue token transaction
     * Usable only from internal
     *
     * @param  _transactionId  Chaincode stub
     * @param  _actionId  Chaincode stub*
     * @param  jsonParams  Chaincode stub
     *
     * @return       chaincode response
     */
    protected void issueToken(String _transactionId, String _actionId, JsonElement jsonParams) throws BalanceTrackerException {

        try {

            // analyzing input parameters
            String tokenId = jsonParams.getAsJsonObject().get("tokenId").getAsString();
            String code = jsonParams.getAsJsonObject().get("tokenCode").getAsString();
            Integer amount = Integer.parseInt(jsonParams.getAsJsonObject().get("amount").getAsString());
            String flavorId = jsonParams.getAsJsonObject().get("flavorId").getAsString();
            String destinationAccountId = jsonParams.getAsJsonObject().get("destinationAccountId").getAsString();

            JsonArray actionTagsJsonArray = jsonParams.getAsJsonObject().get("actionTags").getAsJsonArray();
            List<String> actionTagsArray = JSONHelper.convertJSONArrayToArray(actionTagsJsonArray);

            JsonArray tokenTagsJsonArray = jsonParams.getAsJsonObject().get("tokenTags").getAsJsonArray();
            List<String> tokenTagsArray = JSONHelper.convertJSONArrayToArray(tokenTagsJsonArray);

            // calling issue action
            ActionBL actionBL = new ActionBL(this.getStub());
            actionBL.issue(_transactionId,_actionId,tokenId, code, amount,flavorId,destinationAccountId, actionTagsArray, tokenTagsArray);


            logger.Log("Token issunance has been successfully finished", LogLevelEnum.INFO, this);

        } catch(Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }


    /**
     * Starting a transfer token transaction
     * Usable only from internal
     *
     * @param  _transactionId  Chaincode stub
     * @param  _actionId  Chaincode stub*
     * @param  jsonParams  Chaincode stub
     *
     * @return       chaincode response
     */
    protected void transferToken(String _transactionId, String _actionId, JsonElement jsonParams) throws BalanceTrackerException {
        try {

            // analyzing input parameters
            Integer amount = Integer.parseInt(jsonParams.getAsJsonObject().get("amount").getAsString());
            String tokenId = jsonParams.getAsJsonObject().get("tokenId").getAsString();
            String newTokenId = jsonParams.getAsJsonObject().get("newTokenId").getAsString();
            String destinationAccountId = jsonParams.getAsJsonObject().get("destinationAccountId").getAsString();

            JsonArray actionTagsJsonArray = jsonParams.getAsJsonObject().get("actionTags").getAsJsonArray();
            List<String> actionTagsArray = JSONHelper.convertJSONArrayToArray(actionTagsJsonArray);

            JsonArray tokenTagsJsonArray = jsonParams.getAsJsonObject().get("tokenTags").getAsJsonArray();
            List<String> tokenTagsArray = JSONHelper.convertJSONArrayToArray(tokenTagsJsonArray);


            // calling transfer action
            ActionBL actionBL = new ActionBL(this.getStub());
            actionBL.transfer(_transactionId,_actionId,tokenId, newTokenId, amount, destinationAccountId, actionTagsArray, tokenTagsArray);

            logger.Log("Token transfer has been successfully finished", LogLevelEnum.INFO, this);

        } catch(Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }


    /**
     * Starting a retire token transaction
     * Usable only from internal
     *
     * @param  _transactionId  Chaincode stub
     * @param  _actionId  Chaincode stub*
     * @param  jsonParams  Chaincode stub
     *
     * @return       chaincode response
     */
    protected void retireToken(String _transactionId, String _actionId, JsonElement jsonParams) throws BalanceTrackerException
    {
        try {

            // analyzing input parameters
            Integer amount = Integer.parseInt(jsonParams.getAsJsonObject().get("amount").getAsString());
            String tokenId = jsonParams.getAsJsonObject().get("tokenId").getAsString();
            JsonArray actionTagsJsonArray = jsonParams.getAsJsonObject().get("actionTags").getAsJsonArray();
            List<String> actionTagsArray = JSONHelper.convertJSONArrayToArray(actionTagsJsonArray);

            // calling retire action
            ActionBL actionBL = new ActionBL(this.getStub());
            actionBL.retire(_transactionId,_actionId,tokenId,  amount, actionTagsArray);


            logger.Log("Token retirement has been successfully finished", LogLevelEnum.INFO, this);

        } catch(Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }


    /**
     * Starting a retire token transaction
     * Usable only from internal
     *
     * @param  _transactionId  Chaincode stub
     * @param  _actionId  Chaincode stub*
     * @param  jsonParams  Chaincode stub
     *
     * @return       chaincode response
     */
    protected void mergeToken(String _transactionId, String _actionId, JsonElement jsonParams) throws BalanceTrackerException
    {
        try {

            // analyzing input parameters
            JsonArray tokenIdsJsonArray = jsonParams.getAsJsonObject().get("tokenIds").getAsJsonArray();
            List<String> tokenIdsArray = JSONHelper.convertJSONArrayToArray(tokenIdsJsonArray);
            String newTokenId = jsonParams.getAsJsonObject().get("newTokenId").getAsString();
            JsonArray tokenTagsJsonArray = jsonParams.getAsJsonObject().get("tokenTags").getAsJsonArray();
            List<String> tokenTagsArray = JSONHelper.convertJSONArrayToArray(tokenTagsJsonArray);

            // calling merge action
            ActionBL actionBL = new ActionBL(this.getStub());
            actionBL.merge(tokenIdsArray,newTokenId,tokenTagsArray);

            logger.Log("Token retirement has been successfully finished", LogLevelEnum.INFO, this);

        } catch(Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }


    /**
     * Starting a new transaction
     * Usable only from internal
     *
     * @param  args  Chaincode stub
     * @return       chaincode response
     */
    public Chaincode.Response createTransaction(List<String> args) {

        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));

        String command = args.get(0);

        if (!checkString(command))
            return newErrorResponse(responseError("Invalid argument", ""));
        logger.Log(command, LogLevelEnum.INFO, this );

        try {


            // CREATING MAIN TRANSACTION OBJECT

            // analyzing master data

            JsonObject jsonObject = new JsonParser().parse(command).getAsJsonObject();
            JsonArray actionArray = jsonObject.getAsJsonArray("actions");
            String transactionId = jsonObject.get("transactionId").getAsString();
            Integer sequence =  Integer.parseInt(jsonObject.get("sequence").getAsString());
            List<String> tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("tags"));

            if (actionArray.size() ==  0){
                return newErrorResponse(responseError("Number of actions is zero for the transaction", "500"));
            }

            // creating action id-s
            KeyGenerationHelper keyGen = new KeyGenerationHelper();
            List<String> actionIds = new ArrayList<String>();
            for (int i = 0; i < actionArray.size(); i++){
                String actionId = transactionId + KeyGenerationHelper.actionPrefix + i;
                actionIds.add(actionId);
            }

            TransactionBL transactionBL = new TransactionBL(this.getStub());
            Transaction retTransaction = transactionBL.createTransaction(transactionId,sequence,tags,actionIds);

            // forking an creating actions

            for (int i = 0; i < actionArray.size(); i++) {

                String operatorType = actionArray.get(i).getAsJsonObject().get("type").getAsString();
                logger.Log("Command type : " + operatorType + " command " + actionArray.get(i), LogLevelEnum.INFO, this);

                // issue transaction
                if (operatorType.equals("issue")){

                    this.issueToken(transactionId, actionIds.get(i), actionArray.get(i));

                // transfer transaction
                } else if (operatorType.equals("transfer")){

                    this.transferToken(transactionId, actionIds.get(i), actionArray.get(i));

                } else if (operatorType.equals("retire")) {

                    this.retireToken(transactionId, actionIds.get(i), actionArray.get(i));

                } else if (operatorType.equals("merge")) {

                  this.mergeToken(transactionId, actionIds.get(i), actionArray.get(i));
            }

        }

            logger.Log("Transaction execution has been successfully finished", LogLevelEnum.INFO, this);
           return newSuccessResponse("Transaction execution has been successfully finished, Id: " + transactionId.toString());

        } catch(Throwable e){

            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

    /**
     * Getting a transaction by transaction id
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response getTransaction(List<String> args) {
        // input validation

        // input parameter validation - strict
        // 0 - flavorId

        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", "500"));

        if (!checkString(args.get(0)))
            return newErrorResponse(responseError("Invalid argument", "500"));

        String transactionId = args.get(0);

        try {

            TransactionBL transactionBL = new TransactionBL(this.getStub());
            Transaction retTransacction = transactionBL.getTransaction(transactionId);

            if(retTransacction == null)
                return newErrorResponse(responseError("Nonexistent key", "500"));

            logger.Log("TransactionService getTransaction call succeeded, key : " + retTransacction.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(retTransacction.toJSONString())));

        } catch(Throwable e){

            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }

    }

    /**
     * Querying transactions by a filter string
     * Usable only from internal
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response queryTransaction(List<String> args) {
        return null;
    }


}
