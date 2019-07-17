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

echo "File path:"
echo $1
echo "with explorer"
echo $2

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
docker-compose -f docker-compose.yml up -d ca.example.com orderer.example.com peer0.org1.example.com couchdb cli

# wait for Hyperledger Fabric to start
# incase of errors when running later commands, issue export FABRIC_START_TIMEOUT=<larger number>
export FABRIC_START_TIMEOUT=30
#echo ${FABRIC_START_TIMEOUT}
sleep ${FABRIC_START_TIMEOUT}

# Create the channel
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel create -o orderer.example.com:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx
# Join peer0.org1.example.com to the channel.
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel join -b mychannel.block

# Starting hyperledger explorer
if [ "$2" == "-e" ]; then
echo "starting explorer"
docker-compose -f docker-compose.yml up -d explorerdb.example.com explorer.mynetwork.com proms grafana
fi

# Executing balancetracker initialization
docker exec cli scripts/balancetrackerinit.sh

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

