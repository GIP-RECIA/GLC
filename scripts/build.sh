profile_prod=prod
build_arguments='-Dmaven.test.skip=true -Darguments="-DskipTests"'

package () {
  echo "--- Package ---"
  ./mvnw clean package -P${profile_prod} ${build_arguments}
}

snapshot () {
  echo "--- Snapshot ---"
  ./mvnw clean package deploy -P${profile_prod} ${build_arguments}
}

release () {
  echo "--- Release ---"
  ./mvnw clean package release:prepare release:perform -P${profile_prod} ${build_arguments}
}

if [[ $1 != "" ]]; then
  choice="$1"
else
  echo "0 -> package"
  echo "1 -> snapshot"
  echo "2 -> release"
  echo ""

  read -r -p "Action: " choice
fi

case ${choice} in

  0) package;;
  1) snapshot;;
  2) release;;
  *) echo "Unknown choice";;

esac
