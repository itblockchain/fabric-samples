package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.*;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Snapshot;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Getkey service
 */
public class GetSnapshot extends ServiceBase implements ServiceInterface  {

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
        requiredServices.add("GetKey");
        requiredServices.add("CreateKey");
        requiredServices.add("CreateAccount");
        requiredServices.add("GetAccount");
        requiredServices.add("UpdateAccount");
        requiredServices.add("CreateFlavor");
        requiredServices.add("GetFlavor");
        requiredServices.add("UpdateFlavor");
        requiredServices.add("GetAction");
        return requiredServices;
    }

    /**
     * Getting Snapshot by snapshot id
     *
     * @param  args arguments of the call
     * @param  _stub chaincode stub
     * @return       Key response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {

        logger.entering(this.getClass().getSimpleName(), "getSnapshot()");

        // check basic config and auth
        super.callService(args, _stub);

        // input parameter validation
        // 0 - keyId

        if (args.size() != 1)
            throw new ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        String snapshotId = args.get(0);

        logger.config("parameter Snapshot ID " + snapshotId);
        logger.config("transaction id : " + _stub.getTxId());

        if (!checkString(snapshotId))
            throw new ParametersNotAStringException("Invalid argument, keyId does not have a string format");

        SnapshotBLInterface snapshotBL = new SnapshotBL(_stub);
        Snapshot retSnapshot = snapshotBL.getSnapshot(snapshotId);

        logger.exiting(this.getClass().getSimpleName(), "getSnapshot()");

        return retSnapshot;
    }

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5017;

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
        protected Integer code = 5027;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }
}
