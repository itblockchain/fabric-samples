package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.ActionBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.ActionBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Ancestor class for the issue / retire / transfer / merge services
 */
public  abstract class TransactionServiceBase extends ServiceBase {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(TransactionServiceBase.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Starting an issue token transaction
     * Usable only from internal
     *
     * @param  _transactionId  Chaincode stub
     * @param  _actionId  Chaincode stub*
     * @param  jsonParams  Chaincode stub
     *
     * @return  boolean success response
     */
    protected boolean issueToken(String _transactionId, JsonElement jsonParams, ChaincodeStub _stub, HashMap<String, String> _transactionTags) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "issueToken()");

        String actionId;
        String tokenId;
        String code;
        Double amount;
        String flavorId;
        String destinationAccountId;
        String actionTagsJsonString;
        HashMap<String,String> actionTagsArray;
        String tokenTagsJsonString;
        HashMap<String,String> tokenTagsArray;

        try {
            // analyzing input parameters
            actionId = jsonParams.getAsJsonObject().get("id").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseIssueException("Error at parsing ID in the issue action ",  _ex);
        }

         try {
            tokenId = jsonParams.getAsJsonObject().get("tokenId").getAsString();
         } catch(Exception _ex){
             throw new TransactionServiceBase.ParseIssueException("Error at parsing token ID in the issue action ",  _ex);
         }

         try {
            code = jsonParams.getAsJsonObject().get("tokenCode").getAsString();
         } catch(Exception _ex){
             throw new TransactionServiceBase.ParseIssueException("Error at parsing token code in the issue action ",  _ex);
         }

          try {
            amount = Double.parseDouble(jsonParams.getAsJsonObject().get("amount").getAsString());
          } catch(Exception _ex){
              throw new TransactionServiceBase.ParseIssueException("Error at parsing amount in the issue action ",  _ex);
          }

          try {
               flavorId = jsonParams.getAsJsonObject().get("flavorId").getAsString();
          } catch(Exception _ex){
              throw new TransactionServiceBase.ParseIssueException("Error at parsing flavor id in the issue action ",  _ex);
          }

          try {
              destinationAccountId = jsonParams.getAsJsonObject().get("destinationAccountId").getAsString();
          } catch(Exception _ex){
              throw new TransactionServiceBase.ParseIssueException("Error at parsing destination account id in the issue action ",  _ex);
          }

        try {
            actionTagsJsonString = jsonParams.getAsJsonObject().get("actionTags").toString();
            actionTagsArray = JSONHelper.createHashMapFromString(actionTagsJsonString);
         } catch(Exception _ex){
             throw new TransactionServiceBase.ParseIssueException("Error at parsing destination action tags in the issue action ",  _ex);
         }

        try {
            tokenTagsJsonString = jsonParams.getAsJsonObject().get("tokenTags").toString();
            tokenTagsArray = JSONHelper.createHashMapFromString(tokenTagsJsonString);
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseIssueException("Error at parsing token tags at issue action ",  _ex);
        }

        // calling issue action
        ActionBL actionBL = new ActionBL(_stub);
        actionBL.issue(_transactionId, actionId,tokenId, code, amount,flavorId,destinationAccountId, actionTagsArray, tokenTagsArray, _transactionTags);

        logger.exiting(this.getClass().getSimpleName(), "issueToken()");

        return true;
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
    protected boolean transferToken(String _transactionId, JsonElement jsonParams, ChaincodeStub _stub,HashMap<String, String> _transactionTags) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "transferToken()");

        String actionId;
        Double amount;
        String tokenId;
        String newTokenId;
        String destinationAccountId;
        String actionTagsJsonString;
        HashMap<String,String> actionTagsArray;
        String tokenTagsJsonString;
        HashMap<String,String> tokenTagsArray;

        try {
            // analyzing input parameters
            actionId = jsonParams.getAsJsonObject().get("id").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseTransferException("Error at parsing action id in transfer action ",  _ex);
        }

        try {
            amount = Double.parseDouble(jsonParams.getAsJsonObject().get("amount").getAsString());
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseTransferException("Error at parsing amount in transfer action ",  _ex);
        }

        try {
            tokenId = jsonParams.getAsJsonObject().get("tokenId").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseTransferException("Error at parsing token id in transfer action ",  _ex);
        }

        try {
            newTokenId = jsonParams.getAsJsonObject().get("newTokenId").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseTransferException("Error at parsing new token id in transfer action ",  _ex);
        }

        try {
            destinationAccountId = jsonParams.getAsJsonObject().get("destinationAccountId").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseTransferException("Error at parsing destination account id in transfer action ",  _ex);
        }

        try {
            actionTagsJsonString = jsonParams.getAsJsonObject().get("actionTags").toString();
            actionTagsArray = JSONHelper.createHashMapFromString(actionTagsJsonString);
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseTransferException("Error at parsing action tags in transfer action ",  _ex);
        }

        try {
            tokenTagsJsonString = jsonParams.getAsJsonObject().get("tokenTags").toString();
            tokenTagsArray = JSONHelper.createHashMapFromString(tokenTagsJsonString);
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseTransferException("Error at parsing token tags in transfer action ",  _ex);
        }

        // calling transfer action
        ActionBLInterface actionBL = new ActionBL(_stub);
        actionBL.transfer(_transactionId,actionId,tokenId, newTokenId, amount, destinationAccountId, actionTagsArray, tokenTagsArray, _transactionTags);

        logger.exiting(this.getClass().getSimpleName(), "transferToken()");

        return true;
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
    protected boolean retireToken(String _transactionId, JsonElement jsonParams, ChaincodeStub _stub, HashMap<String, String> _transactionTags) throws BalanceTrackerException
    {

        logger.entering(this.getClass().getSimpleName(), "retireToken()");

        String actionId;
        Double amount;
        String tokenId;
        String actionTagsString;
        HashMap<String,String> actionTagsArray;

        try {
            // analyzing input parameters
            actionId = jsonParams.getAsJsonObject().get("id").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseRetireException("Error action id at parsing retire action ",_ex);
        }

        try {
            amount = Double.parseDouble(jsonParams.getAsJsonObject().get("amount").getAsString());
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseRetireException("Error amount at parsing retire action ",_ex);
        }

        try {
            tokenId = jsonParams.getAsJsonObject().get("tokenId").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseRetireException("Error token id at parsing retire action ",_ex);
        }

        try {

            actionTagsString = jsonParams.getAsJsonObject().get("actionTags").toString();
            actionTagsArray = JSONHelper.createHashMapFromString(actionTagsString);

        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseRetireException("Error action tags at parsing retire action ",_ex);
        }

        // calling retire action
        ActionBLInterface actionBL = new ActionBL(_stub);
        actionBL.retire(_transactionId,actionId,tokenId,  amount, actionTagsArray, _transactionTags);

        logger.exiting(this.getClass().getSimpleName(), "retireToken()");

        return true;

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
    protected boolean mergeToken(String _transactionId, JsonElement jsonParams, ChaincodeStub _stub) throws BalanceTrackerException
    {
        logger.entering(this.getClass().getSimpleName(), "mergeToken()");

        String actionId;
        JsonArray tokenIdsJsonArray;
        List<String> tokenIdsArray;
        String newTokenId;
        String tokenTagsString;
        HashMap<String,String> tokenTagsArray;

        try {
            // analyzing input parameters
            actionId = jsonParams.getAsJsonObject().get("id").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseMergeException("Error id at parsing merge action ", _ex);
        }

        try {
            tokenIdsJsonArray = jsonParams.getAsJsonObject().get("tokenIds").getAsJsonArray();
            tokenIdsArray = JSONHelper.convertJSONArrayToArray(tokenIdsJsonArray);
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseMergeException("Error token ids at parsing merge action ", _ex);
        }

        try {
            newTokenId = jsonParams.getAsJsonObject().get("newTokenId").getAsString();
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseMergeException("Error newTokenId at parsing merge action ", _ex);
        }

        try {
            tokenTagsString = jsonParams.getAsJsonObject().get("tokenTags").toString();
            tokenTagsArray = JSONHelper.createHashMapFromString(tokenTagsString);
        } catch(Exception _ex){
            throw new TransactionServiceBase.ParseMergeException("Error token tags at parsing merge action ", _ex);
        }

        // calling merge action
        ActionBLInterface actionBL = new ActionBL(_stub);
        actionBL.merge(tokenIdsArray,newTokenId,tokenTagsArray);

        logger.exiting(this.getClass().getSimpleName(), "mergeToken()");

        return true;
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * error at issue transaction
     */
    public static class ParseIssueException extends InputParameterInvalidException
    {
        protected Integer code = 5089;

        public ParseIssueException(String _msg, Exception _cause) {
            super(_msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }
    /**
     * error at transfer transaction
     */
    public static class ParseTransferException extends InputParameterInvalidException
    {
        protected Integer code = 5089;

        public ParseTransferException(String _msg, Exception _cause) {
            super(_msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


    /**
     * error at retire transaction
     */
    public static class ParseRetireException extends InputParameterInvalidException
    {
        protected Integer code = 5089;

        public ParseRetireException(String _msg, Exception _cause) {
            super(_msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * error at merge transaction
     */
    public static class ParseMergeException extends InputParameterInvalidException
    {
        protected Integer code = 5089;

        public ParseMergeException(String _msg, Exception _cause) {
            super(_msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}
