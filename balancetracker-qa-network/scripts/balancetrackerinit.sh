#!/bin/bash
#
# Copyright Interticket
#

echo
echo "#####################################################"
echo "##### Balance Tracker: installing chaincode to peer0 #########"
echo "#####################################################"
echo

CORE_PEER_ADDRESS=peer0:7051

peer chaincode install -l java -n bccc -v v1 -p /opt/gopath/src/github.com/chaincode/  --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

sleep 25

echo
echo "#####################################################"
echo "##### Balance Tracker: installing chaincode to peer1 #########"
echo "#####################################################"
echo

CORE_PEER_ADDRESS=peer1:7051

peer chaincode install -l java -n bccc -v v1 -p /opt/gopath/src/github.com/chaincode/  --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

sleep 25

CORE_PEER_ADDRESS=peer0:7051

echo
echo "#####################################################"
echo "##### Balance Tracker: initializing chaincode #########"
echo "#####################################################"
echo

#initializing with private store: you need to have the farm configured with private store !

#peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n mycc  -v v1 -c '{"Args":[]}' -P 'OR ("Org1MSP.member")' --collections-config /opt/gopath/src/github.com/hyperledger/fabric/peer/scripts/priv_collection_config.json

peer chaincode instantiate -o orderer:7050 -C bcchannel -n bccc  -v v1 -c '{"Args":[]}' -P 'OR ("Org1MSP.member")' --collections-config /opt/gopath/src/github.com/hyperledger/fabric/peer/scripts/priv_collection_config.json --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

sleep 120

echo
echo "#####################################################"
echo "##### Testing on peer0 #########"
echo "#####################################################"
echo

echo
echo "#####################################################"
echo "##### Balance Tracker: init services #########"
echo "#####################################################"
echo

peer chaincode invoke -o orderer:7050 --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["InitServices", "[\"GetVersion\",\"GetAuthor\",\"GetDescription\",\"GetServices\",\"SetLogLevel\",\"GetKey\",\"CreateKey\",\"GetAccount\",\"CreateAccount\",\"UpdateAccount\",\"GetFlavor\",\"CreateFlavor\",\"UpdateFlavor\",\"GetAction\",\"GetToken\",\"GetCertificate\",\"IssueCertificate\",\"RevokeCertificate\",\"GetQueryResult\", \"GetTransaction\",\"CreateTransaction\",\"GetSnapshot\"]", "{\"Account\":\"priv\",\"Key\":\"priv\"}"]}'

sleep 5


echo
echo "#####################################################"
echo "##### Balance Tracker: test master services #########"
echo "#####################################################"
echo
echo "Test GetVersion"
echo

#MASTER SERVICE: query master service: getVersion

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetVersion"]}'

sleep 2

#MASTER SERVICE: query master service: getAuthor
echo
echo "Test GetAuthor"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAuthor"]}'

sleep 2

#MASTER SERVICE: query master service: getDescription
echo
echo "Test GetAuthor"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetDescription"]}'

sleep 2

#MASTER SERVICE: query master service: getServices
echo
echo "Test GetServices"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetServices"]}'

sleep 2


#KEY SERVICE: create new keys
echo
echo "#####################################################"
echo "##### Balance Tracker: test KEYS #########"
echo "#####################################################"
echo
echo "Test CreateKey 1"

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateKey","testKey1"]}'

sleep 5

echo
echo "Test CreateKey 2"
echo

peer chaincode invoke -o orderer:7050 --tls --waitForEvent --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateKey","testKey2"]}'

sleep 5

#KEY SERVICE: test new keys
echo
echo "Test GetKey 1"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetKey","testKey1"]}'

sleep 2

echo
echo "Test GetKey 2"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetKey","testKey2"]}'

#FLAVOR SERVICE: test new Flavor, update existing Flavor
echo
echo "#####################################################"
echo "##### Balance Tracker: Test FLAVORS #########"
echo "#####################################################"
echo
echo "Test CreateFlavor 1"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateFlavor","testFlavor1","true", "0", "{\"flvTag1\":\"flvTagValue1\"}", "[]" ]}'

sleep 5

echo
echo "Test GetFlavor 1"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetFlavor","testFlavor1"]}'

sleep 2

echo
echo "Test UpdateFlavor 1"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["UpdateFlavor","testFlavor1","{testTag1:testTag1}"]}'

sleep 5

#ACCOUNT SERVICE: test new Account, updating existing Account
echo
echo "#####################################################"
echo "##### Balance Tracker: Test ACCOUNTS #########"
echo "#####################################################"
echo
echo "Test CreateAccount 1"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateAccount","testAccount1", "0", "[]", "[]" ]}'

sleep 5

echo
echo "Test GetAccount 1"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAccount","testAccount1"]}'

sleep 2

echo
echo "Test CreateAccount 2"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateAccount","testAccount2", "0", "{testTag1:testTag1}", "[]" ]}'

sleep 5

echo
echo "Test GetAccount 2"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAccount","testAccount2"]}'

sleep 2

echo
echo "Test UpdateAccount 2"
echo

peer chaincode invoke -o orderer:7050 --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["UpdateAccount","testAccount1","{testTag3:testTag3}"]}'

sleep 5

#TRANSACTION SERVICE: issue new transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test ISSUE TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Issue token 1"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateTransaction","{\"transactionId\":\"transactionId1\",\"sequence\":1,\"tags\":{\"trTag1\":\"trTagValue1\"},\"actions\":[{\"type\":\"issue\",\"id\":\"1001\",\"flavorId\":\"testFlavor1\",\"tokenId\":\"newTokenId1\",\"tokenCode\":\"tokenCode1\",\"destinationAccountId\":\"testAccount1\",\"amount\":\"1001\",\"tokenTags\":{\"tknTag1\":\"toknTagValue1\"},\"actionTags\":{\"actTag1\":\"actTagValue1\"}}]}"]}'

sleep 5

echo
echo "Test GetTransaction"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetTransaction","transactionId1"]}'

sleep 2

echo
echo "Test GetAction"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAction","1001"]}'

sleep 2

echo
echo "Test GetSnapshot"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetSnapshot","1001_sh"]}'


sleep 2

echo
echo "Test GetToken"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetToken","newTokenId1"]}'

sleep 2


#TRANSACTION SERVICE: transfer transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test TRANSFER TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Transfer token 1 into token 2"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateTransaction","{ \"transactionId\": \"transactionId2\" , \"sequence\":1,            \"tags\":{}, \"actions\": [{\"id\": \"act1\", \"type\": \"transfer\",\"tokenId\":\"newTokenId1\",\"newTokenId\": \"newTokenId2\", \"amount\": \"10\",\"destinationAccountId\":\"testAccount2\",\"actionTags\":{},\"tokenTags\":{} }]}"]}'

sleep 5

echo
echo "Test Transfer token 1 into token 2"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetToken","newTokenId2"]}'

sleep 2

#TRANSACTION SERVICE: retire transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test RETIRE TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Retire from token 2"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateTransaction","{\"transactionId\":\"transactionId3\",\"sequence\":1,\"tags\":{}, \"actions\": [{\"id\": \"act2\", \"type\": \"retire\",\"tokenId\": \"newTokenId2\",\"amount\": \"5\", \"actionTags\":{} }]}"]}'

sleep 5

echo
echo "Test GetToken amount 2"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetToken","newTokenId2"]}'

sleep 2

#TRANSACTION SERVICE: merge transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test MERGE TRANSACTION #########"
echo "#####################################################"
echo
echo "Test Issue new token"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateTransaction","{\"transactionId\":\"transactionId4\",\"sequence\":1, \"tags\":{}, \"actions\":[{\"id\": \"act3\", \"type\": \"issue\",\"id\": \"1055\",\"flavorId\": \"testFlavor1\", \"tokenId\": \"newTokenId8\",  \"tokenCode\":\"tokenCode1\", \"destinationAccountId\": \"testAccount1\",\"amount\": \"1001\", \"tokenTags\":{}, \"actionTags\": {} }]}"]}'

sleep 5

echo
echo "Test Merge tokens"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateTransaction","{\"transactionId\":\"transactionId5\",\"sequence\":1,\"tags\":{}, \"actions\": [{\"id\": \"act4\", \"type\": \"merge\",\"tokenIds\":[\"newTokenId8\",\"newTokenId1\"],\"newTokenId\": \"testTokenIdNew\",\"tokenTags\":{} }]}"]}'

sleep 5

echo
echo "Test GetToken"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetToken","testTokenIdNew"]}'

sleep 2

echo
echo "#####################################################"
echo "##### Balance Tracker: Test Rich query services #########"
echo "#####################################################"
echo
echo "Test IssueCertificate"
echo


echo
echo "Test rich query getQueryResult"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetQueryResult","{\"selector\": {\"accountId\":\"testAccount1\"}}"]}'
sleep 2

echo
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetQueryResult","{\"selector\": {\"modelType\":\"Account\"}}"]}'
sleep 2


echo
echo "#####################################################"
echo "##### Balance Tracker: Test Certificate services #########"
echo "#####################################################"
echo
echo "Test IssueCertificate"
echo

peer chaincode invoke -o orderer:7050 --waitForEvent --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["IssueCertificate","testCert1", "hashValue1", "myCert", "testAccount1" ]}'
sleep 5

echo
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetCertificate","testCert1"]}'
sleep 2


echo
echo "#####################################################"
echo "##### Testing on peer1 #########"
echo "#####################################################"
echo

CORE_PEER_ADDRESS=peer1:7051

echo
echo "#####################################################"
echo "##### Balance Tracker: test master services #########"
echo "#####################################################"
echo
echo "Test GetVersion"
echo

#MASTER SERVICE: query master service: getVersion

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetVersion"]}'

sleep 2

#MASTER SERVICE: query master service: getAuthor
echo
echo "Test GetAuthor"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAuthor"]}'

sleep 2

#MASTER SERVICE: query master service: getDescription
echo
echo "Test GetAuthor"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetDescription"]}'

sleep 2

#MASTER SERVICE: query master service: getServices
echo
echo "Test GetServices"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetServices"]}'

sleep 2


#KEY SERVICE: create new keys
echo
echo "#####################################################"
echo "##### Balance Tracker: test KEYS #########"
echo "#####################################################"
echo
echo "Test CreateKey 3"

peer chaincode invoke -o orderer:7050 --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem -C bcchannel -n bccc --peerAddresses peer0:7051 --tlsRootCertFiles /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt -c '{"Args":["CreateKey","testKey3"]}'

sleep 5

#KEY SERVICE: test new keys
echo
echo "Test GetKey 1"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetKey","testKey1"]}'

sleep 2

echo
echo "Test GetKey 2"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetKey","testKey2"]}'

sleep 2

echo
echo "Test GetKey 3"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetKey","testKey3"]}'

echo
echo "#####################################################"
echo "##### Balance Tracker: test FLAVORS #########"
echo "#####################################################"
echo

echo
echo "Test GetFlavor 1"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetFlavor","testFlavor1"]}'

sleep 2

#ACCOUNT SERVICE: test new Account, updating existing Account
echo
echo "#####################################################"
echo "##### Balance Tracker: Test ACCOUNTS #########"
echo "#####################################################"

echo
echo "Test GetAccount 1"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAccount","testAccount1"]}'

sleep 2

echo
echo "Test GetAccount 2"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAccount","testAccount2"]}'

sleep 2

#TRANSACTION SERVICE: issue new transaction
echo
echo "#####################################################"
echo "##### Balance Tracker: Test ISSUE TRANSACTION #########"
echo "#####################################################"

echo
echo "Test GetTransaction"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetTransaction","transactionId1"]}'

sleep 2

echo
echo "Test GetAction"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetAction","1001"]}'

sleep 2

echo
echo "Test GetSnapshot"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetSnapshot","1001_sh"]}'

sleep 2

echo
echo "Test GetToken"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetToken","newTokenId1"]}'

sleep 2

echo
echo "#####################################################"
echo "##### Balance Tracker: Test Certificate services #########"
echo "#####################################################"
echo

peer chaincode query -C bcchannel -n bccc -c '{"Args":["GetCertificate","testCert1"]}'
sleep 2

# change system variable to peer0
CORE_PEER_ADDRESS=peer0:7051

echo "##########################################"
echo "##### End of Integration testing #########"
echo "##########################################"

#END INTEGRATION TESTING
