package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Transaction;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract interface for the business logic of the transaction
 */
public interface TransactionBLInterface {

    /**
     * Getting transaction by transaction id
     *
     * @param  transactionId arguments of the call
     * @return       chaincode response
     */
    Transaction getTransaction(String transactionId) throws BalanceTrackerException;

    /**
     * Creating a new token
     *
     * @param  _transactionId arguments of the call
     * @param  _sequence arguments of the call
     * @param  _tags arguments of the call
     * @param  _actionIds arguments of the call
     * @return       chaincode response
     */
    Transaction createTransaction(String _transactionId, Integer _sequence, HashMap<String,String> _tags, List<String> _actionIds)  throws BalanceTrackerException;

    }
