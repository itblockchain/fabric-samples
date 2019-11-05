package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;


/**
 * Key model
 */
public class Key extends ModelBase {

    protected String keyId;

    /**
     * Constructor
     */
    public Key(String keyId) {

        this.keyId = keyId;
        this.modelType = "Key";

    }

    public String getKeyId() {
        return keyId;
    }

    /**
     * Factory function
     */
    public static Key createKey(String keyString) throws Key.DeserializationException{

        try {

            Gson gson = new Gson();
            Key key = gson.fromJson(keyString, Key.class);

            return key;
        }
        catch (Exception _ex){
            throw new Key.DeserializationException("There is a problem at reading out key from the blockchain ",  _ex);
        }
    }

    /**
     * EXCEPTIONS
     */

    /**
     * DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5054;

        public DeserializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * SerializationException
     */
    public static class SerializationException extends BalanceTrackerException
    {
        protected Integer code = 5064;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}

