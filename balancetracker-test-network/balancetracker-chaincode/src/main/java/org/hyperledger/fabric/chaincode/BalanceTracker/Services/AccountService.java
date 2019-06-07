package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.LogLevelEnum;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.QueryFilter;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * Services related to Accounts
 */
public class AccountService extends ServiceBase {

    /**
     * AccountService logic constructor
     * Chaincode stub has to be initialized
     */
    public AccountService(ChaincodeStub _stub) {
        this.stub = _stub;
    }


    /**
     * Creating new account
     * Account id has to be externally generated
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response createAccount(List<String> args) {

        // INPUT PARAMETER VALIDATION
        // 0 - accountId
        // 2 - quorum
        // 3 - tags
        // 4 - keyIds

        String accountId;
        int quorum;
        List<String> tags;
        List<String> keyIds;

        try {

            if (args.size() != 4)
                return newErrorResponse(responseError("Incorrect number of arguments, expecting exactly 5", "500"));

            if (!checkString(args.get(0)))
                return newErrorResponse(responseError("Invalid argument, flavorId does not have a string format", "500"));

            if (!checkString(args.get(1)))
                return newErrorResponse(responseError("Invalid argument, quorum does not have a string format", "500"));

            if (!checkString(args.get(2)))
                return newErrorResponse(responseError("Invalid argument, tags does not have a string format", "500"));

            if (!checkString(args.get(3)))
                return newErrorResponse(responseError("Invalid argument, keyIds does not have a string format", "500"));

            accountId = args.get(0);
            quorum = Integer.parseInt(args.get(1));
            tags = JSONHelper.createArrayFromString(args.get(2));
            keyIds = JSONHelper.createArrayFromString(args.get(3));

        } catch (Throwable e) {
            logger.Log("Error at converting the input parameters ", LogLevelEnum.INFO, this);
            logger.LogError(e, this);
            return newErrorResponse(responseError(e.getMessage(), "500"));
        }

        // Business Logic

        try {

            AccountBL accountBL = new AccountBL(this.stub);
            Account retAccount = accountBL.createAccount(accountId, tags, quorum, keyIds);

            logger.Log("AccountService createAccount call succeeded, key : " + retAccount.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse(responseSuccess("Account created"));

        } catch(Throwable e){
            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

    /**
     * Getting account by account id
     *
     * @param  args  Chaincode stub
     * @return       chaincode response
     */
    public Chaincode.Response getAccount(List<String> args) {
        // input parameter validation - strict
        // 0 - flavorId

        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", "500"));

        if (!checkString(args.get(0)))
            return newErrorResponse(responseError("Invalid argument", "500"));


        String accountId = args.get(0);

        try {

            AccountBL accountBL = new AccountBL(this.getStub());
            Account retAccount = accountBL.getAccount(accountId);

            if(retAccount == null)
                return newErrorResponse(responseError("Nonexistent key", "500"));

            logger.Log("FlavorService getAccount call succeeded, key : " + retAccount.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(retAccount.toJSONString())));

        } catch(Throwable e){
            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }


        /**
         * Update account specified by account id
         * Only tags can be updated
         *
         * @param  args arguments of the call
         * @return       chaincode response
         */
    public Chaincode.Response updateAccount(List<String> args) {
        // INPUT PARAMETER VALIDATION
        // 0 - keyId
        // 1 - quorum
        // 2 - tags
        // 3 - keyIds

        String accountId;
        List<String> tags;

        try {

            if (args.size() != 2)
                return newErrorResponse(responseError("Incorrect number of arguments, expecting exactly 1", "500"));

            if (!checkString(args.get(0)))
                return newErrorResponse(responseError("Invalid argument", "500"));

            if (!checkString(args.get(1)))
                return newErrorResponse(responseError("Invalid argument", "500"));

            accountId = args.get(0);
            tags = JSONHelper.createArrayFromString(args.get(1));

        } catch (Throwable e) {
            logger.Log("Error at converting the input parameters ", LogLevelEnum.INFO, this);
            logger.LogError(e, this);
            return newErrorResponse(responseError(e.getMessage(), "500"));
        }

        // Business Logic

        try {

            AccountBL accountBL = new AccountBL(this.stub);
            Account retAccount = accountBL.updateAccount(accountId, tags);

            logger.Log("AccountService updateAccount call succeeded, key : " + retAccount.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse(responseSuccess("Account sucessfully updated"));
        } catch(Throwable e){
            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

    /**
     * Query accounts by a spefified filter string
     *
     * it is implemented on client side for Release1
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response queryAccount(List<String> args) {
        return null;
    }

}
