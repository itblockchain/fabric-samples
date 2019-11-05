package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Extended token model to refer the related tags of account and flavor
 * used only reading out information
 */
public class TokenEx extends Token {

    protected HashMap<String,String> flavorTags;
    protected HashMap<String,String> accountTags;

    /**
     * Constructor
     */
    public TokenEx(Token _token, HashMap<String,String> _flavorTags, HashMap<String,String> _accountTags){
        this.tokenId = _token.tokenId;
        this.tokenCode = _token.tokenCode;
        this.amount = _token.amount;
        this.flavorId = _token.flavorId;
        this.accountId = _token.accountId;
        this.tags = _token.tags;
        this.flavorTags = _flavorTags;
        this.accountTags = _accountTags;
        this.modelType = "Token";
    }

    /**
     * Getters
     */
    public HashMap<String,String> getFlavorTags(){
        if(flavorTags == null)
            return new HashMap<String,String>();
        return flavorTags;
    }

    public HashMap<String, String> getAccountTags(){
        if(accountTags == null)
            return new HashMap<String, String>();
        return accountTags;
    }

}
