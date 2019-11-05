package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * General interface for service routing
 */
public interface ServiceInterface {

    /**
     * Calling the main service
     */
     Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException;


    /**
     * If the service is allowed to run, considering the current indentity information of the chain
     */
    boolean isAllowedToRun(ChaincodeStub _stub) throws BalanceTrackerException;

    /**
     * If the service is configured to run
     */
    boolean isConfiguredToRun(ChaincodeStub _stub) throws BalanceTrackerException;


    /**
     * Dependency between services
     */
     List<String> requiredServiceNames();


}
