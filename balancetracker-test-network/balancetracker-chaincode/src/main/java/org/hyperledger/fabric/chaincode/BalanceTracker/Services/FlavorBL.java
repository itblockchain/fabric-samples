package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.QueryFilter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

public class FlavorBL extends BusinessLogicBase {

    public FlavorBL(ChaincodeStub _stub){
        this.stub = _stub;
    }

    /**
     * Getting a flavor by flavor id
     * only internally
     *
     * @param  flavorId arguments of the call
     * @return       chaincode response
     */
    Flavor getFlavor(String flavorId) throws BalanceTrackerException {

        try {

            String flavorString = this.getStub().getStringState(flavorId);

            if (flavorString == null)
                return null;

            if(!checkString(flavorString))
                return null;

            Flavor flavor = Flavor.createFlavor(flavorString);

            return flavor;

        }
        catch (Throwable ex){
            throw ex;
        }

    }


    /**
     * Creating a flavor by several parameters
     *
     * @param  _flavorId arguments of the call
     * @return       chaincode response
     */
    Flavor createFlavor(String _flavorId, boolean _isFungible, List<String> _tags, Integer _quorum, List<String> _keyIds) throws BalanceTrackerException{

        try {

            // Business logic error handling

            if (this.getFlavor(_flavorId)!= null){
                throw new BalanceTrackerException("Cannot create Flavor, Flavor Id is already taken", 500);
            }

            if (_quorum > _keyIds.size()){
                throw new BalanceTrackerException("Cannot create Flavor, quorum is bigger than the number of key ids", 500);
            }

            // checking if keys id-s exist
            KeyBL keyBL = new KeyBL(this.getStub());
            for (String _id : _keyIds){
                if (keyBL.getKey(_id) == null){
                    throw new BalanceTrackerException("Referred input key does not exist at the createFlavor call of the Flavor service, keyID: " + _id);
                }
            }

            // create new flavor and write it into the blockchain

            Flavor flavorToStore = new Flavor(_flavorId, _isFungible, _tags, _quorum,  _keyIds);

            this.getStub().putState(_flavorId, (new ObjectMapper()).writeValueAsBytes(flavorToStore));

            return flavorToStore;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * Update Flavor, only tags can be added
     *
     * @param  _flavorId arguments of the call
     * @param  tags arguments of the call
     * @return       chaincode response
     */
    Flavor updateFlavor(String _flavorId, List<String> tags) throws BalanceTrackerException {

        try {

            Flavor flavorToUpdate = this.getFlavor(_flavorId);

            if (flavorToUpdate == null){
                throw new BalanceTrackerException("Cannot update Flavor, Flavor Id is not found", 500);
            }

            flavorToUpdate.addTags(tags);

            this.getStub().putState(_flavorId, (new ObjectMapper()).writeValueAsBytes(flavorToUpdate));

            return flavorToUpdate;
        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }


    /**
     * Update account only tags can be updated
     *
     * Filter is implemented on the client side for release 1
     *
     * @param  filter arguments of the call
     * @return       chaincode response
     */
    List<Flavor> gueryFlavor(QueryFilter filter) throws BalanceTrackerException{
        throw new BalanceTrackerException("Filter is implemented only on the client side for release 1");
    }



}
