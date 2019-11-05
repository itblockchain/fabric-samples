package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Key;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

/**
 * Abstract interface for the business logic of the key
 */
public interface KeyBLInterface {

    /**
     * Getting key by key id
     * only internally callable
     *
     * @param  keyId arguments of the call
     * @return key object if exist, null if it does not exist (null is an expected value)
     */
    Key getKey(String keyId) throws BalanceTrackerException;

    /**
     * Creating new key
     *
     * @param  _keyId primary key of the newly created id
     */
    Key createKey(String _keyId) throws BalanceTrackerException;



}
