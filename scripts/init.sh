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
if ! sdk env; then
  sdk env install
fi
if [ ! -f "src/main/resources/config/application-dev.yml" ]; then
  cp -u src/main/resources/config/application-dev.example.yml src/main/resources/config/application-dev.yml
fi
