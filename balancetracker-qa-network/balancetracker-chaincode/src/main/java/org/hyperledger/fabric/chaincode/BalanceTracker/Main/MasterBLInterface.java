package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Config;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract interface for the business logic of the master service
 */
public interface MasterBLInterface {

    /**
     * get Config value object if exist
     * @return config object if exist, null if it does not exist (null is an expected value)
     */
    public Config getConfig() throws Config.DeserializationException;


    /**
     * Creating a new config
     *
     * @param  _services primary key of the newly created id*
     */
    public Config createConfig(List<String> _services, HashMap<String, String> _storageModels) throws BalanceTrackerException;


    /**
     * Updating the config
     *
     * @param  _services primary key of the newly created id*
     */
    public Config updateConfig(List<String> _services, HashMap<String, String> _storageModels) throws BalanceTrackerException;

    }
