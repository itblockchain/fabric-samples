package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.List;
import java.util.ArrayList;

/**
 * Account model
 */
public class Account extends ModelBase  {

    protected String accountId;
    protected List<String> tags;
    protected Integer quorum;
    protected List<String> keyIds;

    /**
     * Constructor
     */
    public Account(String _accountId, List<String> _tags, Integer _quorum, List<String> _keyIds) {
        this.accountId = _accountId;
        this.tags = _tags;
        this.quorum = _quorum;
        this.keyIds = _keyIds;
    }

    /**
     * Constructor
     */
    public Account(String _accountId, List<String> _tags) {
        this(_accountId, _tags,1, new ArrayList<String>());
    }

    /**
     * Constructor
     */
    public Account(String _accountId, Integer _quorum, List<String> _keyIds) {
        this(_accountId, new ArrayList<String>(), _quorum, _keyIds);
    }

    /**
     * Constructor
     */
    public Account(String _accountId) {
         this(_accountId, new ArrayList<String>(), 1, new ArrayList<String>());
    }

    /**
     * Getters
     */
    public String getAccountId() {
        return accountId;
    }

    public List<String> getTags() {
        return tags;
    }

    public Integer getQuorum() {
        return quorum;
    }

    public List<String> getKeyIds() {
        return keyIds;
    }

    /**
     * Factory function
     */
    public static Account createAccount(String accountString) throws BalanceTrackerException {

        try {

            JsonObject jsonObject = new JsonParser().parse(accountString).getAsJsonObject();

            String accountId = jsonObject.get("accountId").getAsString();
            Integer quorum = jsonObject.get("quorum").getAsInt();
            List<String> keyIds = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("keyIds"));
            List<String> tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("tags"));

            Account act = new Account(accountId,tags, quorum, keyIds);
            return act;
        }
        catch (Throwable ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * rewriting tags
     */
    public void setTags(List<String> _tags){
        this.tags = _tags;
    }

    /**
     * adding tags
     */
    public void addTags(List<String> _tags){
        this.tags.addAll(_tags);
    }



}
