package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Config;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Base ancestor class for business logic
 */
public class BusinessLogicBase extends BalanceTrackerBase {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(FlavorBL.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Custom get state implementation with private property config override
     * @param key name of the value
     * @return value the value read from the ledger
     */
    byte[] getState(String key, String objectType) throws BalanceTrackerException{

        logger.entering(this.getClass().getSimpleName(), "getState()");

        if (this.getStub() == null)
            throw new StubIsNullException("Stub can not be found at BusinessLogicBase geState");

        MasterBL master = new MasterBL(this.getStub());
        Config config = master.getConfig();

        if (config == null)
            throw new ConfigDoesNotExistException("Config can not be found at BusinessLogicBase geState");

        HashMap<String, String> storageModels = config.getStorageModel();

        String storageModelForObject = "";
        for (Map.Entry<String, String> storageElement : storageModels.entrySet()) {
            if(storageElement.getKey().equals(objectType)){
                storageModelForObject = storageElement.getValue();
            }
        }

        logger.fine("Configured storage model is " + storageModelForObject + " for " + objectType);

        byte[] toRet = null;

        if ((storageModelForObject == null) || (storageModelForObject.isEmpty())){
            // normal get
            toRet = this.getStub().getState(key);

        } else if (!storageModelForObject.equals(NONSTORAGE_CONFIG)){
            // private get
            toRet = this.getStub().getPrivateData(storageModelForObject,key);

        }else if (storageModelForObject.equals(NONSTORAGE_CONFIG)){
        // object is not implemented: no get -> do nothing

        }

        logger.exiting(this.getClass().getSimpleName(), "getState()");

        return toRet;
    }

    /**
     * Custom get state implementation with private property config override
     *
     * @param key   name of the value
     */
    String getStringState(String key, String objectType) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getStringState()");

        if (this.getStub() == null)
            throw new StubIsNullException("Stub can not be found at BusinessLogicBase geState");

        MasterBL master = new MasterBL(this.getStub());
        Config config = master.getConfig();

        if (config == null)
            throw new ConfigDoesNotExistException("Config can not be found at BusinessLogicBase geState");

        HashMap<String, String> storageModels = config.getStorageModel();

        String storageModelForObject = "";
        for (Map.Entry<String, String> storageElement : storageModels.entrySet()) {
            if(storageElement.getKey().equals(objectType)){
                storageModelForObject = storageElement.getValue();
            }
        }

        logger.fine("Configured storage model is " + storageModelForObject + " for " + objectType);

        String toRet = null;

        if ((storageModelForObject == null) || (storageModelForObject.isEmpty())){
            // normal get
            toRet = this.getStub().getStringState(key);

        } else if (!storageModelForObject.equals(NONSTORAGE_CONFIG)){
            // private get
            toRet = this.getStub().getPrivateDataUTF8(storageModelForObject,key);

        }else if (storageModelForObject.equals(NONSTORAGE_CONFIG)){
            // object is not implemented: no get -> do nothing

        }

        logger.exiting(this.getClass().getSimpleName(), "getStringState()");

        return toRet;
    }


    /**
     * Custom put state implementation with private property config override
     *
     * @param key   name of the value
     * @param value the value to write to the ledger
     */
    void putState(String key, byte[] value, String objectType) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getState()");

        if (this.getStub() == null)
            throw new StubIsNullException("Stub can not be found at BusinessLogicBase putState");

        MasterBL master = new MasterBL(this.getStub());
        Config config = master.getConfig();

        if (config == null)
            throw new ConfigDoesNotExistException("Config can not be found at BusinessLogicBase putState");

        HashMap<String, String> storageModels = config.getStorageModel();

        String storageModelForObject = "";
        for (Map.Entry<String, String> storageElement : storageModels.entrySet()) {
            if(storageElement.getKey().equals(objectType)){
                storageModelForObject = storageElement.getValue();
            }
        }

        logger.fine("Configured storage model is " + storageModelForObject + " for " + objectType);

        if ((storageModelForObject == null) || (storageModelForObject.isEmpty())){
            // normal put
            this.getStub().putState(key, value);

        } else if (!storageModelForObject.equals(NONSTORAGE_CONFIG)){
            // private put
            this.getStub().putPrivateData(storageModelForObject,key, value);

        }else if (storageModelForObject.equals(NONSTORAGE_CONFIG)){
            // object is not implemented: no put -> do nothing

        }

        logger.exiting(this.getClass().getSimpleName(), "getState()");
    }


    /**
     * Custom delstate
     *
     * @param key name of the value to be deleted
     */
    void delState(String key, String objectType) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "delState()");

        if (this.getStub() == null)
            throw new StubIsNullException("Stub can not be found at BusinessLogicBase putState");

        MasterBL master = new MasterBL(this.getStub());
        Config config = master.getConfig();

        if (config == null)
            throw new ConfigDoesNotExistException("Config can not be found at BusinessLogicBase putState");

        HashMap<String, String> storageModels = config.getStorageModel();

        String storageModelForObject = "";
        for (Map.Entry<String, String> storageElement : storageModels.entrySet()) {
            if(storageElement.getKey().equals(objectType)){
                storageModelForObject = storageElement.getValue();
            }
        }

        logger.fine("Configured storage model is " + storageModelForObject + " for " + objectType);

        if ((storageModelForObject == null) || (storageModelForObject.isEmpty())){
            // normal put
            this.getStub().delState(key);

        } else if (!storageModelForObject.equals(NONSTORAGE_CONFIG)){
            // private put
            this.getStub().delPrivateData(storageModelForObject,key);

        }else if (storageModelForObject.equals(NONSTORAGE_CONFIG)){
            // object is not implemented: no put -> do nothing

        }

        logger.exiting(this.getClass().getSimpleName(), "delState()");

    }

    /**
     * Initialization error : Stub is null
     */
    public static class StubIsNullException extends BalanceTrackerException
    {
        protected Integer code = 5090;

        public StubIsNullException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Service not configured to run
     */
    public static class ConfigDoesNotExistException extends BalanceTrackerException
    {
        protected Integer code = 5090;

        public ConfigDoesNotExistException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }
}
