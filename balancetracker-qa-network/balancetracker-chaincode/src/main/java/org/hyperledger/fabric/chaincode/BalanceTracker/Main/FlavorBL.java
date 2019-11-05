package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Business logic for flavor
 */
public class FlavorBL extends BusinessLogicBase implements FlavorBLInterface {

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
     * Stub initialization
     */
    public FlavorBL(ChaincodeStub _stub) throws FlavorBL.StubIsNullException{
        if (_stub == null)
            throw new FlavorBL.StubIsNullException("Stub can not be null at initializing the flavor business logic");
        this.stub = _stub;
    }

    /**
     * Getting a flavor by flavor id
     * only internally
     *
     * @param  flavorId arguments of the call
     * @return       chaincode response
     */
    public Flavor getFlavor(String flavorId) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getFlavor()");
        logger.finer("Parameters : flavorId : " + flavorId);
        logger.finer("transaction id : " + this.getStub().getTxId());


        String flavorString = this.getStringState(flavorId, FLAVOR);

            // returning null is a standard case
            if (flavorString == null)
                return null;

            if(!checkString(flavorString))
                return null;

            Flavor flavor = Flavor.createFlavor(flavorString);

        logger.exiting(this.getClass().getSimpleName(), "getFlavor()");
        logger.finer("return : " + flavor.toJSONString());

        return flavor;
    }


    /**
     * Creating a flavor by several parameters
     *
     * @param  _flavorId arguments of the call
     * @return       Flavor response
     */
     public Flavor createFlavor(String _flavorId, boolean _isFungible, HashMap<String,String> _tags, Integer _quorum, List<String> _keyIds) throws BalanceTrackerException{

        logger.entering(this.getClass().getSimpleName(), "createFlavor()");
        logger.finer("Parameters : flavorId" + _flavorId +
                "\n isFungible : " + _isFungible +
                "\n quorum : " + _quorum.toString());

        // Business logic error handling

            if (this.getFlavor(_flavorId)!= null){
                throw new FlavorBL.IdTakenException("Cannot create Flavor, Flavor Id is already taken");
            }

            if (_quorum > _keyIds.size()){
                throw new FlavorBL.ParameterNotCompatibleException("Cannot create Flavor, quorum is bigger than the number of key ids");
            }

            // checking if keys id-s exist
            KeyBL keyBL = new KeyBL(this.getStub());
            for (String _id : _keyIds){
                if (keyBL.getKey(_id) == null){
                    throw new FlavorBL.NotExistException("Referred input key does not exist at the createFlavor call of the Flavor service, keyID: " + _id);
                }
            }

            // create new flavor and write it into the blockchain

            Flavor flavorToStore = new Flavor(_flavorId, _isFungible, _tags, _quorum,  _keyIds);

            try {

                this.putState(_flavorId, (new ObjectMapper()).writeValueAsBytes(flavorToStore), FLAVOR);
                this.getStub().setEvent(Constants.EventNames.FLAVOR_CREATED,(new ObjectMapper()).writeValueAsBytes(flavorToStore));

            } catch ( JsonProcessingException _ex){
                throw new Flavor.SerializationException("Error at writing Action into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "createFlavor()");
        logger.finer("flavorId : " + _flavorId);

        return flavorToStore;
    }

    /**
     * Update Flavor, only tags can be added
     *
     * @param  _flavorId arguments of the call
     * @param  tags arguments of the call
     * @return       chaincode response
     */
    public Flavor updateFlavor(String _flavorId, HashMap<String,String> tags) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "updateFlavor()");
        logger.finer("Parameters : flavorId : " + _flavorId);

        Flavor flavorToUpdate = this.getFlavor(_flavorId);

            if (flavorToUpdate == null){
                throw new FlavorBL.NotExistException("Cannot update Flavor, Flavor Id is not found");
            }

            flavorToUpdate.addTags(tags);

            try {
                this.putState(_flavorId, (new ObjectMapper()).writeValueAsBytes(flavorToUpdate), FLAVOR);
                this.getStub().setEvent(Constants.EventNames.FLAVOR_UPDATED,(new ObjectMapper()).writeValueAsBytes(flavorToUpdate));

            } catch (JsonProcessingException _ex){
            throw new Flavor.SerializationException("Error at writing Flavor into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "updateFlavor()");
        logger.finer("flavorId : " + _flavorId);

        return flavorToUpdate;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Input is not a string
     */
    public static class ParameterIsNullException extends InputParameterInvalidException
    {
        protected Integer code = 5023;

        public ParameterIsNullException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Input can not be converted
     */
    public static class ParameterNotCompatibleException extends InputParameterInvalidException
    {
        protected Integer code = 5033;

        public ParameterNotCompatibleException(String msg) {
            super(msg);
        }

        public ParameterNotCompatibleException(String msg, Exception _cause) {
            super(msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Account does not exsit -> can not be queried, can not be updated
     */
    public static class NotExistException extends InputParameterInvalidException
    {
        protected Integer code = 5043;

        public NotExistException(String msg) {
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
        protected Integer code = 5093;

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
        protected Integer code = 5083;

        public IdTakenException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}
