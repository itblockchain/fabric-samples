#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
# Exit on first error, print all commands.
set -ev

# Shut down the Docker containers that might be currently running.
# Shutting down exisiting networks
kubectl delete -f kubernetes_fabric.yaml
kubectl delete -f kubernetes_explorerdb.yaml
kubectl delete -f kubernetes_explorer.yaml

# stop minikube
minikube stop

