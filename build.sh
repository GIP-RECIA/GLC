#!/usr/bin/env bash

source ~/.nvm/nvm.sh
source ~/.sdkman/bin/sdkman-init.sh

# Frontend
nvm use
if yarn build; then
  # Backend
  sdk use java 11.0.21-tem
  ./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"
fi
