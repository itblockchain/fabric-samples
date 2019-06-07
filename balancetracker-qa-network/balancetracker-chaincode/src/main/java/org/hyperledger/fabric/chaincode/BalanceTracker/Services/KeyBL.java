package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Key;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.QueryFilter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * Key business logic
 */
public class KeyBL extends BusinessLogicBase  {

    /**
     * Key business logic constructor
     * Chaincode stub has to be initialized
     */
    public KeyBL(ChaincodeStub _stub) {
        this.stub = _stub;
    }

    /**
     * Getting key by key id
     * only internally callable
     *
     * @param  keyId arguments of the call
     * @return key object if exist, null if it does not exist (null is an expected value)
     */
    Key getKey(String keyId) throws BalanceTrackerException {

        try {

            String keyString = this.getStub().getStringState(keyId);

            if (keyString == null)
                return null;

            if(!checkString(keyString))
                return null;

            Key key = Key.createKey(keyString);
            return key;
        }
        catch (Throwable ex){
            throw ex;
        }
    }

    /**
     * Creating new key
     *
     * @param  _keyId primary key of the newly created id
     */
    Key createKey(String _keyId) throws BalanceTrackerException{

        try {

            // internal validation if getKey() != null return error

            if (this.getKey(_keyId)!= null){
                throw new BalanceTrackerException("Cannot create key, Key Id is already taken");
            }

            Key keyToStore = new Key(_keyId);

            this.getStub().putState(_keyId, (new ObjectMapper()).writeValueAsBytes(keyToStore));

            return keyToStore;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }
}
