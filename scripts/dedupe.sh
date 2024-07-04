yarn dedupe
if [ `git status --porcelain yarn.lock` ]; then
  git add yarn.lock
  git commit -m "build(yarn): de-duplicate entries"
  git push
fi
