package org.hyperledger.fabric.chaincode.BalanceTracker.Main;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Config;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Master business logic for storing permanent meta information in the ledger
 */
public class MasterBL extends BusinessLogicBase implements MasterBLInterface{

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
     * Master business logic constructor
     * Chaincode stub has to be initialized
     */
    public MasterBL(ChaincodeStub _stub) throws MasterBL.StubIsNullException {
        if (_stub == null)
            throw new MasterBL.StubIsNullException("Stub can not be null at initializing the master BL service");
        this.stub = _stub;
    }

    /**
     * get Config value object if exist
     * @return config object if exist, null if it does not exist (null is an expected value)
     */
    public Config getConfig() throws Config.DeserializationException {

        logger.entering(this.getClass().getSimpleName(), "getConfig()");
        logger.finer("Parameters : configId : " + BusinessLogicBase.configId);

        String configString = this.getStub().getStringState(BusinessLogicBase.configId);

        // not found can be normal
        if (configString == null)
            return null;

        if(!checkString(configString))
            return null;

        Config config = Config.createConfig(configString);

        logger.exiting(this.getClass().getSimpleName(), "getConfig()");
        logger.finer("return : " + config.toJSONString());

        return config;
    }


    /**
     * Creating a new config
     *
     * @param  _services primary key of the newly created id*
     */
    public Config createConfig(List<String> _services, HashMap<String, String> _storageModels) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "createConfig()");
        logger.finer("Parameters : configId : " + BusinessLogicBase.configId);

        // internal validation if getKey() != null return error

        if (this.getConfig() != null) {
            throw new MasterBL.IdTakenException("Cannot create config, config Id is already taken. Configuration can be created only once");
        }

        Config configToStore = new Config(_services, _storageModels);

        try {

            this.getStub().putState(BusinessLogicBase.configId, (new ObjectMapper()).writeValueAsBytes(configToStore));
            this.getStub().setEvent(Constants.EventNames.CONFIG_CREATED,(new ObjectMapper()).writeValueAsBytes(configToStore));

        } catch (JsonProcessingException _ex){
            throw new Config.SerializationException("Error at writing Config into the ledger", _ex);
        }

        logger.entering(this.getClass().getSimpleName(), "createConfig()");
        logger.finer("return : " + configToStore.toJSONString());

        return configToStore;
    }

    /**
     * Updating the config
     *
     * @param  _services primary key of the newly created id*
     */
    public Config updateConfig(List<String> _services, HashMap<String, String> _storageModels) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "updateConfig()");
        logger.finer("Parameters : configId : " + BusinessLogicBase.configId);

        // internal validation if getKey() != null return error

        // internal validation if getKey() != null return error

        if (this.getConfig() == null) {
            throw new MasterBL.IdTakenException("Cannot find config to update");
        }

        Config configToStore = new Config(_services, _storageModels);

        try {

            this.getStub().putState(BusinessLogicBase.configId, (new ObjectMapper()).writeValueAsBytes(configToStore));
            this.getStub().setEvent(Constants.EventNames.CONFIG_CREATED,(new ObjectMapper()).writeValueAsBytes(configToStore));

        } catch (JsonProcessingException _ex){
            throw new Config.SerializationException("Error at writing Config into the ledger", _ex);
        }

        logger.entering(this.getClass().getSimpleName(), "updateConfig()");
        logger.finer("return : " + configToStore.toJSONString());

        return configToStore;
    }


    /**
     * EXCEPTION CLASSES
     */
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

}
