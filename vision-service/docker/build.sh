#!/bin/bash

set -e

TAG=$(date '+%Y%m%d')

JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
PATH=$JAVA_HOME/bin:$PATH

echo "@.@ $TAG"

cd ..

chmod +x ./mvnw
./mvnw clean package -Dmaven.test.skip=true
cp -f ./target/llm-application-0.0.1-SNAPSHOT.jar ./cicd/app.jar

echo "@.@ Make Docker image..."
cd cicd
docker rmi -f llm-app:$TAG
docker build -f Dockerfile -t llm-app:$TAG .
