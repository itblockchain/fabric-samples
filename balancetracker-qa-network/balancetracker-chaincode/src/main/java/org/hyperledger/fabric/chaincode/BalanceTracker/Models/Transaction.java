package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Transaction model
 */
public class Transaction extends ModelBase {

    protected String transactionId;
    protected Integer sequence;
    protected long timestamp;
    protected HashMap<String,String> tags;
    protected List<String> actionIds;

    public Transaction(String _transactionId,Integer _sequence,long _timestamp, HashMap<String,String> _tags, List<String>  _actionIds){

       transactionId = _transactionId;
       sequence = _sequence;
       timestamp = _timestamp;
       tags = _tags;
       actionIds = _actionIds;
       this.modelType = "Transaction";

    }

    /**
     * Getters
     */
    public String getTransactionId(){
        return transactionId;
    }

    public Integer getSequence(){
        return sequence;
    }

    public long getTimestamp(){
        return  timestamp;
    }

    public Date getTimestampAsDate(){
        Date date = new Date(this.timestamp);
        return  date;
    }

    public HashMap<String,String> getTags() {
        if(tags == null)
            return new HashMap<String,String>();
        return tags;
    }

    public List<String> getActionIds(){
        if(actionIds == null)
            return new ArrayList<String>();
        return actionIds;
    }

    /**
     * Factory function
     */
    public static Transaction createTransaction(String transactionString) throws Transaction.DeserializationException {

        try {

            Gson gson = new Gson();
            Transaction transaction = gson.fromJson(transactionString, Transaction.class);

            return transaction;
        }
        catch (Exception _ex){
            throw new Transaction.DeserializationException("There is a problem at reading out transaction from the blockchain ",  _ex);
        }
    }

    /**
     * EXCEPTIONS
     */

    /**
     * DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5057;

        public DeserializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

    /**
     * SerializationException
     */
    public static class SerializationException extends BalanceTrackerException
    {
        protected Integer code = 5067;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

}
