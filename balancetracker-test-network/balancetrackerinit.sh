#!/bin/bash
#
# Copyright Interticket
#

echo "##### Balance Tracker: installing chaincode #########"

peer chaincode install -l java -n mycc -v v1 -p /opt/gopath/src/github.com/chaincode/

sleep 10

echo "##### Balance Tracker: initializing chaincode #########"

peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n mycc  -v v1 -c '{"Args":[]}' -P 'OR ("Org1MSP.member")'

sleep 30

echo "##### Balance Tracker: test master services #########"
echo "Test getVersion"

#MASTER SERVICE: query master service: getVersion

peer chaincode query -C mychannel -n mycc -c '{"Args":["getVersion"]}'

#MASTER SERVICE: query master service: getAuthor
echo "Test getAuthor"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getAuthor"]}'

#MASTER SERVICE: query master service: getDescription
echo "Test getAuthor"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getDescription"]}'

#KEY SERVICE: create new keys
echo "##### Balance Tracker: test keys #########"
echo "Test createKey 1"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createKey","testKey1"]}'

echo "Test createKey 2"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createKey","testKey2"]}'

#KEY SERVICE: test new keys
echo "Test getKey 1"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getKey","testKey1"]}'

echo "Test getKey 2"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getKey","testKey2"]}'

#FLAVOR SERVICE: test new Flavor, update existing Flavor

echo "##### Balance Tracker: Test Flavor #########"
echo "Test createFlavor 1"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C channel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createFlavor","testFlavor1","true", "0", "[]", "[]" ]}'

echo "Test getFlavor 1"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getFlavor","testFlavor1"]}'

echo "Test updateFlavor 1"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["updateFlavor","testFlavor1","[testTag1]"]}'

#ACCOUNT SERVICE: test new Account, updating existing Account
echo "##### Balance Tracker: Test Flavor #########"
echo "Test createAccount 1"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createAccount","testAccount1", "0", "[]", "[]" ]}'

echo "Test getAccount 1"

peer chaincode query -C $CHANNEL_NAME -n mycc -c '{"Args":["getAccount","testAccount1"]}'

echo "Test createAccount 2"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createAccount","testAccount2", "0", "[]", "[]" ]}'

echo "Test getAccount 2"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getAccount","testAccount2"]}'

echo "Test updateAccount 2"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["updateAccount","testAccount1","[testTag1]"]}'

#TRANSACTION SERVICE: issue new transaction
echo "##### Balance Tracker: Test ISSUE TRANSACTION #########"
echo "Test Issue token 1"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt '{"Args":["createTransaction","{\"transactionId\":\"transactionId1\",\"sequence\":1,\"tags\":[], \"actions\": [{\"type\": \"issue\",\"id\": \"1001\",\"flavorId\": \"testFlavor1\", \"tokenId\": \"newTokenId1\",  \"tokenCode\":\"tokenCode1\", \"destinationAccountId\": \"testAccount1\",\"amount\": \"1001\", \"tokenTags\": [], \"actionTags\": [] }]}"]}'

echo "Test getTransaction"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getTransaction","transactionId1"]}'

echo "Test getAction"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getAction","transactionId1_a0"]}'

echo "Test getToken"

peer chaincode query -C mychannel -n mycc -c '{"Args":["getToken","newTokenId1"]}'

#TRANSACTION SERVICE: transfer transaction
echo "##### Balance Tracker: Test TRANSFER TRANSACTION #########"
echo "Test Transfer token 1 into token 2"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId2\",\"sequence\":1,\"tags\":[], \"actions\": [{\"type\": \"transfer\",\"tokenId\": \"newTokenId1\",\"newTokenId\": \"newTokenId2\", \"amount\": \"10\",\"destinationAccountId\":\"testAccount2\",\"actionTags\":[],\"tokenTags\":[] }]}"]}'

echo "Test Transfer token 1 into token 2"

peer chaincode query -C $CHANNEL_NAME -n mycc -c '{"Args":["getToken","newTokenId2"]}'

#TRANSACTION SERVICE: retire transaction
echo "##### Balance Tracker: Test RETIRE TRANSACTION #########"
echo "Test Retire from token 2"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId3\",\"sequence\":1,\"tags\":[], \"actions\": [{\"type\": \"retire\",\"tokenId\": \"newTokenId2\",\"amount\": \"5\", \"actionTags\":[] }]}"]}'

echo "Test getToken amount 2"

peer chaincode query -C $CHANNEL_NAME -n mycc -c '{"Args":["getToken","newTokenId2"]}'

#TRANSACTION SERVICE: merge transaction
echo "##### Balance Tracker: Test MERGE TRANSACTION #########"
echo "Test Issue new token"

peer chaincode invoke -o orderer.example.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C mychannel -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId4\",\"sequence\":1,\"tags\":[], \"actions\": [{\"type\": \"issue\",\"id\": \"1001\",\"flavorId\": \"testFlavor1\", \"tokenId\": \"newTokenId3\",  \"tokenCode\":\"tokenCode1\", \"destinationAccountId\": \"testAccount1\",\"amount\": \"1001\", \"tokenTags\": [], \"actionTags\": [] }]}"]}'

echo "Test Merge tokens"

peer chaincode invoke -o orderer.example.com:7050 --tls true --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C $CHANNEL_NAME -n mycc --peerAddresses peer0.org1.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses peer0.org2.example.com:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt -c '{"Args":["createTransaction","{\"transactionId\":\"transactionId5\",\"sequence\":1,\"tags\":[], \"actions\": [{\"type\": \"merge\",\"tokenIds\": [\"newTokenId3\",\"newTokenId1\"],\"newTokenId\": \"testTokenIdNew\",\"tokenTags\":[] }]}"]}'

echo "Test getToken"

peer chaincode query -C $CHANNEL_NAME -n mycc -c '{"Args":["getToken","testTokenIdNew"]}'

echo "##### End of Integration testing #########"
#END INTEGRATION TESTING
