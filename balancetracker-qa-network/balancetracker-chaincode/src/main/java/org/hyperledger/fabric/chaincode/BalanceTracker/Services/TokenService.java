package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.LogLevelEnum;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;

import java.util.List;

/**
 * Services related to Tokens
 */
public class TokenService extends ServiceBase {

    /**
     * TokenService logic constructor
     * Chaincode stub has to be initialized
     */
    public TokenService(ChaincodeStub _stub) {
        this.stub = _stub;
    }

    /**
     * Getting token with additional information by token id
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response getToken(List<String> args) {

        // input validation

        // input parameter validation - strict
        // 0 - flavorId

        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", "500"));

        if (!checkString(args.get(0)))
            return newErrorResponse(responseError("Invalid argument", "500"));

        String tokenId = args.get(0);

        try {

            TokenBL tokenBL = new TokenBL(this.getStub());
            TokenEx retToken = tokenBL.getTokenEx(tokenId);

            if(retToken == null)
                return newErrorResponse(responseError("Nonexistent key", "500"));

            logger.Log("TokenService getToken call succeeded, key : " + retToken.toString(), LogLevelEnum.INFO, this);

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(retToken.toJSONString())));

        } catch(Throwable e){

            if (e instanceof BalanceTrackerException)
                return newErrorResponse(responseError(e.getMessage(), ((BalanceTrackerException)e).getErrorCode().toString()));
            else
                return newErrorResponse(responseError(e.getMessage(), "500"));
        }
    }

    /**
     * Query tokens by a spefified filter string
     *
     * Filter is implemente on the client side for release 1
     *
     * @param  stub  Chaincode stub
     * @param  args arguments of the call
     * @return       chaincode response
     */
    public Chaincode.Response queryToken(List<String> args){
        return null;
    }

}
