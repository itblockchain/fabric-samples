package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;

/**
 * Base class for the different models.
 */
public class ModelBase {

    /**
     * Type of the model to be stored
     */
    protected String modelType;

    public String toJSONString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    public String getModelType(){
        return this.modelType;
    }

}
