package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * Master service for the general general Balance Tracker services
 */
public class MasterService extends ServiceBase  {

    /**
     * Master service logic constructor
     * Chaincode stub has to be initialized
     */
    public MasterService(ChaincodeStub _stub) {
        this.stub = _stub;
    }


    /**
     * Getting the version information of BalanceTracker
     * @return       chaincode response containing the version information
     */
    public Chaincode.Response getVersion(List<String> args) {

        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0", ""));

        try {

            String version = this.getVersion();

            if(!checkString(version))
                return newErrorResponse(responseError("Error at reading the version number", ""));

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(version)));

        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    /**
     * Getting the author information of BalanceTracker
     * @return       chaincode response containing the author information
     */
    public Chaincode.Response getAuthor(List<String> args) {

        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0", ""));

        try {

            String author = this.getAuthor();

            if(!checkString(author))
                return newErrorResponse(responseError("Error at reading the author information", ""));

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(author)));

        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    /**
     * Getting the author description of BalanceTracker
     * @return       chaincode response containing the description information
     */
    public Chaincode.Response getDescription(List<String> args) {

        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0", ""));

        try {

            String description = this.getDescription();

            if(!checkString(description))
                return newErrorResponse(responseError("Error at reading the description information", ""));

            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(description)));

        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

}
