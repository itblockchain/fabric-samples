package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Key;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Key business logic
 */
public class KeyBL extends BusinessLogicBase implements KeyBLInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(KeyBL.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Key business logic constructor
     * Chaincode stub has to be initialized
     */
    public KeyBL(ChaincodeStub _stub) throws KeyBL.StubIsNullException {
        if (_stub == null)
            throw new KeyBL.StubIsNullException("Stub can not be null at initializing the key BL service");
        this.stub = _stub;
    }

    /**
     * Getting key by key id
     * only internally callable
     *
     * @param  keyId arguments of the call
     * @return key object if exist, null if it does not exist (null is an expected value)
     */
    public Key getKey(String keyId) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getKey()");
        logger.finer("Parameters : keyId : " + keyId);
        logger.finer("transaction id : " + this.getStub().getTxId());

        String keyString = this.getStringState(keyId, KEY);

         // not found can be normal
         if (keyString == null)
             return null;

         if(!checkString(keyString))
            return null;

         Key key = Key.createKey(keyString);

        logger.exiting(this.getClass().getSimpleName(), "getKey()");
        logger.finer("return : " + key.toJSONString());

        return key;
    }

    /**
     * Creating new key
     *
     * @param  _keyId primary key of the newly created id
     */
    public Key createKey(String _keyId) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "createKey()");
        logger.finer("Parameters : keyId : " + _keyId);

        // internal validation if getKey() != null return error

        if (this.getKey(_keyId) != null) {
            throw new KeyBL.IdTakenException("Cannot create key, Key Id is already taken");
        }

        Key keyToStore = new Key(_keyId);

        try {

            this.putState(_keyId, (new ObjectMapper()).writeValueAsBytes(keyToStore), KEY);
            this.getStub().setEvent(Constants.EventNames.KEY_CREATED,(new ObjectMapper()).writeValueAsBytes(keyToStore));

        } catch (JsonProcessingException _ex){
            throw new Key.SerializationException("Error at writing Key into the ledger", _ex);
        }

        logger.entering(this.getClass().getSimpleName(), "createKey()");
        logger.finer("return : " + keyToStore.toJSONString());

        return keyToStore;
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * Initialization error : Stub is null
     */
    public static class StubIsNullException extends BalanceTrackerException
    {
        protected Integer code = 5094;

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
        protected Integer code = 5084;

        public IdTakenException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Account does not exsit -> can not be queried, can not be updated
     */
    public static class NotExistException extends BalanceTrackerException
    {
        protected Integer code = 5041;

        public NotExistException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }


}

