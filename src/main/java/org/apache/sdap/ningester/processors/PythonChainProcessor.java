/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.sdap.ningester.processors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.apache.sdap.nexusproto.NexusTile;
import org.apache.sdap.ningester.processors.properties.PythonProcessorModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

public class PythonChainProcessor {

    private RestTemplate restTemplate;

    private List<PythonProcessorModule> processorList;

    private String uriPath;

    public PythonChainProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NexusTile nexusTileProcessor(NexusTile nexusTile) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setContentType(MediaType.APPLICATION_JSON);

        PythonChainProcessorRequest chainProcessorRequest = new PythonChainProcessorRequest();
        chainProcessorRequest.setProcessorList(processorList);
        try {
            chainProcessorRequest.setNexusTile(JsonFormat.printer().print(nexusTile));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<PythonChainProcessorRequest> requestEntity = new HttpEntity<>(chainProcessorRequest, headers);

        return restTemplate.exchange(
                uriPath,
                HttpMethod.POST,
                requestEntity,
                NexusTile.class).getBody();
    }

    public void setProcessorList(List<PythonProcessorModule> processorList) {
        this.processorList = processorList;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public class PythonChainProcessorRequest {

        @JsonProperty("processor_list")
        private List<PythonProcessorModule> processorList;

        @JsonProperty("input_data")
        private String nexusTile;

        public List<PythonProcessorModule> getProcessorList() {
            return processorList;
        }

        public void setProcessorList(List<PythonProcessorModule> processorList) {
            this.processorList = processorList;
        }

        public String getNexusTile() {
            return nexusTile;
        }

        public void setNexusTile(String nexusTile) {
            this.nexusTile = nexusTile;
        }
    }

}
