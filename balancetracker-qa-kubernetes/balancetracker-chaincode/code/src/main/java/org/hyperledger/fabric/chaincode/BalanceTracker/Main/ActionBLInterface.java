package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Action;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Token;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.ActionTypeEnum;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract interface for the business logic of the action
 */
public interface ActionBLInterface {

    /**
     * Getting an action by account id
     * only internally
     *
     * @param  actionId arguments of the call
     * @return       chaincode response
     */
    Action getAction(String actionId) throws BalanceTrackerException;

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
    Action createAction(String _actionId, ActionTypeEnum _type, Double _amount, String _transactionId, String _flavorId, String _sourceAccountId, String _destinationAccountId, String _snapshotId, List<String> _params, HashMap<String,String> _tags) throws BalanceTrackerException;

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
    Token issue(String _transactionId, String _actionId, String _tokenId, String _code, Double _amount, String _flavorId, String _destinationAccountId, HashMap<String,String> _actionTags, HashMap<String,String> _tokenTags, HashMap<String,String> _transactionTags) throws BalanceTrackerException;

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
    Token retire(String _transactionId, String _actionId, String _tokenId, Double _amount, HashMap<String,String> _actionTags, HashMap<String,String> _transactionTags ) throws BalanceTrackerException;

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
    Token transfer(String _transactionId, String _actionId, String _tokenId, String _newTokenId, Double _amount, String _destinationAccountId, HashMap<String,String> _actionTags, HashMap<String,String> _tokenTags, HashMap<String,String> _transactionTags) throws BalanceTrackerException;

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
    Token merge(List<String> _tokenIds, String _newTokenId, HashMap<String,String> _tokenTags) throws BalanceTrackerException;

    }
