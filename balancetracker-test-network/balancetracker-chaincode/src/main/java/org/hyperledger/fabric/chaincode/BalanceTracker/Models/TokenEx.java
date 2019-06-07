package org.hyperledger.fabric.chaincode.BalanceTracker.Models;

import java.util.List;

/**
 * Extended token model to refer the related tags of account and flavor
 * used only reading out information
 */
public class TokenEx extends Token {

    protected List<String> flavorTags;
    protected List<String> accountTags;

    /**
     * Constructor
     */
    public TokenEx(Token _token, List<String> _flavorTags, List<String> _accountTags){
        this.tokenId = _token.tokenId;
        this.tokenCode = _token.tokenCode;
        this.amount = _token.amount;
        this.flavorId = _token.flavorId;
        this.accountId = _token.accountId;
        this.tags = _token.tags;
        this.flavorTags = _flavorTags;
        this.accountTags = _accountTags;
    }

}
