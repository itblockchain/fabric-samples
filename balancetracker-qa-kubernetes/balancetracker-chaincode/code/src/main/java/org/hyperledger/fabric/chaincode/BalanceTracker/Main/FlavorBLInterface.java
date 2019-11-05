package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Flavor;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract interface for the business logic of the flavor
 */
public interface FlavorBLInterface {

    /**
     * Getting a flavor by flavor id
     * only internally
     *
     * @param  flavorId arguments of the call
     * @return       chaincode response
     */
    Flavor getFlavor(String flavorId) throws BalanceTrackerException;

    /**
     * Creating a flavor by several parameters
     *
     * @param  _flavorId arguments of the call
     * @return       Flavor response
     */
    Flavor createFlavor(String _flavorId, boolean _isFungible, HashMap<String,String> _tags, Integer _quorum, List<String> _keyIds) throws BalanceTrackerException;

    /**
     * Update Flavor, only tags can be added
     *
     * @param  _flavorId arguments of the call
     * @param  tags arguments of the call
     * @return       chaincode response
     */
    Flavor updateFlavor(String _flavorId, HashMap<String,String> tags) throws BalanceTrackerException;


}
