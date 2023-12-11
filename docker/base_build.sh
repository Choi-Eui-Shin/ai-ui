#!/bin/bash

set -e

#TAG=$(date '+%Y%m%d')

echo "@.@ Make Docker image for AI..."

docker rmi -f base-ai
docker build -f Dockerfile.base -t base-ai .
