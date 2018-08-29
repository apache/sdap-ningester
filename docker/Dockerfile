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

FROM openjdk:8-jdk

MAINTAINER dev@sdap.apache.org

RUN apt-get update && apt-get install -y \
    bzip2 \
    git \
    gcc \
    python3 \
    wget && \
    addgroup ningester && \
    adduser --system --group ningester && \
    rm -rf /var/lib/apt/lists/* && \
    chgrp ningester /usr/local

USER ningester
WORKDIR /tmp

# ########################
# # Anaconda   #
# ########################
RUN wget -q https://repo.continuum.io/miniconda/Miniconda3-4.3.31-Linux-x86_64.sh -O install_anaconda.sh && \
    /bin/bash install_anaconda.sh -b -p /usr/local/anaconda2 && \
    rm install_anaconda.sh && \
    /usr/local/anaconda2/bin/conda config --add channels conda-forge
ENV PATH /usr/local/anaconda2/bin:$PATH


# Install nexusproto
ARG APACHE_NEXUSPROTO=https://github.com/apache/incubator-sdap-nexusproto.git
ARG APACHE_NEXUSPROTO_BRANCH=master
COPY --chown=ningester:ningester install_nexusproto.sh ./install_nexusproto.sh
RUN ./install_nexusproto.sh $APACHE_NEXUSPROTO $APACHE_NEXUSPROTO_BRANCH

# Install ningesterpy
ARG APACHE_NINGESTERPY=https://github.com/apache/incubator-sdap-ningesterpy.git
ARG APACHE_NINGESTERPY_BRANCH=master
COPY --chown=ningester:ningester install_ningesterpy.sh ./install_ningesterpy.sh
RUN ./install_ningesterpy.sh $APACHE_NINGESTERPY $APACHE_NINGESTERPY_BRANCH

# Install ningester
ARG APACHE_NINGESTER=https://github.com/apache/incubator-sdap-ningester.git
ARG APACHE_NINGESTER_BRANCH=master
COPY --chown=ningester:ningester install_ningester.sh ./install_ningester.sh
RUN ./install_ningester.sh $APACHE_NINGESTER $APACHE_NINGESTER_BRANCH && \
  export NINGESTER_JAR=`find ningester/build/libs -name ningester*.jar`

RUN mkdir /home/ningester/data && \
    mkdir /home/ningester/config && \
    ln -s /data /home/ningester/data && \
    ln -s /config /home/ningester/config

VOLUME /home/ningester/data
VOLUME /home/ningester/config

COPY --chown=ningester:ningester entrypoint.sh ./entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
