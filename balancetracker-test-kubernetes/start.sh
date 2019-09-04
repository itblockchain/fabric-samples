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

echo "##########################################################"
echo "##### Copy files: analyze input parameters #########"
echo "##########################################################"

mkdir -p balancetracker-chaincode/code/src
cp -R $1/src/main balancetracker-chaincode/code/src
cp $1/build.gradle balancetracker-chaincode/code
cp $1/settings.gradle balancetracker-chaincode/code
rm -rf balancetracker-chaincode/code/test

echo "##########################################################"
echo "##### Minikube stop #########"
echo "##########################################################"

minikube stop

echo "##########################################################"
echo "##### Minikube start #########"
echo "##########################################################"

minikube start --mount "/fabric-samples:/data"

echo "##########################################################"
echo "##### Delete existing installation #########"
echo "##########################################################"

# Shutting down exisiting network
kubectl delete -f kubernetes.yaml

echo "##########################################################"
echo "##### Balance Tracker test network is starting #########"
echo "##########################################################"

# Create new network
kubectl create -f kubernetes.yaml

# Create the channel
kubectl exec peer0 peer channel create -o orderer:7050 -c mychannel -f /etc/hyperledger/configtx/channel.tx
# Join peer0.org1.example.com to the channel.
kubectl exec peer0 peer channel join -b mychannel.block

# Starting hyperledger explorer: release 2
#if [ "$3" == "-e" ] || [ "$2" == "-e" ]; then
#echo "starting explorer"
#docker-compose -f docker-compose.yml up -d explorerdb.example.com explorer.mynetwork.com proms #grafana
#fi

# Executing balancetracker initialization
kubectl exec cli scripts/balancetrackerinit.sh

# Executing SDK side testing scripts
#if [ ! -z "$2" ] && [ "$2" != "-e" ];
#then
#echo "SDK test"
#docker exec cli mkdir /srv/test/
#docker cp $2 cli:/srv/test
#docker exec cli java -jar /srv/test/BalanceTracker-1.0.1.jar
#fi

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

