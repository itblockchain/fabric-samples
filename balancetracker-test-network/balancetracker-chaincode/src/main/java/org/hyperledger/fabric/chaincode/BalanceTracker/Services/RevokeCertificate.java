package org.hyperledger.fabric.chaincode.BalanceTracker.Services;

import org.hyperledger.fabric.chaincode.BalanceTracker.Main.BalanceTrackerBase;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.CertificateBL;
import org.hyperledger.fabric.chaincode.BalanceTracker.Main.CertificateBLInterface;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Certificate;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerFormatter;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.InputParameterInvalidException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Service for revoking certificates
 */
public class RevokeCertificate extends ServiceBase implements ServiceInterface   {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(RevokeCertificate.class.getName());

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
        requiredServices.add("GetCertificate");
        requiredServices.add("IssueCertificate");
        return requiredServices;
    }

    /**
     * Calling revokeCertificate
     * @param  args arguments of the call
     * @param  _stub arguments of the call*
     * @return       object response
     */
    @Override
    public Object callService(List<String> args, ChaincodeStub _stub) throws BalanceTrackerException {
        logger.entering(this.getClass().getSimpleName(), "revokeCertificate()");

        // check basic config and auth
        super.callService(args, _stub);

        // INPUT PARAMETER VALIDATION
        // 0 - certificateId

        String certificateId;

        if (args.size() != 1)
            throw new RevokeCertificate.ParametersIncorrectNumberException("Incorrect number of arguments, expecting exactly 1");

        if (!checkString(args.get(0)))
            throw new RevokeCertificate.ParametersNotAStringException("Invalid argument, certificateId does not have a string format");

        certificateId = args.get(0);

        logger.config("parameter Certificate Id " + certificateId);
        logger.config("transaction id : " + _stub.getTxId());

        // Business Logic

        CertificateBLInterface certificateBL = new CertificateBL(_stub);
        Certificate retCertificate = certificateBL.revokeCertificate(certificateId);

        logger.exiting(this.getClass().getSimpleName(), "revokeCertificate()");

        return retCertificate;
    }

    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5018;

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
        protected Integer code = 5028;

        public ParametersNotAStringException(String msg) {
            super(msg);
        }

        @Override
        public Integer getErrorCode() {
            return this.code;
        }
    }


}
