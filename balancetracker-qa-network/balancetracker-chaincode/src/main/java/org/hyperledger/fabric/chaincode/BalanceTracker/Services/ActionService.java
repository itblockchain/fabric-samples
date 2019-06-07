package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.ActionTypeEnum;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.LogLevelEnum;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;

import java.util.List;

/**
 * Services related to Actions
 */
public class ActionService extends ServiceBase {

    /**
     * ActionService logic constructor
     * Chaincode stub has to be initialized
     */
    public ActionService(ChaincodeStub _stub) {
        this.stub = _stub;
    }


    /**
     * Getting action by action id
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response getAction(List<String> args) {

        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));

        String actionId = args.get(0);

        if (!checkString(actionId))
            return newErrorResponse(responseError("Invalid argument", ""));

        try {

            String actionString = stub.getStringState(actionId);

            if(!checkString(actionString))
                return newErrorResponse(responseError("Nonexistent key", ""));
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(actionString)));
        } catch(Throwable e){
            logger.LogError(e, this);
            return newErrorResponse(responseError(e.getMessage(), ""));
        }

    }

    /**
     * Query actions by a spefified filter string
     *
     * Implemented on client side for release 1
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response queryAction(List<String> args){
        return null;
    }

    /**
     * Create action
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    Chaincode.Response createAction(String _actionId, ActionTypeEnum _type, String _transactionId, String _flavorId, String _tags){
        return null;
    }

}
