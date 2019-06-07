package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot model
 */
public class Snapshot extends ModelBase {

    protected String snapshotId;
    protected List<String> action_tags;
    protected List<String> flavor_tags;
    protected List<String> transaction_tags;

    /**
     * Default constructor
     */
    public Snapshot(String _snapshotId){
        snapshotId = snapshotId;
        action_tags = new ArrayList<String>();
        flavor_tags = new ArrayList<String>();
        transaction_tags = new ArrayList<String>();
    }

    /**
     * Non-default constructor
     */
    public Snapshot(String _snapshotId, List<String> _action_tags, List<String> _flavor_tags, List<String> _transaction_tags){
        snapshotId = _snapshotId;
        action_tags = _action_tags;
        flavor_tags = _flavor_tags;
        transaction_tags = _transaction_tags;
    }

    /**
     * Getters
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    public List<String> getActionTags(){
        return action_tags;
    }

    public List<String> getFlavorTags(){
        return flavor_tags;
    }

    public List<String> getTransactionTags(){
        return transaction_tags;
    }

    /**
     * Update functions
     */

    public void updateActionTags(List<String> _action_tags){
        this.action_tags = new ArrayList<String>();
        this.action_tags.addAll(_action_tags);
    }

    public void updateFlavorTags(List<String> _flavor_tags){
        this.flavor_tags = new ArrayList<String>();
        this.flavor_tags.addAll(_flavor_tags);
    }

    public void updateTransactionTags(List<String> _transaction_tags){
        this.transaction_tags = new ArrayList<String>();
        this.transaction_tags.addAll(_transaction_tags);
    }

    /**
     * Factory function
     */
    public static Snapshot createSnapshot(String transactionString) throws BalanceTrackerException {

        try {

            JsonObject jsonObject = new JsonParser().parse(transactionString).getAsJsonObject();

            String snapshotId = jsonObject.get("snapshotId").getAsString();
            List<String> action_tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("action_tags"));
            List<String> flavor_tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("flavor_tags"));
            List<String> transaction_tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("transaction_tags"));

            Snapshot s = new Snapshot(snapshotId, action_tags, flavor_tags, transaction_tags);
            return s;
        }
        catch (Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }

}
