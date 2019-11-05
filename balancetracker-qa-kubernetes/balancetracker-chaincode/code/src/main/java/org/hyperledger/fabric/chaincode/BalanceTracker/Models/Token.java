package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Token model
 */
public class Token extends ModelBase  {

    protected String tokenId;
    protected String tokenCode;
    protected Double amount;
    protected String flavorId;
    protected String accountId;
    protected HashMap<String,String> tags;

    /**
     * Default constructor, due to the TokenEx object
     */
    public Token(){
        this.modelType = "Token";
    }

    /**
     * Constructor
     */
    public Token(String _tokenId, String _tokenCode, Double _amount, String _flavorId, String _accountId, HashMap<String,String> _tags){

        tokenId = _tokenId;
        tokenCode = _tokenCode;
        amount = _amount;
        flavorId = _flavorId;
        accountId = _accountId;
        tags = _tags;
        this.modelType = "Token";
    }

    /**
     * Getters
     */
    public String getTokenId(){
        return tokenId;
    }

    public Double getAmount(){
        return amount;
    }

    public String getFlavorId(){
        return flavorId;
    }

    public String getTokenCode(){return this.tokenCode; }

    public String getAccountId(){
        return accountId;
    }

    public HashMap<String,String> getTags(){
        if(tags == null)
            return new HashMap<String,String>();
        return tags;
    }

    /**
     * Factory function
     */
    public static Token createToken(String tokenString) throws Token.DeserializationException {

        try {

            Gson gson = new Gson();
            Token token = gson.fromJson(tokenString, Token.class);

            return token;
        }
        catch (Exception _ex){
            throw new Token.DeserializationException("There is a problem at reading out token from the blockchain ",  _ex);
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
     * update amount
     */
    public void updateAmount(Double _amount){
        this.amount = _amount;
    }


    /**
     * EXCEPTIONS
     */

    /**
     * DeserializationException
     */
    public static class DeserializationException extends BalanceTrackerException
    {
        protected Integer code = 5056;

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
        protected Integer code = 5066;

        public SerializationException(String msg, Exception _ex) {
            super(msg, _ex);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }


}
