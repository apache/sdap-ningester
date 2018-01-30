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
set -e

NINGESTER_JAR=`find ningester/build/libs -name ningester*.jar`
CONFIG_FILES=`find /config -name "*.yml" | awk -vORS=, '{ print $1 }'`
GRANULE=`find /data -type f -print -quit`

python -m sdap.ningesterpy 2>&1 | sed "s/^/[ningesterpy] /" &

java -Dspring.profiles.active=$1 -Dspring.config.location=classpath:/application.yml,${CONFIG_FILES} -jar ${NINGESTER_JAR} granule=file://${GRANULE} ${@:2} | sed "s/^/[ningester] /"