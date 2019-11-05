package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract interface for the business logic of the account
 */
public interface AccountBLInterface {

    /**
     * Getting account by account id
     * only internally
     *
     * @param  accountId arguments of the call
     * @return       chaincode response
     */
    Account getAccount(String accountId) throws BalanceTrackerException;

    /**
     * Getting account by account id
     * only internally
     *
     * @param  _accountId arguments of the call
     * @param  _tags arguments of the call
     * @param  _quorum arguments of the call
     * @param  _keyIds arguments of the call
     **
     * @return       chaincode response
     */
    Account createAccount(String _accountId, HashMap<String,String> _tags, Integer _quorum, List<String> _keyIds) throws BalanceTrackerException;

    /**
     * Update account only tags can be updated
     *
     * @param  _accountId arguments of the call
     * @param  _tags arguments of the call*
     * @return       chaincode response
     */
    Account updateAccount(String _accountId, HashMap<String,String> _tags) throws BalanceTrackerException;

}
