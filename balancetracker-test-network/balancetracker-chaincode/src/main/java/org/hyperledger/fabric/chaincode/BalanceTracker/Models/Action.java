package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

import java.util.Date;
import java.util.List;

/**
 * Action model
 */
public class Action extends ModelBase  {

    protected String actionId;
    protected ActionTypeEnum type;
    protected Integer amount;
    protected Integer timestamp;
    protected String transactionId;
    protected String flavorId;
    protected String sourceAccountId;
    protected String destinationAccountId;
    protected String snapshotId;
    protected List<String> params;
    protected List<String> tags;

    /**
     * Constructor - minimal
     */
    public Action(String _actionId){
        actionId = _actionId;
    }


    /**
     * Constructor - without transaction id
     */
    public Action(String _actionId, ActionTypeEnum _type,Integer _amount,Integer _timestamp, String _flavorId, String _sourceAccountId, String _destinationAccountId,String _snapshotId,List<String> _params, List<String> _tags){
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
    }

    /**
     * Constructor - with transaction id
     */
    public Action(String _actionId, ActionTypeEnum _type,Integer _amount,Integer _timestamp, String _transactionId, String _flavorId, String _sourceAccountId, String _destinationAccountId,String _snapshotId,List<String> _params, List<String> _tags){
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

    public Integer getTimestamp(){
        return timestamp;
    }

    public String getTransactionId(){
        return transactionId;
    }

    public Integer getAmount(){
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

    public String getSnapshotIs(){
        return snapshotId;
    }

    public List<String> getTags(){
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
    public static Action createAction(String actionString) throws BalanceTrackerException {

        try {

            JsonObject jsonObject = new JsonParser().parse(actionString).getAsJsonObject();

            String actionId = jsonObject.get("actionId").getAsString();
            ActionTypeEnum type = ActionTypeEnum.valueOf(jsonObject.get("type").getAsString());
            Integer amount = jsonObject.get("amount").getAsInt();
            Integer timestamp = jsonObject.get("timestamp").getAsInt();
            String transactionId = jsonObject.get("transactionId").getAsString();
            String flavorId = jsonObject.get("flavorId").getAsString();
            String sourceAccountId = jsonObject.get("sourceAccountId").getAsString();
            String destinationAccountId = jsonObject.get("destinationAccountId").getAsString();
            String snapshotId = jsonObject.get("snapshotId").getAsString();
            List<String> params = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("params"));
            List<String> tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("tags"));

            Action act = new Action(actionId,type,amount,timestamp,transactionId,flavorId,sourceAccountId,destinationAccountId,snapshotId,params,tags);
            return act;
         }
         catch (Throwable ex){
            throw new BalanceTrackerException(ex);
         }
     }

}
