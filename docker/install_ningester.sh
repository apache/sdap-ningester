#!/usr/bin/env bash
set -e

APACHE_NINGESTER="https://github.com/apache/incubator-sdap-ningester.git"
MASTER="master"

GIT_REPO=${1:-APACHE_NINGESTER}
GIT_BRANCH=${2:-$MASTER}

mkdir ningester
pushd ningester
git init
git pull ${GIT_REPO} ${GIT_BRANCH}

./gradlew bootRepackage
popd