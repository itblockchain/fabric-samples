package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

public class TransactionBL extends BusinessLogicBase {

    /**
     * TokenService logic constructor
     * Chaincode stub has to be initialized
     */
    public TransactionBL(ChaincodeStub _stub){
        this.stub = _stub;
    }

    /**
     * Getting transaction by transaction id
     *
     * @param  transactionId arguments of the call
     * @return       chaincode response
     */
    Transaction getTransaction(String transactionId) throws BalanceTrackerException {

        try {

            String transactionString = this.getStub().getStringState(transactionId);

            if (transactionString == null)
                return null;

            if(!checkString(transactionString))
                return null;

            Transaction tr = Transaction.createTransaction(transactionString);

            return tr;
        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * Creating a new token
     *
     * @param  _transactionId arguments of the call
     * @param  _sequence arguments of the call
     * @param  _timestamp arguments of the call
     * @param  _tags arguments of the call
     * @param  _actionIds arguments of the call
     * @return       chaincode response
     */
    Transaction createTransaction(String _transactionId, Integer _sequence, List<String> _tags, List<String> _actionIds)  throws BalanceTrackerException  {

        try{

            // Business logic error handling
            // TransactionId error handling
            if ((_transactionId == null) || (_transactionId.length() == 0) ){
                throw new BalanceTrackerException("Cannot create Transaction, Transaction Id is null", 500);
            }

            if (this.getTransaction(_transactionId) != null){
                throw new BalanceTrackerException("Cannot create Transaction, Transaction Id is already taken", 500);
            }

            // checking if actionIds exist

            // checking if action keys id references exist
            // at transaction creation, the action id-s are still not available

 //           ActionBL actionBL = new ActionBL(this.getStub());
 //           for (String _id : _actionIds){
 //               if (actionBL.getAction(_id) == null){
 //                   throw new BalanceTrackerException("Referred action id does not exist at the createTransaction call of the Transaction service, actionID: " + _id);
 //               }
 //           }

            // creating the timestamp at creation
            Integer _timestamp = (int) (System.currentTimeMillis() / 1000L);

            // create new Transaction and write it into the blockchain
            Transaction transactionToStore = new Transaction(_transactionId,_sequence,_timestamp, _tags,_actionIds);

            this.getStub().putState(_transactionId, (new ObjectMapper()).writeValueAsBytes(transactionToStore));

            return transactionToStore;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }


}
