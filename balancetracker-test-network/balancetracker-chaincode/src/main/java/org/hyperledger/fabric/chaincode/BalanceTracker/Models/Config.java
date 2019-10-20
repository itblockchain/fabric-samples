package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Model storing global config
 */
public class Config extends ModelBase {

    protected List<String> services;
    protected HashMap<String, String> storageModel;

    /**
     * Constructor
     */
    public Config(List<String> _services, HashMap<String, String> _storageModel)
    {
        this.storageModel = _storageModel;
        this.services = _services;
    }

    /**
     * Getters
     */
    public List<String> getServices() {
        if (services == null)
            return new ArrayList<String>();
        return services;
    }

    public HashMap<String, String> getStorageModel() {
        if (storageModel == null)
            return new HashMap<String, String>();
        return storageModel;
    }


    /**
     * Factory function
     */
    public static Config createConfig(String configString) throws Config.DeserializationException {

        try {

            Gson gson = new Gson();
            Config cnf = gson.fromJson(configString, Config.class);

            return cnf;
        }
        catch (Exception _ex){
            throw new Config.DeserializationException("There is a problem at reading out the config from the blockchain ",  _ex);
        }
    }

    /**
     * DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5059;

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
        protected Integer code = 5069;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

}
