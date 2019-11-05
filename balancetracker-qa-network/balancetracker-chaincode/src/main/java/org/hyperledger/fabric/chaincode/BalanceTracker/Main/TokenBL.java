package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class TokenBL extends BusinessLogicBase implements TokenBLInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(TokenBL.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Constructor
     */
    public TokenBL(ChaincodeStub _stub) throws TokenBL.StubIsNullException {
        if (_stub == null){
            throw new TokenBL.StubIsNullException("Stub can not be null at initializing the token service");
        }
        this.stub = _stub;
     }

    /**
     * Getting token by token id
     *
     * @param  tokenId arguments of the call
     * @return       Token response
     */
    public Token getToken(String tokenId) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getToken()");
        logger.finer("Parameters : tokenId : " + tokenId);
        logger.finer("transaction id : " + this.getStub().getTxId());

        String tokenString = this.getStringState(tokenId, TOKEN);

            if (tokenString == null)
                return null;

            if(!checkString(tokenString))
                return null;

            Token token = Token.createToken(tokenString);

        logger.exiting(this.getClass().getSimpleName(), "getToken()");
        logger.finer("result : " + token.toJSONString());

        return token;
    }

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
    public Token createToken(String _tokenId, String _tokenCode, Double _amount, String _flavorId, String _accountId, HashMap<String,String> _tags)  throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "createToken()");
        logger.finer("Parameters : tokenId" + _tokenId +
                "\n tokenCode : " + _tokenCode +
                "\n amount : " + _amount.toString() +
                "\n flavorId : " + _flavorId +
                "\n accountId : " + _accountId);

            // Business logic error handling
            // Tokenid error handling
            if ((_tokenId == null) || (_tokenId.length() == 0) ){
                throw new TokenBL.ParameterIsNullException("Cannot create Token, TokenId is null");
            }

            if (this.getToken(_tokenId) != null){
                throw new TokenBL.IdTakenException("Cannot create Token, Token Id is already taken");
            }

            // FlavorId error handling
            if ((_flavorId == null) || (_flavorId.length() == 0)){
                throw new TokenBL.ParameterIsNullException("Cannot create Token, FlavorId is null");
            }

            FlavorBL flavorBL = new FlavorBL(this.getStub());
            Flavor flavor = flavorBL.getFlavor(_flavorId);

            if (flavor == null){
                throw new TokenBL.NotExistException("Cannot create Token, referred FlavorId does not exist");
            }

            // AccountId error handling
            if ((_accountId == null) || (_accountId.length() == 0)){
                throw new TokenBL.ParameterIsNullException("Cannot create Token, AccountId is null");
            }

            AccountBL accountBL = new AccountBL(this.getStub());

            if (accountBL.getAccount(_accountId) == null){
                throw new TokenBL.NotExistException("Cannot create Account, referred AccountId does not exist");
            }

            // Non-fungible tokens special case error handling

            // There can be only one non-fungible token issued
            if ((flavor.isFungible() == false) && (_amount != 1)){
                throw new TokenBL.ParameterNotCompatibleException("From non-fungible tokens only one can be issued");
            }

            // Non fungible tokens should implement a code
            if ((flavor.isFungible() == false) && ((_tokenId == null) || (_tokenId.length() == 0))){
                throw new TokenBL.ParameterNotCompatibleException("Code can not be empty for non-fungible tokens");
            }

            // create new Token and write it into the blockchain

            Token tokenToStore = new Token(_tokenId,_tokenCode,_amount,_flavorId, _accountId, _tags);

            try {

                this.putState(_tokenId, (new ObjectMapper()).writeValueAsBytes(tokenToStore), TOKEN);
                this.getStub().setEvent(Constants.EventNames.TOKEN_ISSUED, (new ObjectMapper()).writeValueAsBytes(tokenToStore));

            } catch (JsonProcessingException _ex){
                throw new Token.SerializationException("Error at writing Token into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "createToken()");
        logger.finer("result : " + tokenToStore.toJSONString());

        return tokenToStore;
    }


    /**
     * Updating token by adding tags to the token
     *
     * @param  _tokenId arguments of the call
     * @param  _tags arguments of the call
     * @return       chaincode response
     */
    public Token updateToken(String _tokenId, HashMap<String,String> _tags)  throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "updateToken()");
        logger.finer("Parameters : tokenId : " + _tokenId);

        Token tokenToUpdate = this.getToken(_tokenId);

            if (tokenToUpdate == null){
                throw new TokenBL.NotExistException("Cannot update Token, Token Id is not found");
            }

            tokenToUpdate.addTags(_tags);

            try {

                this.putState(_tokenId, (new ObjectMapper()).writeValueAsBytes(tokenToUpdate), TOKEN);
                this.getStub().setEvent(Constants.EventNames.TOKEN_UPDATED, (new ObjectMapper()).writeValueAsBytes(tokenToUpdate));

            } catch (JsonProcessingException _ex){
                throw new Token.SerializationException("Error at writing Token into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "updateToken()");
        logger.finer("result : " + tokenToUpdate.toJSONString());

        return tokenToUpdate;
    }


    /**
     * Getting token by token id
     *
     * @param  tokenId arguments of the call
     * @return       chaincode response
     */
    public TokenEx getTokenEx(String tokenId) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getTokenEx()");
        logger.finer("Parameters : tokenId : " + tokenId);

        String tokenString = this.getStringState(tokenId, TOKEN);

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
                throw new TokenBL.NotExistException("Data inconsistency in Token, referred flavor id is not found");
            }
            HashMap<String,String> flavorTags = flavor.getTags();

            // getting account tags
            AccountBL accountBL = new AccountBL(this.getStub());
            Account account = accountBL.getAccount(token.getAccountId());

            if (account == null) {
                throw new TokenBL.NotExistException("Data inconsistency in Token, referred account id is not found");
            }
            HashMap<String,String> accountTags = account.getTags();

            // creating extended token
            TokenEx tokenEx = new TokenEx(token,flavorTags,accountTags);

        logger.exiting(this.getClass().getSimpleName(), "getTokenEx()");
        logger.finer("result : " + tokenEx.toJSONString());

        return tokenEx;
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
    public Token retireToken(String _tokenId, Double _amount)  throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "retireToken()");
        logger.finer("Parameters : tokenId" + _tokenId +
                "\n amount : " + _amount.toString());

        Token tokenToUpdate = this.getToken(_tokenId);

            if (tokenToUpdate == null){
                throw new TokenBL.NotExistException("Cannot retire Token, Token Id is not found");
            }

            if (tokenToUpdate.getAmount() < _amount){
                throw new TokenBL.ParameterNotCompatibleException("Cannot retire Token, Token doesnt have enough balance");
            }

            // delete state
            if (tokenToUpdate.getAmount() == _amount){

                this.getStub().delState(_tokenId);

            }
            // update state
            else {

                tokenToUpdate.updateAmount(tokenToUpdate.getAmount() -  _amount);

                try {

                this.putState(_tokenId, (new ObjectMapper()).writeValueAsBytes(tokenToUpdate), TOKEN);
                this.getStub().setEvent(Constants.EventNames.TOKEN_RETIRED, (new ObjectMapper()).writeValueAsBytes(tokenToUpdate));

                } catch (JsonProcessingException _ex){
                    throw new Token.SerializationException("Error at writing Token into the ledger", _ex);
                }

            }

        logger.exiting(this.getClass().getSimpleName(), "retireToken()");
        logger.finer("result : " + tokenToUpdate.toJSONString());

            return tokenToUpdate;
        }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Input is not a string
     */
    public static class ParameterIsNullException extends InputParameterInvalidException
    {
        protected Integer code = 5028;

        public ParameterIsNullException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Input can not be converted
     */
    public static class ParameterNotCompatibleException extends InputParameterInvalidException
    {
        protected Integer code = 5038;

        public ParameterNotCompatibleException(String msg) {
            super(msg);
        }

        public ParameterNotCompatibleException(String msg, Exception _cause) {
            super(msg, _cause);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Account does not exsit -> can not be queried, can not be updated
     */
    public static class NotExistException extends InputParameterInvalidException
    {
        protected Integer code = 5048;

        public NotExistException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Initialization error : Stub is null
     */
    public static class StubIsNullException extends BalanceTrackerException
    {
        protected Integer code = 5098;

        public StubIsNullException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Id is already taken
     */
    public static class IdTakenException extends BalanceTrackerException
    {
        protected Integer code = 5088;

        public IdTakenException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}
