package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

import java.util.HashMap;
import java.util.List;

/**
 * Action model
 */
public class Action extends ModelBase  {

    protected String actionId;
    protected ActionTypeEnum type;
    protected Double amount;
    protected long timestamp;
    protected String transactionId;
    protected String flavorId;
    protected String sourceAccountId;
    protected String destinationAccountId;
    protected String snapshotId;
    protected List<String> params;
    protected HashMap<String,String> tags;

    /**
     * Constructor - minimal
     */
    public Action(String _actionId){

        actionId = _actionId;
        this.modelType = "Action";
    }


    /**
     * Constructor - without transaction id
     */
    public Action(String _actionId, ActionTypeEnum _type,Double _amount,long _timestamp, String _flavorId, String _sourceAccountId, String _destinationAccountId,String _snapshotId,List<String> _params, HashMap<String,String> _tags){
        this.actionId = _actionId;
        this.type = _type;
        this.amount = _amount;
        this.timestamp = _timestamp;
        this.flavorId = _flavorId;
        this.sourceAccountId = _sourceAccountId;
        this.destinationAccountId = _destinationAccountId;
        this.snapshotId = _snapshotId;
        this.params = _params;
        this.tags = _tags;
        this.modelType = "Action";
    }

    /**
     * Constructor - with transaction id
     */
    public Action(String _actionId, ActionTypeEnum _type,Double _amount, long _timestamp, String _transactionId, String _flavorId, String _sourceAccountId, String _destinationAccountId,String _snapshotId,List<String> _params, HashMap<String,String> _tags){
        this.actionId = _actionId;
        this.type = _type;
        this.amount = _amount;
        this.timestamp = _timestamp;
        this.transactionId = _transactionId;
        this.flavorId = _flavorId;
        this.sourceAccountId = _sourceAccountId;
        this.destinationAccountId = _destinationAccountId;
        this.snapshotId = _snapshotId;
        this.params = _params;
        this.tags = _tags;
        this.modelType = "Action";
    }


    /**
     * Getters
     */
    public String getActionId (){
        return actionId;
    }

    public ActionTypeEnum getType(){
        return type;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getTransactionId(){
        return transactionId;
    }

    public Double getAmount(){
        return amount;
    }

    public String getFlavorId(){
        return flavorId;
    }

    public String getSourceAccountId(){
        return sourceAccountId;
    }

    public String getDestinationAccountId(){
        return this.destinationAccountId;
    }

    public List<String> getParams(){
        return params;
    }

    public String getSnapshotId(){
        return snapshotId;
    }

    public HashMap<String,String> getTags(){
        return tags;
    }

    /**
     * Setters
     * Transaction id might be set afterwards
     */
    public void setTransactionId(String _transactionId){
        this.transactionId = _transactionId;
    }

    /**
     * Create action
     */
    public static Action createAction(String actionString) throws Action.DeserializationException {

        try {

            Gson gson = new Gson();
            Action act = gson.fromJson(actionString, Action.class);

            return act;
         }
         catch (Exception _ex){
            throw new DeserializationException("There is a problem at reading out the action from the blockchain ",  _ex);
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
        protected Integer code = 5052;

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
        protected Integer code = 5062;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

}
