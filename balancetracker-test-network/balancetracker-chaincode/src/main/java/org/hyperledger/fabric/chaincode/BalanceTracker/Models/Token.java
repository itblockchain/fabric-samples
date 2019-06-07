package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Token model
 */
public class Token extends ModelBase  {

    protected String tokenId;
    protected String tokenCode;
    protected Integer amount;
    protected String flavorId;
    protected String accountId;
    protected List<String> tags;

    /**
     * Default constructor, due to the TokenEx object
     */
    public Token(){}

    /**
     * Constructor
     */
    public Token(String _tokenId, String _tokenCode, Integer _amount, String _flavorId, String _accountId, List<String> _tags){

        tokenId = _tokenId;
        tokenCode = _tokenCode;
        amount = _amount;
        flavorId = _flavorId;
        accountId = _accountId;
        tags = _tags;
    }

    /**
     * Getters
     */
    public String getTokenId(){
        return tokenId;
    }

    public Integer getAmount(){
        return amount;
    }

    public String getFlavorId(){
        return flavorId;
    }

    public String getTokenCode(){return this.tokenCode; }

    public String getAccountId(){
        return accountId;
    }

    public List<String> getTags(){
        return tags;
    }

    /**
     * Factory function
     */
    public static Token createToken(String tokenString) throws BalanceTrackerException {

        try {

            JsonObject jsonObject = new JsonParser().parse(tokenString).getAsJsonObject();

            String tokenId = jsonObject.get("tokenId").getAsString();
            Integer amount = jsonObject.get("amount").getAsInt();
            String code = jsonObject.get("tokenCode").getAsString();
            String flavorId = jsonObject.get("flavorId").getAsString();
            String accountId = jsonObject.get("accountId").getAsString();
            List<String> tags = JSONHelper.convertJSONArrayToArray(jsonObject.getAsJsonArray("tags"));

            Token tkn = new Token(tokenId,code, amount, flavorId, accountId, tags);
            return tkn;
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

    /**
     * update amount
     */
    public void updateAmount(Integer _amount){
        this.amount = _amount;
    }


}
