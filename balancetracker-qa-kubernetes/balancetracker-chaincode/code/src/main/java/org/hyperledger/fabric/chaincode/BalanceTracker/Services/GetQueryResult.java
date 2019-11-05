package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import com.google.gson.Gson;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Getting general query result service
 */
public class GetQueryResult extends TransactionServiceBase implements ServiceInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(GetKey.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Required services
     */
    public List<String> requiredServiceNames(){
        List<String> requiredServices = new ArrayList<String>();
        return requiredServices;
    }

    /**
     * Getting rich query result
     *
     * @param  args arguments of the call
     * @param  _stub chaincode stub
     * @return       Key response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "getQueryResult()");

        // check basic config and auth
        super.callService(args, _stub);

        if (args.size() != 1)
            throw new GetQueryResult.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        if (!checkString(args.get(0)))
            throw new GetQueryResult.ParametersNotAStringException("Invalid argument, accountId does not have a string format");


        String result = this.richQuery(args.get(0), _stub);

        logger.exiting(this.getClass().getSimpleName(), "getQueryResult()");

        return result;

    }

    /**
     * Getting a general query result
     * @return  result of the query
     */
    private String richQuery(String queryString, ChaincodeStub _stub) {

        ArrayList<String> resArray = new ArrayList<String>();
        QueryResultsIterator<KeyValue> rows = _stub.getQueryResult(queryString);


        if (rows != null) {
            while (rows.iterator().hasNext()) {
                String v = rows.iterator().next().getStringValue();
                if (v != null && v.trim().length() > 0) {
                    resArray.add(v);
                }
            }
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(resArray);

        return jsonString;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5016;

        public ParametersIncorrectNumberException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }

    /**
     * Input is not a string
     */
    public static class ParametersNotAStringException extends InputParameterInvalidException
    {
        protected Integer code = 5026;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}
