#!/usr/bin/env bash
set -e

APACHE_NINGESTERPY="https://github.com/apache/incubator-sdap-ningesterpy.git"
MASTER="master"

GIT_REPO=${1:-APACHE_NINGESTERPY}
GIT_BRANCH=${2:-$MASTER}

mkdir ningesterpy
pushd ningesterpy
git init
git pull ${GIT_REPO} ${GIT_BRANCH}

python setup.py install
popd