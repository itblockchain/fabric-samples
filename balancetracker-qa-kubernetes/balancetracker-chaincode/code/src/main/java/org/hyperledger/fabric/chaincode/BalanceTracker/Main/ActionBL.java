package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Business logic for actions
 */
public class ActionBL extends BusinessLogicBase implements ActionBLInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(ActionBL.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Constrcutor
     */
    public ActionBL(ChaincodeStub _stub) throws ActionBL.StubIsNullException {
        if (_stub == null)
            throw new ActionBL.StubIsNullException("Stub can not be null at initializing the action business logic");
        this.stub = _stub;
    }

    /**
     * Getting an action by account id
     * only internally
     *
     * @param  actionId arguments of the call
     * @return       chaincode response
     */
    public Action getAction(String actionId) throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "getAction()");
        logger.finer("accountId : " + actionId);
        logger.finer("transaction id : " + this.getStub().getTxId());


        String actionString = this.getStringState(actionId, ACTION);

        // not found id a normal use case
        if (actionString == null)
           return null;

        if(!checkString(actionString))
            return null;

         Action action = Action.createAction(actionString);

        logger.exiting(this.getClass().getSimpleName(), "getAction()");
        logger.finer("return : " + action.toJSONString());

        return action;
    }


    /**
     * Creating action by filter
     *
     * @param  _actionId arguments of the call
     * @param  _type arguments of the call
     * @param  _amount arguments of the call
     * @param  _timestamp arguments of the call
     * @param  _transactionId arguments of the call
     * @param  _flavorId arguments of the call
     * @param  _sourceAccountId arguments of the call
     * @param  _destinationAccountId arguments of the call
     * @param  _snapshotId arguments of the call
     * @param  _params arguments of the call
     * @param  _tags arguments of the call
     *
     * @return       chaincode response
     */
    public Action createAction(String _actionId, ActionTypeEnum _type, Double _amount, String _transactionId, String _flavorId, String _sourceAccountId, String _destinationAccountId, String _snapshotId, List<String> _params, HashMap<String,String> _tags) throws BalanceTrackerException
    {
        logger.entering(this.getClass().getSimpleName(), "createAction()");
        logger.finer("Parameters : accountId : " + _actionId +
                "\n type : " + _type.toString() +
                "\n amount : " + _amount.toString() +
                "\n transactionId : " + _transactionId +
                "\n flavorId : " + _flavorId +
                "\n sourceAccountId : " + _sourceAccountId +
                "\n snapshotId : " + _snapshotId);

            // Business logic input error handling

            if (this.getAction(_actionId)!= null){
                throw new ActionBL.IdTakenException("Cannot create Action, Action Id is already taken");
            }

            if (_amount <= 0){
                throw new ActionBL.ParameterIsNullException("Cannot create Action, amount must be bigger than zero");
            }

            // CHECKING REFERENCES

            // FlavorId error handling
            if ((_flavorId == null) || (_flavorId.length() == 0)){
                throw new ActionBL.ParameterIsNullException("Cannot create Action, FlavorId is null");
            }

            FlavorBL flavorBL = new FlavorBL(this.getStub());
            if (flavorBL.getFlavor(_flavorId) == null){
                throw new ActionBL.NotExistException("Cannot create Action, referred FlavorId does not exist");
            }

            // Snapshot Id error handling
            if ((_snapshotId == null) || (_snapshotId.length() == 0)){
                throw new ActionBL.NotExistException("Cannot create Action, _snapshotId is null");
            }

//            SnapshotBL snapshotBL = new SnapshotBL(this.getStub());
//            if (snapshotBL.getSnapshot(_snapshotId) == null){
//                throw new BalanceTrackerException("Cannot create Action, referred _snapshotId does not exist", 500);
//            }

            // source and destination account checking
            AccountBL accountBL = new AccountBL(this.getStub());

            // checking destination account
            if(_type.equals(ActionTypeEnum.ISSUE) || _type.equals(ActionTypeEnum.TRANSFER)){

                if ((_destinationAccountId == null) || (_destinationAccountId.length() == 0)){
                    throw new ActionBL.ParameterIsNullException("Cannot create Action, DestinationAccount Id is null");
                }

                if (accountBL.getAccount(_destinationAccountId) == null){
                    throw new ActionBL.NotExistException("Cannot create Action, referred _destinationAccountId does not exist");
                }
            }

            // checking source account
            if(_type.equals(ActionTypeEnum.RETIRE) || _type.equals(ActionTypeEnum.TRANSFER)){

                if ((_sourceAccountId == null) || (_sourceAccountId.length() == 0)){
                    throw new ActionBL.ParameterIsNullException("Cannot create Action, Source AccountId Id is null");
                }

                if (accountBL.getAccount(_sourceAccountId) == null){
                    throw new ActionBL.NotExistException("Cannot create Action, referred _sourceAccountId does not exist");
                }
            }

            // checking referred transaction
            if ((_transactionId == null) || (_transactionId.length() == 0)){
                throw new ActionBL.ParameterIsNullException("Cannot create Action, _transactionId is null");
            }

            // transaction id still not exist at the time of the action creation
//            TransactionBLBL transactionBL = new TransactionBLBL(this.getStub());
//            if (transactionBL.getTransaction(_transactionId) == null){
//                throw new BalanceTrackerException("Cannot create Action, referred _transactionId does not exist", 500);
//            }

            // create new Action and write it into the blockchain
            Instant issuedAtI = this.getStub().getTxTimestamp();
            long _timestamp = issuedAtI.getEpochSecond();

            Action actionToStore = new Action(_actionId,_type,_amount,_timestamp,_transactionId,_flavorId,_sourceAccountId,_destinationAccountId,_snapshotId,_params,_tags);

            try {
                this.putState(_actionId,(new ObjectMapper()).writeValueAsBytes(actionToStore), ACTION );
                this.getStub().setEvent(Constants.EventNames.ACTION_CREATED,(new ObjectMapper()).writeValueAsBytes(actionToStore));

            } catch (
                JsonProcessingException _ex){
                throw new Action.SerializationException("Error at writing Action into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "createAction()");
        logger.finer("return : " + actionToStore.toJSONString());

        return actionToStore;
     }

    /**
     * Issuing a new token, by
     * 1. Collect flavor tags
     * 2. Create snapshot with tags
     * 3. Create action with snapshot reference
     * 4. Create token
     *
     * PROTOTYPE IMPLEMENTATION: SIGNATURES ARE NOT CONSIDERED
     *
     * @param  _transactionId arguments of the call
     * @param  _amount arguments of the call
     * @param  _flavorId arguments of the call
     * @param  _destinationAccountId arguments of the call
     * @param  _actionTags arguments of the call
     * @param  _tokenTags arguments of the call*
     * @return       chaincode response
     */
     public Token issue(String _transactionId, String _actionId, String _tokenId, String _code, Double _amount, String _flavorId, String _destinationAccountId, HashMap<String,String> _actionTags, HashMap<String,String> _tokenTags,HashMap<String,String> _transactionTags ) throws BalanceTrackerException {

         logger.entering(this.getClass().getSimpleName(), "issue()");
         logger.finer("Parameters : transactionId" + _transactionId +
                 "\n accountId : " + _actionId +
                 "\n amount : " + _amount.toString() +
                 "\n code : " + _code +
                 "\n flavorId : " + _flavorId +
                 "\n destinationAccountId : " + _destinationAccountId);

         // FlavorId error handling
            if ((_flavorId == null) || (_flavorId.length() == 0)){
                throw new  ActionBL.ParameterIsNullException("Cannot create Action, FlavorId is null");
            }

            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flv = flavorBL.getFlavor(_flavorId);

            if (flv == null){
                throw new ActionBL.NotExistException("Cannot issue token, referred FlavorId does not exist");
            }

            // check if nonfungible token can be issued only amount 1
            if ((flv.isFungible() == false) && (_amount != 1)){
                throw new ActionBL.ParameterNotCompatibleException("Cannot issue token, from non-fungible token, only 1 can be issued");
            }

            // getting flavor tags
            HashMap<String,String> flavorTags = flv.getTags();

            // creating snapshot with tags
            SnapshotBL snapshotBL = new SnapshotBL(this.getStub());
            String snapshotId =  KeyGenerationHelper.getSnapshotIdFromActionId(_actionId);
            Snapshot snapshot = snapshotBL.createSnapshot(snapshotId,_actionTags, flavorTags, _transactionTags);

            // create action - ISSUE
            Instant issuedAtI = this.getStub().getTxTimestamp();
            long unixTimestamp = issuedAtI.getEpochSecond();

            Action newAction =  this.createAction(_actionId, ActionTypeEnum.ISSUE,_amount,_transactionId,flv.getFlavorId(),"", _destinationAccountId, snapshot.getSnapshotId(), new ArrayList<String>(), _actionTags);

            // create token
            // PROTOTYPE IMPLEMENTATION: token is created without signature validation
            TokenBL tokenBL = new TokenBL(this.getStub());
            Token token = tokenBL.createToken(_tokenId, _code, _amount, _flavorId, _destinationAccountId, _tokenTags);

         logger.exiting(this.getClass().getSimpleName(), "issue()");
         logger.finer("return : " + token.toJSONString());

         return token;
    }


    /**
     * Retire a new token with creating actions, by
     * 1. Collect flavor tags
     * 2. Create snapshot with tags
     * 3. Create action with snapshot reference
     * 4. Retire token
     *
     * PROTOTYPE IMPLEMENTATION: SIGNATURES ARE NOT CONSIDERED
     *
     * @param  _transactionId arguments of the call
     * @param  _amount arguments of the call
     * @param  _actionTags arguments of the call
     * @return       chaincode response
     */
    public Token retire(String _transactionId, String _actionId, String _tokenId, Double _amount, HashMap<String,String> _actionTags, HashMap<String,String> _transactionTags) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "retire()");
        logger.finer("Parameters : transactionId" + _transactionId +
                "\n accountId : " + _actionId +
                "\n amount : " + _amount.toString() +
                "\n tokenId : " + _tokenId);

        // getting token

            if ((_tokenId == null) || (_tokenId.length() == 0)){
                throw new ActionBL.ParameterIsNullException("Cannot create retire Action: TokenId is null");
            }


            TokenBL tokenBL = new TokenBL(this.getStub());
            Token tkn = tokenBL.getToken(_tokenId);

            if (tkn == null){
                throw new  ActionBL.NotExistException("Cannot create retire Action: Non existent token id");
            }


            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flv = flavorBL.getFlavor(tkn.getFlavorId());

            if (flv == null){
                throw new  ActionBL.NotExistException("Cannot retire token, referred FlavorId does not exist");
            }

            AccountBL accountBL = new AccountBL(this.getStub());
            Account account = accountBL.getAccount(tkn.getAccountId());

            if (account == null){
                throw new  ActionBL.NotExistException("Cannot retire token, referred AccountId does not exist");
            }

            // getting tags
            HashMap<String,String> flavorTags = flv.getTags();
            HashMap<String,String> accountTags = account.getTags();

            // creating snapshot with tags
            SnapshotBL snapshotBL = new SnapshotBL(this.getStub());
            String snapshotId =  KeyGenerationHelper.getSnapshotIdFromActionId(_actionId);
            Snapshot snapshot = snapshotBL.createSnapshot(snapshotId,_actionTags, flavorTags, _transactionTags);

            // create action - ISSUE
            int unixTimestamp = (int) (System.currentTimeMillis() / 1000L);
            Action newAction =  this.createAction(_actionId, ActionTypeEnum.RETIRE,_amount,_transactionId,flv.getFlavorId(),tkn.getAccountId(), "", snapshot.getSnapshotId(), new ArrayList<String>(), _actionTags);

            // retire token
            // PROTOTYPE IMPLEMENTATION: token is retired without signature validation
            Token token = tokenBL.retireToken(_tokenId, _amount);

        logger.exiting(this.getClass().getSimpleName(), "retire()");
        logger.finer("return : " + token.toJSONString());

        return token;
    }


    /**
     * Transferring a new token, by
     * 1. Collect flavor tags
     * 2. Create snapshot with tags
     * 3. Create action with snapshot reference
     * 4. Create token
     * 5. Retire token
     *
     * PROTOTYPE IMPLEMENTATION: SIGNATURES ARE NOT CONSIDERED
     *
     * @param  _transactionId arguments of the call
     * @param  _tokenId arguments of the call
     * @param  _amount arguments of the call*
     * @param  _destinationAccountId arguments of the call
     * @param  _destinationAccountId arguments of the call
     * @param  _actionTags arguments of the call
     * @param  _tokenTags arguments of the call*
     * @return       chaincode response
     */
    public Token transfer(String _transactionId, String _actionId, String _tokenId, String _newTokenId, Double _amount, String _destinationAccountId, HashMap<String,String> _actionTags, HashMap<String,String> _tokenTags, HashMap<String,String> _transactionTags) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "transfer()");
        logger.finer("Parameters : transactionId" + _transactionId +
                "\n accountId : " + _actionId +
                "\n amount : " + _amount.toString() +
                "\n tokenId : " + _tokenId +
                "\n newTokenId : " + _newTokenId +
                "\n destinationAccountId : " + _destinationAccountId);

        // getting the token

            if ((_tokenId == null) || (_tokenId.length() == 0)){
                throw new ActionBL.ParameterIsNullException("Cannot create transfer Action: TokenId is null");
            }

            TokenBL tokenBL = new TokenBL(this.getStub());
            Token tkn = tokenBL.getToken(_tokenId);

            if (tkn == null){
                throw new ActionBL.NotExistException("Cannot create transfer Action: Non existent token id");
            }

            // checking destination account
            AccountBL accountBL = new AccountBL(this.getStub());
            Account account = accountBL.getAccount(_destinationAccountId);

            if (account == null){
                throw new ActionBL.NotExistException("Cannot transfer token, referred destination Account Id does not exist");
            }

            // getting and checking the flavor - for tokenId and for tags
            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flv = flavorBL.getFlavor(tkn.getFlavorId());

            if (flv == null){
                throw new ActionBL.NotExistException("Cannot transfer token, referred FlavorId does not exist");
            }

            // check if nonfungible token can be issued only amount 1
            if ((flv.isFungible() == false) && (_amount != 1)){
                throw new ActionBL.ParameterNotCompatibleException("Cannot transfer token, from non-fungible token, only 1 can be issued");
            }

            // getting tags
            HashMap<String,String> flavorTags = flv.getTags();
            HashMap<String,String> accountTags = account.getTags();

            // creating snapshot with tags
            SnapshotBL snapshotBL = new SnapshotBL(this.getStub());
            String snapshotId =  KeyGenerationHelper.getSnapshotIdFromActionId(_actionId);
            Snapshot snapshot = snapshotBL.createSnapshot(snapshotId,_actionTags, flavorTags, _transactionTags);

            // create action - ISSUE
            int unixTimestamp = (int) (System.currentTimeMillis() / 1000L);
            Action newAction =  this.createAction(_actionId, ActionTypeEnum.TRANSFER,_amount,_transactionId,flv.getFlavorId(),tkn.getAccountId(),_destinationAccountId, snapshot.getSnapshotId(), new ArrayList<String>(), _actionTags);

            // retire token
            // PROTOTYPE IMPLEMENTATION: token is retired without signature validation
            tokenBL.retireToken(_tokenId, _amount);

            // create token
            // PROTOTYPE IMPLEMENTATION: token is created without signature validation
            Token token = tokenBL.createToken(_newTokenId, tkn.getTokenCode(), _amount, tkn.getFlavorId(), _destinationAccountId, _tokenTags);

        logger.entering(this.getClass().getSimpleName(), "transfer()");
        logger.finer("return : " + token.toJSONString());

        return token;
    }


    /**
     * Merging tokens on the same account, by
     * 1. Check if all tokens are on the same account
     * 2. Check if all tokens are non-fungible
     * 3. Retire all tokens
     * 4. Create new token with the sum amount
     *
     * PROTOTYPE IMPLEMENTATION: SIGNATURES ARE NOT CONSIDERED
     *
     * @param  _tokenIds arguments of the call
     * @param  _newTokenId arguments of the call
     * @param  _tokenTags arguments of the call
     * @return       chaincode response
     */
    public Token merge(List<String> _tokenIds, String _newTokenId, HashMap<String,String> _tokenTags) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "merge()");
        logger.finer( "Parameters : newTokenId" + _newTokenId);

        if (_tokenIds.size() < 1) {
                throw new ActionBL.ParametersIncorrectNumberException("Cannot do merge: zero number of tokenids");
            }

            String accountId = "";
            Double amount = 0.0;
            String flavorId = "";

            TokenBL tokenBL = new TokenBL(this.getStub());

            for (String _tokenId : _tokenIds) {

                // getting the token

                if ((_tokenId == null) || (_tokenId.length() == 0)) {
                    throw new ActionBL.ParameterIsNullException("Cannot do merge: referred TokenId is null");
                }


                Token tkn = tokenBL.getToken(_tokenId);

                if (tkn == null) {
                    throw new ActionBL.NotExistException("Cannot do merge: referred token id does not exist");
                }


                // getting and checking the flavor - for tokenId and for tags
                FlavorBL flavorBL = new FlavorBL(this.getStub());
                Flavor flv = flavorBL.getFlavor(tkn.getFlavorId());

                if (flv == null){
                    throw new ActionBL.NotExistException("Cannot do merge , referred FlavorId does not exist");
                }

                // check if nonfungible token can be issued only amount 1
                if ((flv.isFungible() == false)){
                    throw new ActionBL.ParameterNotCompatibleException("Cannot do merge, non-fungible token");
                }

                // check if flavorId always the same
                if ((!flavorId.equals("") && (!flv.getFlavorId().equals(flavorId)))){
                    throw new ActionBL.ParameterNotCompatibleException("Cannot merge, tokens does not refer to the same flavor");
                }

                // checking destination account
                AccountBL accountBL = new AccountBL(this.getStub());
                Account account = accountBL.getAccount(tkn.getAccountId());

                if (account == null){
                    throw new ActionBL.NotExistException("Cannot merge, referred  Account Id does not exist");
                }

                if ((!accountId.equals("") && (!accountId.equals(account.getAccountId())))){
                    throw new ActionBL.NotExistException("Cannot merge, tokens does not refer to the same account");
                }

                amount += tkn.getAmount();
                accountId = account.getAccountId();
                flavorId = flv.getFlavorId();

                // retire each token
                Token token = tokenBL.retireToken(_tokenId, tkn.getAmount());

            }

            // create new token : only non-fungible: code is always zero
            Token token = tokenBL.createToken(_newTokenId, "", amount, flavorId, accountId, _tokenTags);

        logger.exiting(this.getClass().getSimpleName(), "merge()");

        return token;
    }



    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5012;

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
    public static class ParameterIsNullException extends InputParameterInvalidException
    {
        protected Integer code = 5022;

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
        protected Integer code = 5032;

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
        protected Integer code = 5042;

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
        protected Integer code = 5092;

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
        protected Integer code = 5082;

        public IdTakenException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

}
