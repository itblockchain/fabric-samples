package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Key;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

public class TokenBL extends BusinessLogicBase {

    public TokenBL(ChaincodeStub _stub){
        this.stub = _stub;
    }

    /**
     * Getting token by token id
     *
     * @param  tokenId arguments of the call
     * @return       chaincode response
     */
    Token getToken(String tokenId) throws BalanceTrackerException {

        try {

            String tokenString = this.getStub().getStringState(tokenId);

            if (tokenString == null)
                return null;

            if(!checkString(tokenString))
                return null;

            Token token = Token.createToken(tokenString);

            return token;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * Creating a new token
     *
     * @param  _tokenId arguments of the call
     * @param  _code arguments of the call
     * @param  _amount arguments of the call
     * @param  _flavorId arguments of the call
     * @param  _accountId arguments of the call
     * @param  _tags arguments of the call
     * @return       chaincode response
     */
    Token createToken(String _tokenId, String _tokenCode, Integer _amount, String _flavorId, String _accountId, List<String> _tags)  throws BalanceTrackerException  {

        try{

            // Business logic error handling
            // Tokenid error handling
            if ((_tokenId == null) || (_tokenId.length() == 0) ){
                throw new BalanceTrackerException("Cannot create Token, TokenId is null", 500);
            }

            if (this.getToken(_tokenId) != null){
                throw new BalanceTrackerException("Cannot create Token, Token Id is already taken", 500);
            }

            // FlavorId error handling
            if ((_flavorId == null) || (_flavorId.length() == 0)){
                throw new BalanceTrackerException("Cannot create Token, FlavorId is null", 500);
            }

            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flavor = flavorBL.getFlavor(_flavorId);
            if (flavor == null){
                throw new BalanceTrackerException("Cannot create Token, referred FlavorId does not exist", 500);
            }

            // AccountId error handling
            if ((_accountId == null) || (_accountId.length() == 0)){
                throw new BalanceTrackerException("Cannot create Token, AccountId is null", 500);
            }

            AccountBL accountBL = new AccountBL(this.getStub());
            if (accountBL.getAccount(_accountId) == null){
                throw new BalanceTrackerException("Cannot create Account, referred AccountId does not exist", 500);
            }

            // Non-fungible tokens special case error handling

            // There can be only one non-fungible token issued
            if ((flavor.isFungible() == false) && (_amount != 1)){
                throw new BalanceTrackerException("From non-fungible tokens only one can be issued", 500);
            }

            // Non fungible tokens should implement a code
            if ((flavor.isFungible() == false) && ((_tokenId == null) || (_tokenId.length() == 0))){
                throw new BalanceTrackerException("Code can not be empty for non-fungible tokens", 500);
            }

            // create new Token and write it into the blockchain

            Token tokenToStore = new Token(_tokenId,_tokenCode,_amount,_flavorId, _accountId, _tags);

            this.getStub().putState(_tokenId, (new ObjectMapper()).writeValueAsBytes(tokenToStore));

            return tokenToStore;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }


    /**
     * Updating token by adding tags to the token
     *
     * @param  _tokenId arguments of the call
     * @param  _tags arguments of the call
     * @return       chaincode response
     */
    Token updateToken(String _tokenId, List<String> _tags)  throws BalanceTrackerException  {

        try {

            Token tokenToUpdate = this.getToken(_tokenId);

            if (tokenToUpdate == null){
                throw new BalanceTrackerException("Cannot update Token, Token Id is not found", 500);
            }

            tokenToUpdate.addTags(_tags);

            this.getStub().putState(_tokenId, (new ObjectMapper()).writeValueAsBytes(tokenToUpdate));

            return tokenToUpdate;
        }
            catch (Exception ex){
                throw new BalanceTrackerException(ex);
        }
    }


    /**
     * Getting token by token id
     *
     * @param  tokenId arguments of the call
     * @return       chaincode response
     */
    TokenEx getTokenEx(String tokenId) throws BalanceTrackerException {

        try {

            String tokenString = this.getStub().getStringState(tokenId);

            if (tokenString == null)
                return null;

            if(!checkString(tokenString))
                return null;

            // getting the base token
            Token token = Token.createToken(tokenString);

            // getting flavor tags
            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flavor = flavorBL.getFlavor(token.getFlavorId());
            if (flavor == null) {
                throw new BalanceTrackerException("Data inconsistency in Token, referred flavor id is not found", 500);
            }
            List<String> flavorTags = flavor.getTags();

            // getting account tags
            AccountBL accountBL = new AccountBL(this.getStub());
            Account account = accountBL.getAccount(token.getAccountId());
            if (account == null) {
                throw new BalanceTrackerException("Data inconsistency in Token, referred account id is not found", 500);
            }
            List<String> accountTags = account.getTags();

            // creating extended token
            TokenEx tokenEx = new TokenEx(token,flavorTags,accountTags);

            return tokenEx;
        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }

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
    Token retireToken(String _tokenId, Integer _amount)  throws BalanceTrackerException  {

        try {

            Token tokenToUpdate = this.getToken(_tokenId);

            if (tokenToUpdate == null){
                throw new BalanceTrackerException("Cannot retire Token, Token Id is not found", 500);
            }

            if (tokenToUpdate.getAmount() < _amount){
                throw new BalanceTrackerException("Cannot retire Token, Token doesnt have enough balance", 500);
            }

            // delete state
            if (tokenToUpdate.getAmount() == _amount){
                this.getStub().delState(_tokenId);
            }
            // update state
            else {
                tokenToUpdate.updateAmount(tokenToUpdate.getAmount() -  _amount);
                this.getStub().putState(_tokenId, (new ObjectMapper()).writeValueAsBytes(tokenToUpdate));
            }
            return tokenToUpdate;
        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }


}
