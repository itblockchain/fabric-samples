package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Snapshot;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract interface for the business logic of the snapshot
 */
public interface SnapshotBLInterface {

    /**
     * Getting snapshot by token id
     *
     * @param  snapshotId arguments of the call
     * @return       Snapshot response
     */
    Snapshot getSnapshot(String snapshotId) throws BalanceTrackerException;

    /**
     * Creating a new token
     *
     * @param  _snapshotId arguments of the call
     * @param  _action_tags arguments of the call
     * @param  _flavor_tags arguments of the call
     * @param  _transaction_tags arguments of the call
     * @return       Snapshot response
     */
    Snapshot createSnapshot(String _snapshotId, HashMap<String,String> _action_tags, HashMap<String,String> _flavor_tags, HashMap<String,String> _transaction_tags)  throws BalanceTrackerException;


    /**
     * Updating snapshot by setting new tag structure
     *
     * @param  _snapshotId arguments of the call
     * @param  _action_tags arguments of the call
     * @param  _flavor_tags arguments of the call
     * @param  _transaction_tags arguments of the call
     * @return       chaincode response
     */
    Snapshot updateSnapshot(String _snapshotId, HashMap<String,String> _action_tags, HashMap<String,String> _flavor_tags, HashMap<String,String> _transaction_tags)  throws BalanceTrackerException;

    }
