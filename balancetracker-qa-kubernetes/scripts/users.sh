#!/bin/bash
#
# Copyright Interticket
#

echo
echo "#####################################################"
echo "##### Enroll fabric ca admin user #########"
echo "#####################################################"
echo

fabric-ca-client enroll -u https://Interticket:Blockchain4ever@localhost:7054 --tls.certfiles /etc/hyperledger/fabric-ca-server-config/ca.org1.example.com-cert.pem

echo
echo "#####################################################"
echo "##### Create client admin #########"
echo "#####################################################"
echo

fabric-ca-client register --id.name clientadmin --id.secret Blockchain4ever --id.affiliation org1 --id.attrs 'hf.Revoker=true,admin=true:ecert' -u https://localhost:7054 --tls.certfiles /etc/hyperledger/fabric-ca-server-config/ca.org1.example.com-cert.pem

echo
echo "#####################################################"
echo "##### Create exploreradmin #########"
echo "#####################################################"
echo

fabric-ca-client register --id.name exploreradmin --id.secret Blockchain4ever --id.affiliation org1 --id.attrs 'hf.Revoker=true,admin=true:ecert' -u https://localhost:7054 --tls.certfiles /etc/hyperledger/fabric-ca-server-config/ca.org1.example.com-cert.pem

echo "##########################################"
echo "##### End of users #########"
echo "##########################################"

#END INTEGRATION TESTING
