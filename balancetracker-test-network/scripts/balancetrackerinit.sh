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
echo "##### Balance Tracker: test master services #########"
echo "#####################################################"
echo
echo "Test getVersion"
echo

#MASTER SERVICE: query master service: getVersion

peer chaincode query -C mychannel -n mycc -c '{"Args":["getVersion"]}'

sleep 2

#MASTER SERVICE: query master service: getAuthor
echo
echo "Test getAuthor"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getAuthor"]}'

sleep 2

#MASTER SERVICE: query master service: getDescription
echo
echo "Test getAuthor"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getDescription"]}'

sleep 2

#KEY SERVICE: create new keys
echo
echo "#####################################################"
echo "##### Balance Tracker: test KEYS #########"
echo "#####################################################"
echo
echo "Test createKey 1"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createKey","testKey1"]}'

sleep 5

echo
echo "Test createKey 2"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createKey","testKey2"]}'

sleep 5

#KEY SERVICE: test new keys
echo
echo "Test getKey 1"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getKey","testKey1"]}'

sleep 2

echo
echo "Test getKey 2"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getKey","testKey2"]}'

#FLAVOR SERVICE: test new Flavor, update existing Flavor
echo
echo "#####################################################"
echo "##### Balance Tracker: Test FLAVORS #########"
echo "#####################################################"
echo
echo "Test createFlavor 1"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createFlavor","testFlavor1","true", "0", "[]", "[]" ]}'

sleep 5

echo
echo "Test getFlavor 1"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getFlavor","testFlavor1"]}'

sleep 2

echo
echo "Test updateFlavor 1"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["updateFlavor","testFlavor1","[testTag1]"]}'

sleep 5

#ACCOUNT SERVICE: test new Account, updating existing Account
echo
echo "#####################################################"
echo "##### Balance Tracker: Test ACCOUNTS #########"
echo "#####################################################"
echo
echo "Test createAccount 1"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createAccount","testAccount1", "0", "[]", "[]" ]}'

sleep 5

echo
echo "Test getAccount 1"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getAccount","testAccount1"]}'

sleep 2

echo
echo "Test createAccount 2"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createAccount","testAccount2", "0", "[]", "[]" ]}'

sleep 5

echo
echo "Test getAccount 2"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getAccount","testAccount2"]}'

sleep 2

echo
echo "Test updateAccount 2"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["updateAccount","testAccount1","[testTag1]"]}'

sleep 5

#TRANSACTION SERVICE: issue new transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test ISSUE TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Issue token 1"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId1\",\"sequence\":1,\"tags\":[],\"actions\":[{\"type\":\"issue\",\"id\":\"1001\",\"flavorId\":\"testFlavor1\",\"tokenId\":\"newTokenId1\",\"tokenCode\":\"tokenCode1\",\"destinationAccountId\":\"testAccount1\",\"amount\":\"1001\",\"tokenTags\": [],\"actionTags\":[]}]}"]}'

sleep 5

echo
echo "Test getTransaction"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getTransaction","transactionId1"]}'

echo
echo "Test getAction"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getAction","transactionId1_a0"]}'

sleep 2

echo
echo "Test getToken"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getToken","newTokenId1"]}'

sleep 2

#TRANSACTION SERVICE: transfer transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test TRANSFER TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Transfer token 1 into token 2"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{ \"transactionId\": \"transactionId2\" , \"sequence\":1,            \"tags\":[], \"actions\": [{\"type\": \"transfer\",\"tokenId\":\"newTokenId1\",\"newTokenId\": \"newTokenId2\", \"amount\": \"10\",\"destinationAccountId\":\"testAccount2\",\"actionTags\":[],\"tokenTags\":[] }]}"]}'

sleep 5

echo
echo "Test Transfer token 1 into token 2"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getToken","newTokenId2"]}'

sleep 2

#TRANSACTION SERVICE: retire transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test RETIRE TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Retire from token 2"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId3\",\"sequence\":1,\"tags\":[], \"actions\": [{\"type\": \"retire\",\"tokenId\": \"newTokenId2\",\"amount\": \"5\", \"actionTags\":[] }]}"]}'

sleep 5

echo
echo "Test getToken amount 2"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getToken","newTokenId2"]}'

sleep 2

#TRANSACTION SERVICE: merge transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test MERGE TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Issue new token"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId4\",\"sequence\":1, \"tags\":[], \"actions\":[{\"type\": \"issue\",\"id\": \"1001\",\"flavorId\": \"testFlavor1\", \"tokenId\": \"newTokenId3\",  \"tokenCode\":\"tokenCode1\", \"destinationAccountId\": \"testAccount1\",\"amount\": \"1001\", \"tokenTags\": [], \"actionTags\": [] }]}"]}'

sleep 5

echo
echo "Test Merge tokens"
echo

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId5\",\"sequence\":1,\"tags\":[], \"actions\": [{\"type\": \"merge\",\"tokenIds\":[\"newTokenId3\",\"newTokenId1\"],\"newTokenId\": \"testTokenIdNew\",\"tokenTags\":[] }]}"]}'

sleep 5

echo
echo "Test getToken"
echo

peer chaincode query -C mychannel -n mycc -c '{"Args":["getToken","testTokenIdNew"]}'

sleep 2
echo "##########################################"
echo "##### End of Integration testing #########"
echo "##########################################"
#END INTEGRATION TESTING
