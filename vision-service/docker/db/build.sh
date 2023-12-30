#!/bin/bash

set -e
TAG=$(date '+%Y%m%d')

echo "@.@ Build: Database..." 
docker rmi -f dreamsalmon/aiui:db-$TAG
docker build -f Dockerfile.db -t dreamsalmon/aiui:db-$TAG .
docker push dreamsalmon/aiui:db-$TAG