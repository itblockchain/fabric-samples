package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.JsonArray;
import com.google.gson.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.LogLevelEnum;
import org.hyperledger.fabric.shim.Chaincode;


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
    }

    public String getKeyId() {
        return keyId;
    }

    /**
     * Factory function
     */
    public static Key createKey(String keyString) throws BalanceTrackerException{

        try {

            JsonObject jsonObject = new JsonParser().parse(keyString).getAsJsonObject();

            String keyId = jsonObject.get("keyId").getAsString();

            Key key = new Key(keyId);

            return key;

        }
        catch (Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }

}

