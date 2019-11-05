package org.hyperledger.fabric.chaincode.BalanceTracker.Main;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;

import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Business logic for accounts
 */
public class AccountBL extends BusinessLogicBase implements AccountBLInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(AccountBL.class.getName());

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
    public AccountBL(ChaincodeStub _stub) throws AccountBL.StubIsNullException{

        if (_stub == null)
            throw new AccountBL.StubIsNullException("Stub can not be null at initializing the account service");
        this.stub = _stub;
    }

    /**
     * Getting account by account id
     * only internally
     *
     * @param  accountId arguments of the call
     * @return       chaincode response
     */
    public Account getAccount(String accountId) throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "getAccount()");
        logger.finer("Parameters : accountId : " + accountId);
        logger.finer("transaction id : " + this.getStub().getTxId());

        String accountString = this.getStringState(accountId, ACCOUNT);

        // null indicates that the account is not found that can be normal
        if (accountString == null)
            return null;

        // null indicates that the account is not found that can be normal
        if(!checkString(accountString))
            return null;

         Account account = Account.createAccount(accountString);

        logger.exiting(this.getClass().getSimpleName(), "getAccount()");
        logger.finer("returning: " + account.toJSONString());

        return account;
    }


    /**
     * Getting account by account id
     * only internally
     *
     * @param  _accountId arguments of the call
     * @param  _tags arguments of the call
     * @param  _quorum arguments of the call
     * @param  _keyIds arguments of the call
     **
     * @return       chaincode response
     */
    public Account createAccount(String _accountId, HashMap<String,String> _tags, Integer _quorum, List<String> _keyIds) throws BalanceTrackerException {

            logger.entering(this.getClass().getSimpleName(), "createAccount()");
            logger.finer("Parameters : accountId : " + _accountId +
                    "\n quorum : " + _quorum);

        // Business logic error handling

            if (this.getAccount(_accountId)!= null){
                throw new AccountBL.IdTakenException("Cannot create Account, Account Id is already taken");
            }

            if (_quorum > _keyIds.size()){
                throw new AccountBL.KeyNumberMismatchException("Cannot create Account, quorum is bigger than the number of key ids");
            }

            // checking if keys id-s exist
            KeyBL keyBL = new KeyBL(this.getStub());
            for (String _id : _keyIds){
                if (keyBL.getKey(_id) == null){
                    throw new AccountBL.NotExistException("Referred input key does not exist at the createAccount call of the Account service, keyID: " + _id);
                }
            }

            // create new Account and write it into the blockchain

            Account accountToStore = new Account(_accountId, _tags, _quorum,  _keyIds);

            try{
                this.putState(_accountId,(new ObjectMapper()).writeValueAsBytes(accountToStore), ACCOUNT );
                this.getStub().setEvent(Constants.EventNames.ACCOUNT_CREATED,(new ObjectMapper()).writeValueAsBytes(accountToStore));

            } catch (
                JsonProcessingException _ex){
                throw new Account.SerializationException("Error at writing Account into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "createAccount()");
        logger.finer("return : " + accountToStore.toJSONString());

        return accountToStore;
    }

    /**
     * Update account only tags can be updated
     *
     * @param  _accountId arguments of the call
     * @param  _tags arguments of the call*
     * @return       chaincode response
     */
    public Account updateAccount(String _accountId, HashMap<String,String> _tags) throws BalanceTrackerException  {

        logger.entering(this.getClass().getSimpleName(), "updateAccount()");
        logger.finer("Parameters : accountId : " + _accountId);

        Account accountToUpdate = this.getAccount(_accountId);

            if (accountToUpdate == null){
                throw new AccountBL.NotExistException("Cannot update Account, Account Id is not found :" + _accountId);
            }

            accountToUpdate.addTags(_tags);

            try{

                this.putState(_accountId,(new ObjectMapper()).writeValueAsBytes(accountToUpdate), ACCOUNT );
                this.getStub().setEvent(Constants.EventNames.ACCOUNT_UPDATED,(new ObjectMapper()).writeValueAsBytes(accountToUpdate));


            } catch (
                    JsonProcessingException _ex){
                throw new Account.SerializationException("Error at writing Account into the ledger", _ex);
            }

        logger.exiting(this.getClass().getSimpleName(), "updateAccount()");
        logger.finer("return : " + accountToUpdate.toJSONString());

        return accountToUpdate;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Initialization error : Stub is null
     */
    public static class StubIsNullException extends BalanceTrackerException
    {
        protected Integer code = 5091;

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
        protected Integer code = 5081;

        public IdTakenException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

    /**
     * quorum is not equal to the number of keys
     */
    public static class KeyNumberMismatchException extends InputParameterInvalidException
    {
        protected Integer code = 5071;

        public KeyNumberMismatchException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

    /**
     * Key does not exsit -> can not be queried, can not be updated
     */
    public static class NotExistException extends InputParameterInvalidException
    {
        protected Integer code = 5041;

        public NotExistException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }

    }

}
