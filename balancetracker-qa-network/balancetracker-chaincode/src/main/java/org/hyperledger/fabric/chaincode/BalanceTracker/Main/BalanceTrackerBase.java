package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.ModelBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Services.*;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;

/**
 * Base ancestor class for different services and business logic
 */
public class BalanceTrackerBase extends ChaincodeBase {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(BalanceTrackerBase.class.getName());

    public static Level logLevel = Level.ALL;

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * versioning convention:
     * BTJ: Balance Tracker Java Implementation
     * Major version: 1.0 from go live
     * Minor version: increment at each master commit
     */
    protected static final String version = "BTJ_v0.6";
    protected static final String author = "Interticket";
    protected static final String description = "Balance Tracker implementation under Hyperledegr Fabric with Java chaincode";
    protected static final String servicePackage = "org.hyperledger.fabric.chaincode.BalanceTracker.Services";
    protected static final String configId = "BalanceTrackerConfig_42";

    protected ChaincodeStub stub;

    protected ChaincodeStub getStub(){
        return stub;
    }

    public static String getVersion() {
        return version;
    }

    public static String getAuthor() {
        return author;
    }

    public static String getDescription() {
        return description;
    }

    public BalanceTrackerBase(){
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
        return newSuccessResponse(responseSuccess(Constants.MessageNames.INIT_SUCCEEDED));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {

        try{

            this.stub = stub;
            String func = stub.getFunction();
            List<String> params = stub.getParameters();
            Object ret;

            logger.info("Invoked  function : " + func);
            for (String param : params){
                logger.info("Parameter :"  + param);
            }

            // SERVICE ROUTING FUNCTIONALITY
            String className = servicePackage + "." + func;
            ServiceInterface service = (ServiceInterface) Class.forName(className).getConstructor().newInstance();

            if (service == null)
            {
                throw new UnsupportedMethodException("The called service can not be found : incorrect service name " + func);
            }
            ret =  service.callService(params, stub);

            // null is a valid return value, indicating the item is not found
            if (ret == null)
                ret = "";

            // return value formatting
            if (ret instanceof ModelBase) {
                return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(((ModelBase)ret).toJSONString())));
            }
            else if(ret instanceof String) {
                return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(ret.toString())));
            }
            else {
                return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccess(ret.toString())));
            }
         }
        catch (InputParameterInvalidException _ex){

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            _ex.printStackTrace(pw);

            logger.severe("Invalid input parameter exception : " + _ex.getMessage() +
                    "\n error code : " + _ex.getErrorCode() +
                    "\n transaction id : " + stub.getTxId() +
                    "\n stack trace : " + sw);

            return newErrorResponse(responseError(Constants.MessageNames.INIT_ERROR + " " + _ex.getMessage(), ((InputParameterInvalidException)_ex).getErrorCode().toString()));
        }
        catch (BalanceTrackerException _ex){

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            _ex.printStackTrace(pw);

            logger.severe("BalanceTracker error : " + _ex.getMessage() +
                    "\n error code : " + _ex.getErrorCode() +
                    "\n transaction id : " + stub.getTxId() +
                    "\n stack trace : " + sw);

            return newErrorResponse(responseError(Constants.MessageNames.BALANCE_TRACKER_ERROR + " " + _ex.getMessage(), ((BalanceTrackerException)_ex).getErrorCode().toString()));
        }
        catch (Exception _ex){

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            _ex.printStackTrace(pw);

            logger.severe("General error : " + _ex.getMessage() +
                    "\n cause : " + _ex.getCause().getMessage() +
                    "\n transaction id : " + stub.getTxId() +
                    "\n stack trace : " + sw);

            return newErrorResponse(responseError(Constants.MessageNames.GENERAL_ERROR + " " + _ex.getMessage(), "500"));
        }

        catch (Throwable _ex){

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            _ex.printStackTrace(pw);

            logger.severe("Fatal error : " + _ex.getMessage() +
                    "\n cause : " + _ex.getCause().getMessage() +
                    "\n transaction id : " + stub.getTxId() +
                    "\n stack trace : " + sw);

            return newErrorResponse(responseError(Constants.MessageNames.FATAL_ERROR + " " + _ex.getMessage(), "500"));
         }
    }

    public static void main(String[] args) {
        new BalanceTrackerBase().start(args);
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class UnsupportedMethodException extends InputParameterInvalidException
    {
        protected Integer code = 5000;

        public UnsupportedMethodException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ServiceInitializationException extends InputParameterInvalidException
    {
        protected Integer code = 5099;

        public ServiceInitializationException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}
