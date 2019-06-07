## Balance Tracker test network configuration

Author: Interticket

The script creates a simple preconfigured qa environment for the BalanceTracker Interticket application. 
The created peers:
 - 2 endorsement peers
 - ordered
 - certificate authority
 - CLI

Note that this basic configuration uses pre-generated certificates and
key material, and also has predefined transactions to initialize a 
channel named "mychannel".

Make sure that the program runs on an installed Hyperledger Fabric 4.0 under the fabric-samples directory

To regenerate this material, simply run ``generate.sh``.

To start the network, run ``start.sh``. 
The command must be followed as parameter by the path for the source code, including the gradle files as well. 

To stop it, run ``stop.sh``
To completely remove all incriminating evidence of the network
on your system, run ``teardown.sh``.

