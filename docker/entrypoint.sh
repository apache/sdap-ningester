#!/usr/bin/env bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Exit immediately if a simple command returns non-zero exit code
# Cause the status of terminated background jobs to be reported immediately.
set -ebx
# With pipefail, the return status of a pipeline is "the value of the last (rightmost) command to exit with a non-zero status, or zero if all commands exit successfully"
set -o pipefail

NINGESTER_JAR=`find ningester/build/libs -name ningester*.jar`
CONFIG_FILES=`find /home/ningester/config -name "*.yml" | awk -vORS=, '{ print $1 }'`
GRANULE=`find /home/ningester/data -type f -print -quit`

export NINGESTERPY_SETTINGS=/home/ningester/ningesterpy_settings.py
random_port=${RANDOMIZE_NINGESTERPY_PORT:="false"}
if [ ${random_port} == "false" ]
then
    NINGESTER_PY_SERVER_NAME="127.0.0.1:5000"
else
    NINGESTERPY_PORT_FILE="/home/ningester/current_port"
    NINGESTER_PY_SERVER_NAME="127.0.0.1:0"
    echo "CREATE_PORT_FILE=True" >> ${NINGESTERPY_SETTINGS}
    echo "PORT_FILE='${NINGESTERPY_PORT_FILE}'" >> ${NINGESTERPY_SETTINGS}
fi
echo "SERVER_NAME='${NINGESTER_PY_SERVER_NAME}'" >> ${NINGESTERPY_SETTINGS}

echo "Launching ningesterpy. Logs from this process will be prefixed with [ningesterpy]"
python -u -m sdap.ningesterpy 2>&1 | stdbuf -o0 sed -e 's/^/[ningesterpy] /' &

if [ ! ${random_port} == "false" ]; then
    until [ -f "${NINGESTERPY_PORT_FILE}" ]; do
        sleep 1
    done
    port=$(<${NINGESTERPY_PORT_FILE})
    NINGESTER_PY_SERVER_NAME="127.0.0.1:${port}"
fi

NEXT_WAIT_TIME=0
until $(curl --output /dev/null --silent --head --fail http://${NINGESTER_PY_SERVER_NAME}/healthcheck) || [ ${NEXT_WAIT_TIME} -eq 10 ]; do
    echo "Checking http://${NINGESTER_PY_SERVER_NAME}/healthcheck"
    sleep 1
    NEXT_WAIT_TIME=$((NEXT_WAIT_TIME+1))
done
if [ ${NEXT_WAIT_TIME} -eq 10 ]; then
    echo "Timed out waiting for ningesterpy to start" >&2
    exit 1
fi

echo "Launching ningester. Logs from this process will be prefixed with [ningester]"
java -Dspring.profiles.active=$1 -Dspring.config.location=classpath:/application.yml,${CONFIG_FILES} -jar ${NINGESTER_JAR} granule=file://${GRANULE} ${@:2} --ningester.pythonChainProcessor.base_url="http://${NINGESTER_PY_SERVER_NAME}/" 2>&1 | sed -e 's/^/[ningester] /'
JAVA_EXIT_CODE=$?

echo "Exiting with code ${JAVA_EXIT_CODE}"
exit ${JAVA_EXIT_CODE}
