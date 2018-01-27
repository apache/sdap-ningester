#!/usr/bin/env bash
set -e

NINGESTER_JAR=`find ningester/build/libs -name ningester*.jar`
CONFIG_FILES=`find /config -name "*.yml" | awk -vORS=, '{ print $1 }'`
GRANULE=`find /data -type f`

java -Dspring.profiles.active=$1 -Dspring.config.location=${CONFIG_FILES} -jar ${NINGESTER_JAR} granule=${GRANULE}