package org.hyperledger.fabric.chaincode.BalanceTracker.Main;

import org.hyperledger.fabric.chaincode.BalanceTracker.Models.Certificate;
import org.hyperledger.fabric.chaincode.BalanceTracker.Utils.BalanceTrackerException;

/**
 * Interface for certificate services
 */
public interface CertificateBLInterface {

    /**
     * Getting a certificate by certificate id
     *
     * @param  _certificateId arguments of the call
     * @return       chaincode response
     */
    Certificate getCertificate(String _certificateId) throws BalanceTrackerException;

    /**
     * Creating action by filter
     *
     * @param  _certificateId arguments of the call
     * @param  _certificateValue arguments of the call
     * @param  _certificateName arguments of the call
     * @param  _ownerId arguments of the call
     *
     * @return       Certificate response
     */
    public Certificate issueCertificate(String _certificateId, String _certificateValue, String _certificateName, String _ownerId) throws BalanceTrackerException;

    /**
     * Revoking a certificate by certificate id
     *
     * @param  _certificateId arguments of the call
     * @return       chaincode response
     */
    public Certificate revokeCertificate(String _certificateId) throws BalanceTrackerException;

}
