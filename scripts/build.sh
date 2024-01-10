#!/usr/bin/env bash

init () {
  read -r -p "Initialize environment? [y/N] " initialize
  initialize=${initialize,,}

  if [[ "$initialize" =~ ^(yes|y)$ ]]; then
    echo "--- Initialize ---"
    source ./scripts/init.sh
  fi
}

build () {
  read -r -p "Build frontend? [y/N] " frontend
  frontend=${frontend,,}

  if [[ "$frontend" =~ ^(yes|y)$ ]]; then
    echo "--- Build frontend ---"
    if ! yarn build; then exit; fi
  fi
}

package () {
  echo "--- Package ---"
  ./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"
}

snapshot () {
  echo "--- Snapshot ---"
  ./mvnw clean package deploy -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"
}

release () {
  echo "--- Release ---"
  ./mvnw clean package release:prepare release:perform -P prod -Dmaven.test.skip=true -Darguments="-DskipTests -Dmaven.deploy.skip=true"
}

echo "GLC Building Tool"
echo ""
echo "0 -> package"
echo "1 -> snapshot"
echo "2 -> release"
echo ""

read -r -p "Build type: " choice

case $choice in

  0)
    init
    build
    package
  ;;

  1)
    init
    build
    snapshot
  ;;

  2)
    init
    build
    release
  ;;

  *)
    echo "Unknown choice"
  ;;

esac
