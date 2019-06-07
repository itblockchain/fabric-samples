package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.LogLevelEnum;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * Services related to Flavors
 */
public class FlavorService extends ServiceBase {

    /**
     * FlavorService logic constructor
     * Chaincode stub has to be initialized
     */
    public FlavorService(ChaincodeStub _stub) {
        this.stub = _stub;
    }


    /**
     * Creating flavor
     * Flavor id has to be externally generated
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response createFlavor(List<String> args) {

        // INPUT PARAMETER VALIDATION
        // 0 - flavorId
        // 1 - isFungible
        // 2 - quorum
        // 3 - tags
        // 4 - keyIds

        String flavorId;
        boolean isFungible;
        int quorum;
        List<String> tags;
        List<String> keyIds;

        try {

            if (args.size() != 5)
                return newErrorResponse(responseError("Incorrect number of arguments, expecting exactly 5", "500"));

            if (!checkString(args.get(0)))
                return newErrorResponse(responseError("Invalid argument, flavorId does not have a string format", "500"));

            if (!checkString(args.get(1)))
                return newErrorResponse(responseError("Invalid argument, isFungible does not have a string format", "500"));

            if (!checkString(args.get(2)))
                return newErrorResponse(responseError("Invalid argument, quorum does not have a string format", "500"));

            if (!checkString(args.get(3)))
                return newErrorResponse(responseError("Invalid argument, tags does not have a string format", "500"));

            if (!checkString(args.get(4)))
                return newErrorResponse(responseError("Invalid argument, keyIds does not have a string format", "500"));

            flavorId = args.get(0);
            isFungible = Boolean.parseBoolean(args.get(1));
            quorum = Integer.parseInt(args.get(2));
            tags = JSONHelper.createArrayFromString(args.get(3));
            keyIds = JSONHelper.createArrayFromString(args.get(4));

        } catch (Throwable e) {
            logger.Log("Error at converting the input parameters ", LogLevelEnum.INFO, this);
            logger.LogError(e, this);
            return newErrorResponse(responseError(e.getMessage(), "500"));
        }

        // Business Logic

        try {

            FlavorBL flavorBL = new FlavorBL(this.stub);
            Flavor retFlavor = flavorBL.createFlavor(flavorId, isFungible, tags, quorum, keyIds);

            logger.Log("FlavorService createFlavor call succeeded, key : " + retFlavor.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse(responseSuccess("Flavor created"));

        } catch(Throwable e){
            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }


    /**
     * Getting flavor by flavor id
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response getFlavor(List<String> args) {

        // input parameter validation - strict
        // 0 - flavorId

        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", "500"));

        if (!checkString(args.get(0)))
            return newErrorResponse(responseError("Invalid argument", "500"));


        String flavorId = args.get(0);

        try {

            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor retFlavor = flavorBL.getFlavor(flavorId);

            if(retFlavor == null)
                return newErrorResponse(responseError("Nonexistent key", "500"));

            logger.Log("FlavorService getFlavor call succeeded, key : " + retFlavor.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(retFlavor.toJSONString())));

        } catch(Throwable e){
            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

    /**
     * Update flavor specified by flavor id
     * Only tags can be updated
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response updateFlavor(List<String> args) {

        // INPUT PARAMETER VALIDATION
        // 0 - keyId
        // 1 - isFungible
        // 2 - quorum
        // 3 - tags
        // 4 - keyIds

        String flavorId;
        List<String> tags;

        try {

            if (args.size() != 2)
                return newErrorResponse(responseError("Incorrect number of arguments, expecting exactly 1", "500"));

            if (!checkString(args.get(0)))
                return newErrorResponse(responseError("Invalid argument", "500"));

            if (!checkString(args.get(1)))
                return newErrorResponse(responseError("Invalid argument", "500"));

            flavorId = args.get(0);
            tags = JSONHelper.createArrayFromString(args.get(1));

        } catch (Throwable e) {
            logger.Log("Error at converting the input parameters ", LogLevelEnum.INFO, this);
            logger.LogError(e, this);
            return newErrorResponse(responseError(e.getMessage(), "500"));
        }

        // Business Logic

        try {

            FlavorBL flavorBL = new FlavorBL(this.stub);
            Flavor retFlavor = flavorBL.updateFlavor(flavorId, tags);

            logger.Log("FlavorService updateFlavor call succeeded, key : " + retFlavor.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse(responseSuccess("Flavor sucessfully updated"));

        } catch(Throwable e){
            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

    /**
     * Query flavors by a spefified filter string
     *
     * Filter is implemented on the client side for release 1
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response queryFlavor(List<String> args) {
        return null;
    }

}
