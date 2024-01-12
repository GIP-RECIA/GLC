#!/usr/bin/env bash

package () {
  echo "--- Package ---"
  ./mvnw clean package -P prod -Dmaven.test.skip=true -Darguments="-DskipTests"
}

snapshot () {
  echo "--- Snapshot ---"
  ./mvnw clean package deploy -P prod -Dmaven.test.skip=true -Darguments="-DskipTests"
}

release () {
  echo "--- Release ---"
  ./mvnw clean release:prepare release:perform -P prod -Dmaven.test.skip=true -Darguments="-DskipTests"
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
    package
  ;;

  1)
    snapshot
  ;;

  2)
    release
  ;;

  *)
    echo "Unknown choice"
  ;;

esac
