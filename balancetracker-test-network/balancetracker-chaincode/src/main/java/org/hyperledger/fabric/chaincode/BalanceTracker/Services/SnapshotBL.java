package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Snapshot;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Token;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Key;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;


/**
 * basic crud functionality for snapshot
 */
public class SnapshotBL extends BusinessLogicBase {

   public SnapshotBL(ChaincodeStub _stub){
        this.stub = _stub;
    }

    /**
     * Getting snapshot by token id
     *
     * @param  snapshotId arguments of the call
     * @return       chaincode response
     */
    public Snapshot getSnapshot(String snapshotId) throws BalanceTrackerException {

        try {

            String snapshotString = this.getStub().getStringState(snapshotId);

            if (snapshotString == null)
                return null;

            if(!checkString(snapshotString))
                return null;

            Snapshot snapshot = Snapshot.createSnapshot(snapshotString);

            return snapshot;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * Creating a new token
     *
     * @param  _snapshotId arguments of the call
     * @param  _action_tags arguments of the call
     * @param  _flavor_tags arguments of the call
     * @param  _transaction_tags arguments of the call
     * @return       chaincode response
     */
    Snapshot createSnapshot(String _snapshotId, List<String> _action_tags, List<String> _flavor_tags, List<String> _transaction_tags)  throws BalanceTrackerException  {


        try{

            // Business logic error handling
            // _snapshotId error handling
            if ((_snapshotId == null) || (_snapshotId.length() == 0) ){
                throw new BalanceTrackerException("Cannot create Snapshot, SnapshotId is null", 500);
            }

            if (this.getSnapshot(_snapshotId) != null){
                throw new BalanceTrackerException("Cannot create Snapshot, Snapshot Id is already taken", 500);
            }

            // create new Token and write it into the blockchain

            Snapshot snapshotToStore = new Snapshot(_snapshotId,_action_tags,_flavor_tags, _transaction_tags);

            this.getStub().putState(_snapshotId, (new ObjectMapper()).writeValueAsBytes(snapshotToStore));

            return snapshotToStore;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
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
    Snapshot updateSnapshot(String _snapshotId, List<String> _action_tags, List<String> _flavor_tags, List<String> _transaction_tags)  throws BalanceTrackerException  {

        try {

            Snapshot snapshotToUpdate = this.getSnapshot(_snapshotId);

            if (snapshotToUpdate == null){
                throw new BalanceTrackerException("Cannot update Snapshot, Snapshot Id is not found", 500);
            }

            snapshotToUpdate.updateActionTags(_action_tags);
            snapshotToUpdate.updateFlavorTags(_flavor_tags);
            snapshotToUpdate.updateTransactionTags(_transaction_tags);

            this.getStub().putState(_snapshotId, (new ObjectMapper()).writeValueAsBytes(snapshotToUpdate));

            return snapshotToUpdate;
        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }



}
