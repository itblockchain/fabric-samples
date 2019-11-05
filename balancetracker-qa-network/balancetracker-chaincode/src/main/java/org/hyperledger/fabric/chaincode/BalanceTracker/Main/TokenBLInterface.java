package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Token;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.TokenEx;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract interface for the business logic of the token
 */
public interface TokenBLInterface {

    /**
     * Getting token by token id
     *
     * @param  tokenId arguments of the call
     * @return       Token response
     */
    Token getToken(String tokenId) throws BalanceTrackerException;

    /**
     * Creating a new token
     *
     * @param  _tokenId arguments of the call
     * @param  _tokenCode arguments of the call
     * @param  _amount arguments of the call
     * @param  _flavorId arguments of the call
     * @param  _accountId arguments of the call
     * @param  _tags arguments of the call
     * @return       chaincode response
     */
    Token createToken(String _tokenId, String _tokenCode, Double _amount, String _flavorId, String _accountId, HashMap<String,String> _tags)  throws BalanceTrackerException;

    /**
     * Updating token by adding tags to the token
     *
     * @param  _tokenId arguments of the call
     * @param  _tags arguments of the call
     * @return       chaincode response
     */
    Token updateToken(String _tokenId, HashMap<String,String> _tags)  throws BalanceTrackerException;

    /**
     * Getting token by token id
     *
     * @param  tokenId arguments of the call
     * @return       chaincode response
     */
    TokenEx getTokenEx(String tokenId) throws BalanceTrackerException;

    /**
     * Low level version of the retire function
     * An amount of token will be retired from the token
     * If: amount < balance -> update amount
     * If: amount == balance -> delete token
     * If: amount > balance -> error message
     *
     * @param  _tokenId arguments of the call
     * @param  _amount arguments of the call
     * @return       chaincode response
     */
    Token retireToken(String _tokenId, Double _amount)  throws BalanceTrackerException;


    }
