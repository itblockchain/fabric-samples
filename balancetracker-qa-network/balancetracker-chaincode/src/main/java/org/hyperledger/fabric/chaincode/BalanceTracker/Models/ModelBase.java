package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;

/**
 * Base class for the different models.
 */
public class ModelBase {

    public String toJSONString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

}
