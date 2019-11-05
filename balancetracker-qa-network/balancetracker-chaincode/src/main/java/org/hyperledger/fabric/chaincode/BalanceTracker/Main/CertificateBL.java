package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import static org.hyperledger.fabric.chaincode.BalanceTracker.Utils.Constants.ModelTypeNames.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Account;
import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Certificate;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.*;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.time.Instant;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Business logic for certificates
 */
public class CertificateBL extends BusinessLogicBase implements CertificateBLInterface {

    /**
     * Logger initialization
     */
    final static Logger logger = Logger.getLogger(ActionBL.class.getName());

    {
        logger.setLevel(BalanceTrackerBase.logLevel);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new BalanceTrackerFormatter());
        if (logger.getHandlers().length == 0)
            logger.addHandler(ch);
    }

    /**
     * Constrcutor
     */
    public CertificateBL(ChaincodeStub _stub) throws CertificateBL.StubIsNullException {
        if (_stub == null)
            throw new CertificateBL.StubIsNullException("Stub can not be null at initializing the certificate business logic");
        this.stub = _stub;
    }

    /**
     * Getting a certificate by certificate id
     *
     * @param  _certificateId arguments of the call
     * @return       chaincode response
     */
    public Certificate getCertificate(String _certificateId) throws BalanceTrackerException{

        logger.entering(this.getClass().getSimpleName(), "getCertificate()");
        logger.finer("Parameters : certificateId : " + _certificateId);
        logger.finer("transaction id : " + this.getStub().getTxId());

        String certificateString = this.getStringState(_certificateId, CERTIFICATE);

        // null indicates that the certificate is not found that can be normal
        if (certificateString == null)
            return null;

        // null indicates that the certificate is not found that can be normal
        if(!checkString(certificateString))
            return null;

        Certificate certificate = Certificate.createCertificate(certificateString);

        logger.exiting(this.getClass().getSimpleName(), "getCertificate()");
        logger.finer("returning: " + certificate.toJSONString());

        return certificate;
    }


    /**
     * Issueing a new certificate
     *
     * @param  _certificateId arguments of the call
     * @param  _certificateValue arguments of the call
     * @param  _certificateName arguments of the call
     * @param  _issuedAt arguments of the call
     * @param  _ownerId arguments of the call
     *
     * @return       Certificate response
     */
    public Certificate issueCertificate(String _certificateId, String _certificateValue, String _certificateName, String _ownerId) throws BalanceTrackerException{

        logger.entering(this.getClass().getSimpleName(), "issueCertificate()");
        logger.finer("Parameters : certificateId : " + _certificateId +
                "\n certificateValue : " + _certificateValue +
                "\n certificateName : " + _certificateName +
                "\n ownerId : " + _ownerId);

        // Business logic error handling

        if (this.getCertificate(_certificateId)!= null){
            throw new CertificateBL.IdTakenException("Cannot create Certificate, Certificate Id is already taken");
        }

        Instant issuedAtI = this.getStub().getTxTimestamp();
        long issuedAt = issuedAtI.getEpochSecond();

        // checking if account id-s exist
        AccountBL accountBL = new AccountBL(this.getStub());
        if (accountBL.getAccount(_ownerId) == null){
            throw new CertificateBL.NotExistException("Referred account id does not exist at the issueCertificate call of the CertificateBL service, certificateID: " + _certificateId);
        }

        // create new Certidficate and write it into the blockchain

        Certificate certificateToStore = new Certificate(_certificateId, _certificateValue, _certificateName, issuedAt, 0,_ownerId);

        try{
            this.putState(_certificateId, (new ObjectMapper()).writeValueAsBytes(certificateToStore), CERTIFICATE);
            this.getStub().setEvent(Constants.EventNames.CERTIFICATE_ISSUED,(new ObjectMapper()).writeValueAsBytes(certificateToStore));

        } catch (
                JsonProcessingException _ex){
            throw new Account.SerializationException("Error at writing Certificate into the ledger", _ex);
        }

        logger.exiting(this.getClass().getSimpleName(), "issueCertificate()");
        logger.finer("return : " + certificateToStore.toJSONString());

        return certificateToStore;
    }


    /**
     * Revoking a certificate by certificate id
     *
     * @param  _certificateId arguments of the call
     * @return       chaincode response
     */
    public Certificate revokeCertificate(String _certificateId) throws BalanceTrackerException{

        logger.entering(this.getClass().getSimpleName(), "revokeCertificate()");
        logger.finer("Parameters : certificateId" + _certificateId);

        Certificate certificateToUpdate = this.getCertificate(_certificateId);

        if (certificateToUpdate == null){
            throw new CertificateBL.NotExistException("Cannot retire Certificate, Certificate Id is not found");
        }

        this.delState(_certificateId, CERTIFICATE);
        this.getStub().setEvent(Constants.EventNames.CERTIFICATE_REVOKED,_certificateId.getBytes());

        logger.exiting(this.getClass().getSimpleName(), "retireToken()");
        logger.finer("result : " + certificateToUpdate.toJSONString());

        return certificateToUpdate;
    }


    /**
     * EXCEPTION CLASSES
     */

    /**
     * Query does not contain the adequate number of input parameters
     */
    public static class ParametersIncorrectNumberException extends InputParameterInvalidException
    {
        protected Integer code = 5012;

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
