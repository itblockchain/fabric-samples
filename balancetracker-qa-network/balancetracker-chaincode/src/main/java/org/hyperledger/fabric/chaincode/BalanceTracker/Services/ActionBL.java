package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

import java.time.Instant;
import java.time.Instant.*;

import java.util.ArrayList;
import java.util.List;

public class ActionBL extends BusinessLogicBase {

    public ActionBL(ChaincodeStub _stub){
        this.stub = _stub;
    }


    /**
     * Getting an action by account id
     * only internally
     *
     * @param  actionId arguments of the call
     * @return       chaincode response
     */
    Action getAction(String actionId) throws BalanceTrackerException  {

        try {

            String actionString = this.getStub().getStringState(actionId);

            if (actionString == null)
                return null;

            if(!checkString(actionString))
                return null;

            Action action = Action.createAction(actionString);

            return action;

        }
        catch (Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * Querying actions by filter
     *
     * Implemented on client side for release1
     *
     * @param  filter arguments of the call
     * @return       chaincode response
     */
    Action queryAction(QueryFilter filter) throws BalanceTrackerException {
        throw new BalanceTrackerException("Filter is implemented on the client side for Release1");
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
    Action createAction(String _actionId, ActionTypeEnum _type, Integer _amount, Integer _timestamp, String _transactionId, String _flavorId, String _sourceAccountId, String _destinationAccountId, String _snapshotId, List<String> _params, List<String> _tags) throws BalanceTrackerException
    {

        try {

            // Business logic error handling

            if (this.getAction(_actionId)!= null){
                throw new BalanceTrackerException("Cannot create Action, Action Id is already taken", 500);
            }

            if (_amount <= 0){
                throw new BalanceTrackerException("Cannot create Action, amount must be bigger than zero", 500);
            }

            if (_timestamp <= 0){
                throw new BalanceTrackerException("Cannot create Action, timestamp must be a linux timestamp", 500);
            }

            // CHECKING REFERENCES

            // FlavorId error handling
            if ((_flavorId == null) || (_flavorId.length() == 0)){
                throw new BalanceTrackerException("Cannot create Action, FlavorId is null", 500);
            }

            FlavorBL flavorBL = new FlavorBL(this.getStub());
            if (flavorBL.getFlavor(_flavorId) == null){
                throw new BalanceTrackerException("Cannot create Action, referred FlavorId does not exist", 500);
            }

            // Snapshot Id error handling
            if ((_snapshotId == null) || (_snapshotId.length() == 0)){
                throw new BalanceTrackerException("Cannot create Action, _snapshotId is null", 500);
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
                    throw new BalanceTrackerException("Cannot create Action, DestinationAccount Id is null", 500);
                }

                if (accountBL.getAccount(_destinationAccountId) == null){
                    throw new BalanceTrackerException("Cannot create Action, referred _destinationAccountId does not exist", 500);
                }
            }

            // checking source account
            if(_type.equals(ActionTypeEnum.RETIRE) || _type.equals(ActionTypeEnum.TRANSFER)){

                if ((_sourceAccountId == null) || (_sourceAccountId.length() == 0)){
                    throw new BalanceTrackerException("Cannot create Action, Source AccountId Id is null", 500);
                }

                if (accountBL.getAccount(_sourceAccountId) == null){
                    throw new BalanceTrackerException("Cannot create Action, referred _sourceAccountId does not exist", 500);
                }
            }

            // checking referred transaction
            if ((_transactionId == null) || (_transactionId.length() == 0)){
                throw new BalanceTrackerException("Cannot create Action, _transactionId is null", 500);
            }

            // transaction id still not exist at the time of the action creation
//            TransactionBL transactionBL = new TransactionBL(this.getStub());
//            if (transactionBL.getTransaction(_transactionId) == null){
//                throw new BalanceTrackerException("Cannot create Action, referred _transactionId does not exist", 500);
//            }

            // create new Action and write it into the blockchain

            Action actionToStore = new Action(_actionId,_type,_amount,_timestamp,_transactionId,_flavorId,_sourceAccountId,_destinationAccountId,_snapshotId,_params,_tags);

            this.getStub().putState(_actionId, (new ObjectMapper()).writeValueAsBytes(actionToStore));

            return actionToStore;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
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
     Token issue(String _transactionId, String _actionId, String _tokenId, String _code, Integer _amount, String _flavorId, String _destinationAccountId, List<String> _actionTags, List<String> _tokenTags) throws BalanceTrackerException {

        try {

            // FlavorId error handling
            if ((_flavorId == null) || (_flavorId.length() == 0)){
                throw new BalanceTrackerException("Cannot create Action, FlavorId is null", 500);
            }

            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flv = flavorBL.getFlavor(_flavorId);

            if (flv == null){
                throw new BalanceTrackerException("Cannot issue token, referred FlavorId does not exist", 500);
            }

            // check if nonfungible token can be issued only amount 1
            if ((flv.isFungible() == false) && (_amount != 1)){
                throw new BalanceTrackerException("Cannot issue token, from non-fungible token, only 1 can be issued", 500);
            }

            // getting flavor tags
            List<String> flavorTags = flv.getTags();

            // creating snapshot with tags
            SnapshotBL snapshotBL = new SnapshotBL(this.getStub());
            String snapshotId =  KeyGenerationHelper.getSnapshotIdFromActionId(_actionId);
            Snapshot snapshot = snapshotBL.createSnapshot(snapshotId,_actionTags, flavorTags, _tokenTags);

            // create action - ISSUE
            int unixTimestamp = (int) (System.currentTimeMillis() / 1000L);
            Action newAction =  this.createAction(_actionId, ActionTypeEnum.ISSUE,_amount, unixTimestamp,_transactionId,flv.getFlavorId(),"", _destinationAccountId, snapshot.getSnapshotId(), new ArrayList<String>(), _actionTags);

            // create token
            // PROTOTYPE IMPLEMENTATION: token is created without signature validation
            TokenBL tokenBL = new TokenBL(this.getStub());
            Token token = tokenBL.createToken(_tokenId, _code, _amount, _flavorId, _destinationAccountId, _tokenTags);

            return token;
        }
        catch (Exception ex){
        throw new BalanceTrackerException(ex);
        }
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
    Token retire(String _transactionId, String _actionId, String _tokenId, Integer _amount, List<String> _actionTags) throws BalanceTrackerException {

        try {

            // getting token

            if ((_tokenId == null) || (_tokenId.length() == 0)){
                throw new BalanceTrackerException("Cannot create retire Action: TokenId is null", 500);
            }


            TokenBL tokenBL = new TokenBL(this.getStub());
            Token tkn = tokenBL.getToken(_tokenId);

            if (tkn == null){
                throw new BalanceTrackerException("Cannot create retire Action: Non existent token id", 500);
            }


            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flv = flavorBL.getFlavor(tkn.getFlavorId());

            if (flv == null){
                throw new BalanceTrackerException("Cannot retire token, referred FlavorId does not exist", 500);
            }

            AccountBL accountBL = new AccountBL(this.getStub());
            Account account = accountBL.getAccount(tkn.getAccountId());

            if (account == null){
                throw new BalanceTrackerException("Cannot retire token, referred AccountId does not exist", 500);
            }

            // getting tags
            List<String> flavorTags = flv.getTags();
            List<String> accountTags = account.getTags();

            // creating snapshot with tags
            SnapshotBL snapshotBL = new SnapshotBL(this.getStub());
            String snapshotId =  KeyGenerationHelper.getSnapshotIdFromActionId(_actionId);
            Snapshot snapshot = snapshotBL.createSnapshot(snapshotId,_actionTags, flavorTags, accountTags);

            // create action - ISSUE
            int unixTimestamp = (int) (System.currentTimeMillis() / 1000L);
            Action newAction =  this.createAction(_actionId, ActionTypeEnum.RETIRE,_amount, unixTimestamp,_transactionId,flv.getFlavorId(),tkn.getAccountId(), "", snapshot.getSnapshotId(), new ArrayList<String>(), _actionTags);

            // retire token
            // PROTOTYPE IMPLEMENTATION: token is retired without signature validation
            Token token = tokenBL.retireToken(_tokenId, _amount);

            return token;
        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
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
    Token transfer(String _transactionId, String _actionId, String _tokenId, String _newTokenId, Integer _amount, String _destinationAccountId, List<String> _actionTags, List<String> _tokenTags) throws BalanceTrackerException {

        try {

            // getting the token

            if ((_tokenId == null) || (_tokenId.length() == 0)){
                throw new BalanceTrackerException("Cannot create transfer Action: TokenId is null", 500);
            }


            TokenBL tokenBL = new TokenBL(this.getStub());
            Token tkn = tokenBL.getToken(_tokenId);

            if (tkn == null){
                throw new BalanceTrackerException("Cannot create transfer Action: Non existent token id", 500);
            }


            // checking destination account
            AccountBL accountBL = new AccountBL(this.getStub());
            Account account = accountBL.getAccount(_destinationAccountId);

            if (account == null){
                throw new BalanceTrackerException("Cannot transfer token, referred destination Account Id does not exist", 500);
            }

            // getting and checking the flavor - for tokenId and for tags
            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flv = flavorBL.getFlavor(tkn.getFlavorId());

            if (flv == null){
                throw new BalanceTrackerException("Cannot transfer token, referred FlavorId does not exist", 500);
            }

            // check if nonfungible token can be issued only amount 1
            if ((flv.isFungible() == false) && (_amount != 1)){
                throw new BalanceTrackerException("Cannot transfer token, from non-fungible token, only 1 can be issued", 500);
            }

            // getting tags
            List<String> flavorTags = flv.getTags();
            List<String> accountTags = account.getTags();

            // creating snapshot with tags
            SnapshotBL snapshotBL = new SnapshotBL(this.getStub());
            String snapshotId =  KeyGenerationHelper.getSnapshotIdFromActionId(_actionId);
            Snapshot snapshot = snapshotBL.createSnapshot(snapshotId,_actionTags, flavorTags, accountTags);

            // create action - ISSUE
            int unixTimestamp = (int) (System.currentTimeMillis() / 1000L);
            Action newAction =  this.createAction(_actionId, ActionTypeEnum.TRANSFER,_amount, unixTimestamp,_transactionId,flv.getFlavorId(),tkn.getAccountId(),_destinationAccountId, snapshot.getSnapshotId(), new ArrayList<String>(), _actionTags);

            // retire token
            // PROTOTYPE IMPLEMENTATION: token is retired without signature validation
            tokenBL.retireToken(_tokenId, _amount);

            // create token
            // PROTOTYPE IMPLEMENTATION: token is created without signature validation
            Token token = tokenBL.createToken(_newTokenId, tkn.getTokenCode(), _amount, tkn.getFlavorId(), _destinationAccountId, _tokenTags);

            return token;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
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
    Token merge(List<String> _tokenIds, String _newTokenId, List<String> _tokenTags) throws BalanceTrackerException {

        try {

            if (_tokenIds.size() < 1) {
                throw new BalanceTrackerException("Cannot do merge: zero number of tokenids", 500);
            }

            String accountId = "";
            Integer amount = 0;
            String flavorId = "";

            TokenBL tokenBL = new TokenBL(this.getStub());

            for (String _tokenId : _tokenIds) {

                // getting the token

                if ((_tokenId == null) || (_tokenId.length() == 0)) {
                    throw new BalanceTrackerException("Cannot do merge: referred TokenId is null", 500);
                }


                Token tkn = tokenBL.getToken(_tokenId);

                if (tkn == null) {
                    throw new BalanceTrackerException("Cannot do merge: referred token id does not exist", 500);
                }


                // getting and checking the flavor - for tokenId and for tags
                FlavorBL flavorBL = new FlavorBL(this.getStub());
                Flavor flv = flavorBL.getFlavor(tkn.getFlavorId());

                if (flv == null){
                    throw new BalanceTrackerException("Cannot do merge , referred FlavorId does not exist", 500);
                }

                // check if nonfungible token can be issued only amount 1
                if ((flv.isFungible() == false)){
                    throw new BalanceTrackerException("Cannot do merge, non-fungible token", 500);
                }

                // check if flavorId always the same
                if ((!flavorId.equals("") && (!flv.getFlavorId().equals(flavorId)))){
                    throw new BalanceTrackerException("Cannot merge, tokens does not refer to the same flavor", 500);
                }

                // checking destination account
                AccountBL accountBL = new AccountBL(this.getStub());
                Account account = accountBL.getAccount(tkn.getAccountId());

                if (account == null){
                    throw new BalanceTrackerException("Cannot merge, referred  Account Id does not exist", 500);
                }

                if ((!accountId.equals("") && (!accountId.equals(account.getAccountId())))){
                    throw new BalanceTrackerException("Cannot merge, tokens does not refer to the same account", 500);
                }

                amount += tkn.getAmount();
                accountId = account.getAccountId();
                flavorId = flv.getFlavorId();

                // retire each token
                Token token = tokenBL.retireToken(_tokenId, tkn.getAmount());

            }

            // create new token : only non-fungible: code is always zero
            Token token = tokenBL.createToken(_newTokenId, "", amount, flavorId, accountId, _tokenTags);

            return token;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }



}
