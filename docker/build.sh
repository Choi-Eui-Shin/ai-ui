#!/bin/bash

set -e

TAG=$(date '+%Y%m%d')

echo "@.@ $TAG"
echo "@.@ Make Docker image..."

cp ../yolo_service.py .

docker rmi -f aiui-y8-svc:$TAG
docker build -f Dockerfile -t aiui-y8-svc:$TAG .
