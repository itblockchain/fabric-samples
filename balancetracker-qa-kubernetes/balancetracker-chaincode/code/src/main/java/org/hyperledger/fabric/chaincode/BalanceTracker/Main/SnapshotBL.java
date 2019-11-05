package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Snapshot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;


/**
 * basic crud functionality for snapshot
 */
public class SnapshotBL extends BusinessLogicBase implements SnapshotBLInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(SnapshotBL.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Constructor
     */
    public SnapshotBL(ChaincodeStub _stub) throws SnapshotBL.StubIsNullException{
       if (_stub == null){
           throw new SnapshotBL.StubIsNullException("Stub can not be null at initializing the snaphot service");
       }
       this.stub = _stub;
    }

    /**
     * Getting snapshot by token id
     *
     * @param  snapshotId arguments of the call
     * @return       Snapshot response
     */
    public Snapshot getSnapshot(String snapshotId) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getSnapshot()");
        logger.finer("Parameters : snapshotId : " + snapshotId);
        logger.finer("transaction id : " + this.getStub().getTxId());

        String snapshotString = this.getStringState(snapshotId, SNAPSHOT);

            // null is a possible outcome
            if (snapshotString == null)
                return null;

            if(!checkString(snapshotString))
                return null;

            Snapshot snapshot = Snapshot.createSnapshot(snapshotString);

        logger.entering(this.getClass().getSimpleName(), "getSnapshot()");
        logger.finer("result : " + snapshot.toJSONString());

        return snapshot;
    }

    /**
     * Creating a new token
     *
     * @param  _snapshotId arguments of the call
     * @param  _action_tags arguments of the call
     * @param  _flavor_tags arguments of the call
     * @param  _transaction_tags arguments of the call
     * @return       Snapshot response
     */
    public Snapshot createSnapshot(String _snapshotId, HashMap<String,String> _action_tags, HashMap<String,String> _flavor_tags, HashMap<String,String> _transaction_tags)  throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "createSnapshot()");
        logger.finer("Parameters : snapshotId : " + _snapshotId);

        // Business logic error handling
            // _snapshotId error handling
            if ((_snapshotId == null) || (_snapshotId.length() == 0) ){
                throw new SnapshotBL.ParameterIsNullException("Cannot create Snapshot, SnapshotId is null");
            }

            if (this.getSnapshot(_snapshotId) != null){
                throw new SnapshotBL.IdTakenException("Cannot create Snapshot, Snapshot Id is already taken");
            }

            // create new Token and write it into the blockchain

            Snapshot snapshotToStore = new Snapshot(_snapshotId,_action_tags,_flavor_tags, _transaction_tags);

            try {

                this.putState(_snapshotId, (new ObjectMapper()).writeValueAsBytes(snapshotToStore), SNAPSHOT);
                this.getStub().setEvent(Constants.EventNames.SNAPSHOT_CREATED,(new ObjectMapper()).writeValueAsBytes(snapshotToStore));

            } catch (JsonProcessingException _ex){
                throw new Snapshot.SerializationException("Error at writing Snaphot into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "createSnapshot()");
        logger.finer("result : " + snapshotToStore.toJSONString());

        return snapshotToStore;
    }

    /**
     * Updating snapshot by setting new tag structure
     *
     * @param  _snapshotId arguments of the call
     * @param  _action_tags arguments of the call
     * @param  _flavor_tags arguments of the call
     * @param  _transaction_tags arguments of the call
     * @return       chaincode response
     */
    public Snapshot updateSnapshot(String _snapshotId, HashMap<String,String> _action_tags, HashMap<String,String> _flavor_tags, HashMap<String,String> _transaction_tags)  throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "updateSnapshot()");
        logger.finer("Parameters : snapshotId : " + _snapshotId);

        Snapshot snapshotToUpdate = this.getSnapshot(_snapshotId);

            if (snapshotToUpdate == null){
                throw new SnapshotBL.NotExistException("Cannot update Snapshot, Snapshot Id is not found");
            }

            snapshotToUpdate.updateActionTags(_action_tags);
            snapshotToUpdate.updateFlavorTags(_flavor_tags);
            snapshotToUpdate.updateTransactionTags(_transaction_tags);

            try {

                this.putState(_snapshotId, (new ObjectMapper()).writeValueAsBytes(snapshotToUpdate), SNAPSHOT);
                this.getStub().setEvent(Constants.EventNames.SNAPSHOT_UPDATED,(new ObjectMapper()).writeValueAsBytes(snapshotToUpdate));

            } catch (JsonProcessingException _ex){
                throw new Snapshot.SerializationException("Error at writing Snaphot into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "updateSnapshot()");
        logger.finer("result : " + snapshotToUpdate.toJSONString());

        return snapshotToUpdate;
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * Input is not a string
     */
    public static class ParameterIsNullException extends InputParameterInvalidException
    {
        protected Integer code = 5027;

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
        protected Integer code = 5037;

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
        protected Integer code = 5047;

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
        protected Integer code = 5097;

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
        protected Integer code = 5087;

        public IdTakenException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }
}
