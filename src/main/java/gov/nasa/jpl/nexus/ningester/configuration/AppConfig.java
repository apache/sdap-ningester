/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.configuration;

import gov.nasa.jpl.nexus.ningester.configuration.properties.ApplicationProperties;
import gov.nasa.jpl.nexus.ningester.http.NexusTileConverter;
import gov.nasa.jpl.nexus.ningester.processors.AddTimeToSectionSpec;
import gov.nasa.jpl.nexus.ningester.processors.PythonChainProcessor;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class AppConfig {

    private final ApplicationProperties applicationProperties;

    @Autowired
    public AppConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    protected HttpMessageConverter nexusTileConverter() {
        NexusTileConverter converter = new NexusTileConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        return converter;
    }

    @Bean
    protected RestTemplate restTemplate(HttpMessageConverter nexusTileConverter) {
        RestTemplate template = new RestTemplate();

        DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
        uriTemplateHandler.setBaseUrl(applicationProperties.getNingesterpy().getBaseUrl().toString());
        template.setUriTemplateHandler(uriTemplateHandler);

        List<HttpMessageConverter<?>> converters = template.getMessageConverters();
        converters.add(nexusTileConverter);
        template.setMessageConverters(converters);

        return template;
    }

    @Bean
    @JobScope
    protected ItemProcessor<String, NexusContent.NexusTile> pythonChainProcessor(RestTemplate restTemplate, Resource granule) throws IOException {
        PythonChainProcessor processor = new PythonChainProcessor(restTemplate);
        processor.setGranule(granule);
        processor.setProcessorList(applicationProperties.getPythonChainProperties().getProcessorList());
        processor.setUriPath(applicationProperties.getPythonChainProperties().getUriPath());

        return processor::sectionSpecProcessor;
    }

    @Bean
    protected ItemProcessor<String, String> addTimeToSectionSpec(Resource granule) throws IOException {

        AddTimeToSectionSpec processor = new AddTimeToSectionSpec(applicationProperties.getAddTimeToSectionSpec().getTimeLen(), granule.getFile().getAbsolutePath());
        processor.setTimeVar(applicationProperties.getAddTimeToSectionSpec().getTimeVar());
        return processor::process;
    }
}
