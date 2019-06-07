package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.Date;
import java.util.List;

/**
 * Transaction model
 */
public class Transaction extends ModelBase {

    protected String transactionId;
    protected Integer sequence;
    protected Integer timestamp;
    protected List<String> tags;
    protected List<String> actionIds;

    public Transaction(String _transactionId,Integer _sequence,Integer _timestamp, List<String> _tags, List<String>  _actionIds){

       transactionId = _transactionId;
       sequence = _sequence;
       timestamp = _timestamp;
       tags = _tags;
       actionIds = _actionIds;
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

    public Integer getTimestamp(){
        return  timestamp;
    }

    public Date getTimestampAsDate(){
        Date date = new Date(this.timestamp);
        return  date;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getActionIds(){
        return actionIds;
    }

    /**
     * Factory function
     */
    public static Transaction createTransaction(String transactionString) throws BalanceTrackerException {

        try {

            JsonObject jsonObject = new JsonParser().parse(transactionString).getAsJsonObject();

            String transactionId = jsonObject.get("transactionId").getAsString();
            Integer sequence = jsonObject.get("sequence").getAsInt();
            Integer timestamp = jsonObject.get("timestamp").getAsInt();
            List<String> actionIds = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("actionIds"));
            List<String> tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("tags"));

            Transaction tr = new Transaction(transactionId, sequence, timestamp, tags, actionIds);
            return tr;
        }
        catch (Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }
}
