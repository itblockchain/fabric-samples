## Balance Tracker test network configuration

Author: Interticket

The script creates a simple preconfigured test / dev environment for the BalanceTracker Interticket application. 
The created peers:
 - endorsement peer
 - ordered
 - certificate authority
 - CLI

Note that this basic configuration uses pre-generated certificates and
key material, and also has predefined transactions to initialize a 
channel named "bcchannel".

To start the network, run different versions of the ``start.sh``. 
The command must be followed as parameter by the path for the source code, including the gradle files as well. 

``start_minikube.sh`` installs the infrastrcuture on a locally configured minikube envrionment. 

``start_GKE.sh`` installs the infrastrcuture google Kubernetes engine. 


follow -e option to to install hyperledger explorer as well. 



