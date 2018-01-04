/*
 *****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import gov.nasa.jpl.nexus.ningester.configuration.properties.PythonProcessorModule;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PythonChainProcessor {

    private RestTemplate restTemplate;

    private List<PythonProcessorModule> processorList;

    private String uriPath;

    private File granule;

    public PythonChainProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setGranule(Resource granule) throws IOException {
        this.granule = granule.getFile();
    }

    public NexusContent.NexusTile nexusTileProcessor(NexusContent.NexusTile nexusTile) {

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
                NexusContent.NexusTile.class).getBody();
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
