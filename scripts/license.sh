files=$(find ./src/ ./scripts/ -name '*.vue' -type f \
  -or -name '*.ts' -type f \
  -or -name '*.scss' -type f \
  -or -name '*.java' -type f \
  -or -name '*.xml' -type f \
  -or -name '*.sh' -type f)

check () {
  docker run --rm -t --name licence -v ${PWD}:/src \
    ghcr.io/google/addlicense -check -f ./etc/header.template ${files}
}

generate () {
  docker run --rm -t --name licence -v ${PWD}:/src \
    ghcr.io/google/addlicense -f ./etc/header.template ${files}
}

commit () {
  if [[ `git status --porcelain *.vue *.ts *.scss *.java *.xml *.sh` ]]; then
    git add ./src/
    git add ./scripts/
    git commit -m "docs: generate missing license headers"
    git push
  fi
}

if [[ $1 != "" ]]; then
  choice="$1"
else
  echo "1 -> check"
  echo "2 -> generate"
  echo "3 -> generate + commmit"
  echo ""

  read -r -p "Action: " choice
fi

case ${choice} in

  1) check;;
  2) generate;;
  3)
    generate
    commit
  ;;
  *) echo "Unknown choice";;

esac
