#!/usr/bin/env bash

echo "GLC Bulding Tool"
echo ""
echo "0 -> package"
echo "1 -> shapshot"
echo "2 -> release"
echo ""

read -p "Choose build type: " choice

init () {
  echo "--- Initialize ---"
  source ./scripts/init.sh
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

case $choice in

  0)
    init
    if yarn build; then package; fi
  ;;

  1)
    init
    if yarn build; then snapshot; fi
  ;;

  2)
    init
    if yarn build; then release; fi
  ;;

  *)
    echo "Unknown choice"
  ;;

esac
echo "GLC Builder"
