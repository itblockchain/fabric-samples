#!/bin/bash
#
# Copyright Interticket
#

echo
echo "#####################################################"
echo "##### Balance Tracker: installing chaincode #########"
echo "#####################################################"
echo

peer chaincode install -l java -n mycc -v v1 -p /opt/gopath/src/github.com/chaincode/

sleep 25

echo
echo "#####################################################"
echo "##### Balance Tracker: initializing chaincode #########"
echo "#####################################################"
echo

peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n mycc  -v v1 -c '{"Args":[]}' -P 'OR ("Org1MSP.member")'

sleep 120

echo
echo "#####################################################"
echo "##### Balance Tracker: init services #########"
echo "#####################################################"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateService", "GetVersion","GetAuthor","GetDescription","GetServices","SetLogLevel","GetKey","CreateKey","GetAccount","CreateAccount","UpdateAccount","GetFlavor","CreateFlavor","UpdateFlavor","GetAction","GetToken","GetCertificate","IssueCertificate","RevokeCertificate","GetQueryResult", "GetTransaction","CreateTransaction"]}'

sleep 5


