#!/bin/bash
##
# Exit on first error, print all commands.
set -ev

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

echo
echo " ____    _____      _      ____    _____ "
echo "/ ___|  |_   _|    / \    |  _ \  |_   _|"
echo "\___ \    | |     / _ \   | |_) |   | |  "
echo " ___) |   | |    / ___ \  |  _ <    | |  "
echo "|____/    |_|   /_/   \_\ |_| \_\   |_|  "
echo

echo "##########################################################"
echo "##### Preparing java files #########"
echo "##########################################################"

echo "Chaincode File path:"
echo $1
echo "Client File path:"
echo $2
echo "with explorer"
echo $3

mkdir -p balancetracker-chaincode/src
cp -R $1/src/main balancetracker-chaincode/src
cp $1/build.gradle balancetracker-chaincode
cp $1/settings.gradle balancetracker-chaincode
rm -rf balancetracker-chaincode/test

echo "##########################################################"
echo "##### Balance Tracker test network is starting #########"
echo "##########################################################"

# Shutting down exisiting network
docker-compose -f docker-compose.yml down

# Starting hyperledger fabric
docker-compose -f docker-compose.yml up -d ca orderer peer0 peer1 couchdb cli

# wait for Hyperledger Fabric to start
# incase of errors when running later commands, issue export FABRIC_START_TIMEOUT=<larger number>
export FABRIC_START_TIMEOUT=30
#echo ${FABRIC_START_TIMEOUT}
sleep ${FABRIC_START_TIMEOUT}

# Create the channel
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0 peer channel create -o orderer:7050 -c bcchannel -f /etc/hyperledger/configtx/channel.tx
# Join peer0.org1.example.com to the channel.
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0 peer channel join -b bcchannel.block

docker cp peer0:/opt/gopath/src/github.com/hyperledger/fabric/bcchannel.block bcchannel.block

docker cp bcchannel.block peer1:/opt/gopath/src/github.com/hyperledger/fabric/

# Join peer0.org1.example.com to the channel.
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer1 peer channel join -b bcchannel.block

# Update channels with anchor peer
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0 peer channel update -o orderer:7050 -c bcchannel -f /etc/hyperledger/configtx/Org1MSPanchors.tx 

# Starting hyperledger explorer
if [ "$3" == "-e" ] || [ "$2" == "-e" ]; then
echo "starting explorer"
docker-compose -f docker-compose.yml up -d explorerdb explorer
#docker-compose -f docker-compose.yml up -d explorerdb explorer proms grafana
fi

# Executing balancetracker initialization
docker exec cli scripts/balancetrackerinit.sh

# Executing SDK side testing scripts
if [ ! -z "$2" ] && [ "$2" != "-e" ];
then
echo "SDK test"
docker exec cli mkdir /srv/test/
docker cp $2 cli:/srv/test
docker exec cli java -jar /srv/test/BalanceTracker-1.0.1.jar
fi


echo "##########################################################"
echo "##### Balance Tracker test network is finishing #########"
echo "##########################################################"

echo
echo " _____   _   _   ____   "
echo "| ____| | \ | | |  _ \  "
echo "|  _|   |  \| | | | | | "
echo "| |___  | |\  | | |_| | "
echo "|_____| |_| \_| |____/  "
echo

exit 0

