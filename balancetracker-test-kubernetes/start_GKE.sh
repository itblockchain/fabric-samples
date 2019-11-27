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

# Setup google credentials to kubectl
# gcloud container clusters get-credentials balancetracker-dev --region europe-west3-c
# Check if current config is set
# kubectl config current-context

echo "##########################################################"
echo "##### Preparing java files #########"
echo "##########################################################"

echo "Chaincode File path:"
echo $1
echo "Client File path:"
echo $2
echo "with explorer"
echo $3


# configure firwall rules: only at first installation
# gcloud compute firewall-rules create fabricexplorer1 --allow tcp:31080
# gcloud compute firewall-rules create fabric1 --allow tcp:31050
# gcloud compute firewall-rules create fabric2 --allow tcp:31051
# gcloud compute firewall-rules create fabric3 --allow tcp:31053
# gcloud compute firewall-rules create fabric4 --allow tcp:31054
# gcloud compute firewall-rules create fabric5 --allow tcp:31055

echo "##########################################################"
echo "##### Copy files: analyze input parameters #########"
echo "##########################################################"

mkdir -p balancetracker-chaincode/code/src
cp -R $1/src/main balancetracker-chaincode/code/src
cp $1/build.gradle balancetracker-chaincode/code
cp $1/settings.gradle balancetracker-chaincode/code
rm -rf balancetracker-chaincode/code/test

echo "##########################################################"
echo "##### Delete existing installation #########"
echo "##########################################################"

# Shutting down exisiting networks
# kubectl delete -f kubernetes_setuppod.yaml
# kubectl delete -f kubernetes_fabric.yaml
# kubectl delete -f kubernetes_explorerdb.yaml
# kubectl delete -f kubernetes_explorer.yaml
# BE AWARE OF DATALOSS
# kubectl delete -f kubernetes_fabricvolumes.yaml
sleep 1

echo "##########################################################"
echo "##### Balance Tracker test network is starting #########"
echo "##########################################################"

# Create volumes: BE AWARE OF DATALOSS
kubectl create -f kubernetes_fabricvolumes.yaml
sleep 200

# create setup pod and configure mounts
kubectl create -f kubernetes_setuppod.yaml
sleep 200

# copy config files to the mapped directory
kubectl cp /home/hyperledgerdev/fabric-samples-interticket/balancetracker-test-kubernetes setuppod:/fabrichome

# Create new network
kubectl create -f kubernetes_fabric.yaml

sleep 200

PODPEER0=$(kubectl get pod -l balancetracker=peer0 -o jsonpath="{.items[0].metadata.name}")

echo $PODPEER0

# Create the channel
kubectl exec -it $PODPEER0 /etc/hyperledger/configtx/createchannel.sh

# Starting hyperledger explorer: release 2
if [ "$3" == "-e" ] || [ "$2" == "-e" ]; then
echo "##########################################################"
echo "##### Starting Hyperledger Explorer #########"
echo "##########################################################"
echo ""
echo "Start explorer DB"
    kubectl create -f kubernetes_explorerdb.yaml
    sleep 45

    PODEXPLORERDB=$(kubectl get pod -l balancetracker=explorerdb -o jsonpath="{.items[0].metadata.name}")

    kubectl exec -it $PODEXPLORERDB ./createdb.sh

    sleep 30

    echo "Start explorer"

    kubectl create -f kubernetes_explorer.yaml
fi

PODCLI=$(kubectl get pod -l balancetracker=cli -o jsonpath="{.items[0].metadata.name}")

echo $PODCLI

# Executing balancetracker initialization
kubectl exec -it $PODCLI scripts/balancetrackerinit.sh


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

