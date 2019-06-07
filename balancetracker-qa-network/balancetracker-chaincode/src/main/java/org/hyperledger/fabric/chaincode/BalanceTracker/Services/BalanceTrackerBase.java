package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import java.lang.reflect.Executable;
import java.util.List;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

/**
 * Base ancestor class for different services and business logic
 */
public class BalanceTrackerBase extends ChaincodeBase {

    /**
     * versioning convention:
     * BTJ: Balance Tracker Java Implementation
     * Major version: 1.0 from go live
     * Minor version: increment at each master commit
     */
    protected String version = "BTJ_v0.4";
    protected String author = "Interticket";
    protected String description = "Balance Tracker implementation under Hyperledegr Fabric with Java chaincode";

    protected ChaincodeStub stub;

    protected ChaincodeStub getStub() throws BalanceTrackerException{
        if (stub == null){
            throw new BalanceTrackerException("Stub is null");
        }
        return stub;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    protected LoggerInterface logger;

    public BalanceTrackerBase(){
        logger = new ConsoleLogger(LogEnum.DEBUG);
    }

    protected class ChaincodeResponse {
        public String message;
        public String code;
        public boolean OK;

        public ChaincodeResponse(String message, String code, boolean OK) {
            this.code = code;
            this.message = message;
            this.OK = OK;
        }
    }

    protected String responseError(String errorMessage, String code) {
        try {
            return (new ObjectMapper()).writeValueAsString(new BalanceTrackerBase.ChaincodeResponse(errorMessage, code, false));
        } catch (Throwable e) {
            return "{\"code\":'" + code + "', \"message\":'" + e.getMessage() + " AND " + errorMessage + "', \"OK\":" + false + "}";
        }
    }

    protected String responseSuccess(String successMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new BalanceTrackerBase.ChaincodeResponse(successMessage, "", true));
        } catch (Throwable e) {
            return "{\"message\":'" + e.getMessage() + " BUT " + successMessage + " (NO COMMIT)', \"OK\":" + false + "}";
        }
    }

    protected String responseSuccessObject(String object) {
        return "{\"message\":" + object + ", \"OK\":" + true + "}";
    }

    protected boolean checkString(String str) {
        if (str.trim().length() <= 0 || str == null)
            return false;
        return true;
    }


    @Override
    public Response init(ChaincodeStub stub) {
        return newSuccessResponse(responseSuccess("Init"));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {

        try{

            this.stub = stub;
            String func = stub.getFunction();
            List<String> params = stub.getParameters();

            // SERVICE ROUTING FUNCTIONALITY

            // Keys
            if (func.equals("createKey")) {
                KeyService keyService = new KeyService(this.getStub());
                return keyService.createKey(params);
            } else if (func.equals("getKey")) {
                KeyService keyService = new KeyService(this.getStub());
                return keyService.getKey(params);
            }

            // Accounts
            else if (func.equals("createAccount")) {
                AccountService accountService = new AccountService(this.getStub());
                return accountService.createAccount(params);
            } else if (func.equals("getAccount")) {
                AccountService accountService = new AccountService(this.getStub());
                return accountService.getAccount(params);
            } else if (func.equals("updateAccount")) {
                AccountService accountService = new AccountService(this.getStub());
                return accountService.updateAccount(params);
            }
          //  else if (func.equals("queryAccount")) {
          //      AccountService accountService = new AccountService(this.getStub());
          //      return accountService.queryAccount(params);
          //  }

            // Flavor
            else if (func.equals("createFlavor")) {
                FlavorService flavorService = new FlavorService(this.getStub());
                return flavorService.createFlavor(params);
            } else if (func.equals("getFlavor")) {
                FlavorService flavorService = new FlavorService(this.getStub());
                return flavorService.getFlavor(params);
            } else if (func.equals("updateFlavor")) {
                FlavorService flavorService = new FlavorService(this.getStub());
                return flavorService.updateFlavor(params);
            }
           // else if (func.equals("queryFlavor")) {
           //     FlavorService flavorService = new FlavorService(this.getStub());
           //     return flavorService.queryFlavor(params);
           // }

            // Token
            else if (func.equals("getToken")) {
                TokenService tokenService = new TokenService(this.getStub());
                return tokenService.getToken(params);
            }
        //    else if (func.equals("queryToken")) {
        //        TokenService tokenService = new TokenService(this.getStub());
        //        return tokenService.queryToken(params);
        //    }

            // Action
            else if (func.equals("getAction")) {
                ActionService actionService = new ActionService(this.getStub());
                return actionService.getAction(params);
            }
         //    else if (func.equals("queryAction")) {
         //       ActionService actionService = new ActionService(this.getStub());
         //       return actionService.queryAction(params);
         //   }

            // Transaction
            else if (func.equals("getTransaction")) {
                TransactionService transactionService = new TransactionService(this.getStub());
                return transactionService.getTransaction(params);
            }
          //  else if (func.equals("queryTransaction")) {
          //      TransactionService transactionService = new TransactionService(this.getStub());
          //      return transactionService.queryTransaction(params);
          // }
            else if (func.equals("createTransaction")) {
                TransactionService transactionService = new TransactionService(this.getStub());
                return transactionService.createTransaction(params);
            }

            // Master service
            else if (func.equals("getVersion")) {
                MasterService masterService = new MasterService(this.getStub());
                return masterService.getVersion(params);
            } else if (func.equals("getAuthor")) {
                MasterService masterService = new MasterService(this.getStub());
                return masterService.getAuthor(params);
            } else if (func.equals("getDescription")) {
                MasterService masterService = new MasterService(this.getStub());
                return masterService.getDescription(params);
            }

            return newErrorResponse(responseError("Unsupported method", ""));
         }
         catch (Throwable ex){
             return newErrorResponse(responseError(ex.getMessage(), ""));
         }
    }

    public static void main(String[] args) {
        new BalanceTrackerBase().start(args);
    }

}
