package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Account model
 */
public class Account extends ModelBase  {

    protected String accountId;
    protected HashMap<String, String> tags;
    protected Integer quorum;
    protected List<String> keyIds;

    /**
     * Constructor
     */
    public Account(String _accountId, HashMap<String, String> _tags, Integer _quorum, List<String> _keyIds) {
        this.accountId = _accountId;
        this.tags = _tags;
        this.quorum = _quorum;
        this.keyIds = _keyIds;
        this.modelType = "Account";
    }

    /**
     * Constructor
     */
    public Account(String _accountId, HashMap<String, String> _tags) {
        this(_accountId, _tags,1, new ArrayList<String>());
    }

    /**
     * Constructor
     */
    public Account(String _accountId, Integer _quorum, List<String> _keyIds) {
        this(_accountId, new HashMap<String,String>(), _quorum, _keyIds);
    }

    /**
     * Constructor
     */
    public Account(String _accountId) {
         this(_accountId,new HashMap<String,String>(), 1, new ArrayList<String>());
    }

    /**
     * Getters
     */
    public String getAccountId() {
        return accountId;
    }

    public HashMap<String,String> getTags() {
        if (tags == null)
            return new HashMap<String,String>();
        return tags;
    }

    public Integer getQuorum() {
        return quorum;
    }

    public List<String> getKeyIds() {
         if (keyIds == null)
             return new ArrayList<String>();
        return keyIds;
    }

    /**
     * Factory function
     */
    public static Account createAccount(String accountString) throws DeserializationException {

        try {
              Gson gson = new Gson();
              Account act = gson.fromJson(accountString, Account.class);

              return act;
        }
        catch (Exception _ex){
            throw new Account.DeserializationException("There is a problem at reading out the account from the blockchain ",  _ex);
        }
    }

    /**
     * rewriting tags
     */
    public void setTags(HashMap<String,String> _tags){
        this.tags = _tags;
    }

    /**
     * adding tags
     */
    public void addTags(HashMap<String,String> _tags){
        this.tags.putAll(_tags);
    }


    /**
     * EXCEPTIONS
     */

    /**
     * DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5051;

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
        protected Integer code = 5061;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

}
