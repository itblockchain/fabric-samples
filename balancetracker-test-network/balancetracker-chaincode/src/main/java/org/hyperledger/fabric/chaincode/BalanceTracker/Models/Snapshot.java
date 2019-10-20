package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Snapshot model
 */
public class Snapshot extends ModelBase {

    protected String snapshotId;
    protected HashMap<String,String> actionTags;
    protected HashMap<String,String> flavorTags;
    protected HashMap<String,String> transactionTags;

    /**
     * Default constructor
     */
    public Snapshot(String _snapshotId){
        snapshotId = snapshotId;
        actionTags = new HashMap<String,String>();
        flavorTags = new HashMap<String,String>();
        transactionTags = new HashMap<String,String>();
        this.modelType = "Snapshot";
    }

    /**
     * Non-default constructor
     */
    public Snapshot(String _snapshotId, HashMap<String,String> _action_tags, HashMap<String,String> _flavor_tags, HashMap<String,String> _transaction_tags){
        snapshotId = _snapshotId;
        actionTags = _action_tags;
        flavorTags = _flavor_tags;
        transactionTags = _transaction_tags;
        this.modelType = "Snapshot";
    }

    /**
     * Getters
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    public HashMap<String,String> getActionTags(){
         if(actionTags == null)
             return new HashMap<String,String>();
        return actionTags;
    }

    public HashMap<String,String> getFlavorTags(){
        if(flavorTags == null)
            return new HashMap<String,String>();
        return flavorTags;
    }

    public HashMap<String,String> getTransactionTags(){
        if(transactionTags == null)
            return new HashMap<String,String>();
        return transactionTags;
    }

    /**
     * Update functions
     */

    public void updateActionTags(HashMap<String,String> _action_tags){
        this.actionTags.putAll(_action_tags);
    }

    public void updateFlavorTags(HashMap<String,String>_flavor_tags){
        this.flavorTags.putAll(_flavor_tags);
    }

    public void updateTransactionTags(HashMap<String,String> _transaction_tags){
        this.transactionTags.putAll(_transaction_tags);
    }

    /**
     * Factory function
     */
    public static Snapshot createSnapshot(String snapshotString) throws Snapshot.DeserializationException {

        try {

            Gson gson = new Gson();
            Snapshot snapshot = gson.fromJson(snapshotString, Snapshot.class);

            return snapshot;
        }
        catch (Exception _ex){
            throw new Snapshot.DeserializationException("There is a problem at reading out snapshot from the blockchain ",  _ex);
        }
    }

    /**
     * EXCEPTIONS
     */

    /**
     *DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5055;

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
        protected Integer code = 5065;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }


}
