#!/bin/bash

set -e

TAG=$(date '+%Y%m%d')

echo "@.@ $TAG"
echo "@.@ Make Docker image..."

cp ../Utils.py .
cp ../captcha_model.py .
cp ../MyRect.py .
cp ../extractor_v4.py .
cp ../captcha_service.py .
cp ../bmcnn_service.py .

#docker rmi -f dreamsalmon/builder-app:$TAG
docker build -f Dockerfile -t captcha-app:$TAG .
#docker push dreamsalmon/builder-app:$TAG
