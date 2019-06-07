package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.LogLevelEnum;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * Services related to Keys
 */
public class KeyService extends ServiceBase {

    /**
     * Key service logic constructor
     * Chaincode stub has to be initialized
     */
    public KeyService(ChaincodeStub _stub) {
        this.stub = _stub;
    }


    /**
     * Creating key
     * Key id has to be externally generated
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response createKey(List<String> args) {

        // INPUT PARAMETER VALIDATION
        // 0 - keyId

        String keyId;

        try {

            if (args.size() != 1)
                return newErrorResponse(responseError("Incorrect number of arguments, expecting exactly 1", "500"));

            if (!checkString(args.get(0)))
                return newErrorResponse(responseError("Invalid argument, keyId does not have a string format", "500"));

            keyId = args.get(0);

        } catch (Throwable e) {
            logger.Log("Error at converting the input parameters ", LogLevelEnum.INFO, this);
            logger.LogError(e, this);
            return newErrorResponse(responseError(e.getMessage(), "500"));
        }

        // Business logic

        try {

            KeyBL keyBL = new KeyBL(this.stub);
            Key retKey = keyBL.createKey(keyId);

            logger.Log("KeyService createKey call succeeded, key : " + retKey.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse(responseSuccess("Key created"));

        } catch(Throwable e){

            if (e instanceof BalanceTrackerException) {
                String errorMessage = e.getMessage();
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException) e).getErrorCode().toString()));
            }
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

    /**
     * Getting key by key id
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response getKey(List<String> args) {

        // input parameter validation
        // 0 - keyId

        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", "500"));

        String keyId = args.get(0);

        if (!checkString(keyId))
            return newErrorResponse(responseError("Invalid argument", "500"));

        try {

            KeyBL keyBL = new KeyBL(this.getStub());
            Key retKey = keyBL.getKey(keyId);

            if(retKey == null)
                return newErrorResponse(responseError("Nonexistent key", "500"));

            logger.Log("KeyService getKey call succeeded, key : " + retKey.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(retKey.toJSONString())));

        } catch(Throwable e){

            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

}
