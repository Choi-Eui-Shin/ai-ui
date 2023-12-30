#!/bin/bash

set -e

TAG=$(date '+%Y%m%d')

JAVA_HOME=/home/demian/java11
PATH=$JAVA_HOME/bin:$PATH

echo "@.@ $TAG"

cd ..

#mkdir src/main/resources/static
#cp -r vue/dist/* src/main/resources/static
chmod +x ./mvnw
./mvnw clean package -Dmaven.test.skip=true
cp -f ./target/vision-service-0.0.1-SNAPSHOT.jar ./docker/app.jar

echo "@.@ Make Docker image..."
cd docker
docker rmi -f dreamsalmon/aiui:app-$TAG
docker build -f Dockerfile -t dreamsalmon/aiui:app-$TAG .
docker push dreamsalmon/aiui:app-$TAG