#!/usr/bin/env bash

source ~/.nvm/nvm.sh
source ~/.sdkman/bin/sdkman-init.sh

# Frontend
nvm install
npm i -g yarn
yarn
yarn prepare
yarn predev

# Backend
sdk install java 11.0.21-tem
sdk use java 11.0.21-tem
cp -u src/main/resources/config/application-dev.example.yml src/main/resources/config/application-dev.yml
