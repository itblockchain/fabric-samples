package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Flavor;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

import java.util.List;

public class AccountBL extends BusinessLogicBase {

    public AccountBL(ChaincodeStub _stub){
        this.stub = _stub;
    }

    /**
     * Getting account by account id
     * only internally
     *
     * @param  accountId arguments of the call
     * @return       chaincode response
     */
    Account getAccount(String accountId) throws BalanceTrackerException  {

        try {

            String accountString = this.getStub().getStringState(accountId);

            if (accountString == null)
                return null;

            if(!checkString(accountString))
                return null;

            Account account = Account.createAccount(accountString);

            return account;

        }
        catch (Throwable ex){
            throw new BalanceTrackerException(ex);
        }
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
    Account createAccount(String _accountId, List<String> _tags, Integer _quorum, List<String> _keyIds) throws BalanceTrackerException {

        try {

            // Business logic error handling

            if (this.getAccount(_accountId)!= null){
                throw new BalanceTrackerException("Cannot create Account, Account Id is already taken", 500);
            }

            if (_quorum > _keyIds.size()){
                throw new BalanceTrackerException("Cannot create Account, quorum is bigger than the number of key ids", 500);
            }

            // checking if keys id-s exist
            KeyBL keyBL = new KeyBL(this.getStub());
            for (String _id : _keyIds){
                if (keyBL.getKey(_id) == null){
                    throw new BalanceTrackerException("Referred input key does not exist at the createAccount call of the Account service, keyID: " + _id);
                }
            }

            // create new Account and write it into the blockchain

            Account accountToStore = new Account(_accountId, _tags, _quorum,  _keyIds);

            this.getStub().putState(_accountId, (new ObjectMapper()).writeValueAsBytes(accountToStore));

            return accountToStore;

        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * Update account only tags can be updated
     *
     * @param  _accountId arguments of the call
     * @param  _tags arguments of the call*
     * @return       chaincode response
     */
    Account updateAccount(String _accountId, List<String> _tags) throws BalanceTrackerException  {

        try {

            Account accountToUpdate = this.getAccount(_accountId);

            if (accountToUpdate == null){
                throw new BalanceTrackerException("Cannot update Account, Account Id is not found", 500);
            }

            accountToUpdate.addTags(_tags);

            this.getStub().putState(_accountId, (new ObjectMapper()).writeValueAsBytes(accountToUpdate));

            return accountToUpdate;
        }
        catch (Exception ex){
            throw new BalanceTrackerException(ex);
        }
    }

    /**
     * Update account only tags can be updated
     * it is implemented on client side for Release1
     *
     * @param  args arguments of the call
     * @return       chaincode response
     */
    List<Account> gueryAccount(QueryFilter filter) throws BalanceTrackerException {
        throw new BalanceTrackerException("Filter is implemented on the client side for Release1");

 /*       for(QueryFilterElement filterElement: filter.getFilterList()){
            if (filterElement.getKey().equals("Account ID")){

            }
            if (filterElement.getKey().equals("Tags")){

            }
            else {

                // non consitent:
                // 1. best effort : no error what works works
                // 2. error at non consitency
            }

        }

*/
    }

}
