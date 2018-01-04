/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.configuration;

import gov.nasa.jpl.nexus.ningester.configuration.properties.ApplicationProperties;
import gov.nasa.jpl.nexus.ningester.datatiler.FileSlicer;
import gov.nasa.jpl.nexus.ningester.datatiler.SliceFileByTilesDesired;
import gov.nasa.jpl.nexus.ningester.http.NexusTileConverter;
import gov.nasa.jpl.nexus.ningester.processors.*;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

import java.io.IOException;
import java.util.Arrays;
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
    @ConditionalOnProperty(prefix = "ningester", name = "tile_slicer", havingValue = "sliceFileByTilesDesired")
    @Qualifier("fileSlicer")
    protected FileSlicer sliceFileByTilesDesired(){
        SliceFileByTilesDesired fileSlicer = new SliceFileByTilesDesired();
        fileSlicer.setDimensions(applicationProperties.getSliceFileByTilesDesired().getDimensions());
        fileSlicer.setTilesDesired(applicationProperties.getSliceFileByTilesDesired().getTilesDesired());
        fileSlicer.setTimeDimension(applicationProperties.getSliceFileByTilesDesired().getTimeDimension());
        return fileSlicer;
    }

    @Bean
    protected HttpMessageConverter nexusTileConverter() {
        NexusTileConverter converter = new NexusTileConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        return converter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.pythonChainProcessor", name = "enabled")
    protected RestTemplate restTemplate(HttpMessageConverter nexusTileConverter) {
        RestTemplate template = new RestTemplate();

        DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
        uriTemplateHandler.setBaseUrl(applicationProperties.getPythonChainProcessor().getBaseUrl().toString());
        template.setUriTemplateHandler(uriTemplateHandler);

        List<HttpMessageConverter<?>> converters = template.getMessageConverters();
        converters.add(nexusTileConverter);
        template.setMessageConverters(converters);

        return template;
    }

    /*
     * Item Processor beans defined below
     */
    @Bean
    @ConditionalOnProperty(prefix = "ningester.addDatasetName", name = "enabled")
    protected ItemProcessor<NexusContent.NexusTile, NexusContent.NexusTile> addDatasetName() {

        AddDatasetName processor = new AddDatasetName(applicationProperties.getAddDatasetName().getDatasetName());
        return processor::addDatasetName;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.addDayOfYearAttribute", name = "enabled")
    protected ItemProcessor<NexusContent.NexusTile, NexusContent.NexusTile> addDayOfYearAttribute() {

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(applicationProperties.getAddDayOfYearAttribute().getRegex());
        return processor::setDayOfYearFromGranuleName;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.addTimeFromGranuleName", name = "enabled")
    protected ItemProcessor<NexusContent.NexusTile, NexusContent.NexusTile> addTimeFromGranuleName() {

        AddTimeFromGranuleName processor = new AddTimeFromGranuleName(applicationProperties.getAddTimeFromGranuleName().getRegex(), applicationProperties.getAddTimeFromGranuleName().getDateFormat());
        return processor::setTimeFromGranuleName;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.generateTileId", name = "enabled")
    protected ItemProcessor<NexusContent.NexusTile, NexusContent.NexusTile> generateTileId() {

        GenerateTileId processor = new GenerateTileId();
        processor.setSalt(applicationProperties.getGenerateTileId().getSalt());
        return processor::addTileId;
    }

    @Bean
    @JobScope
    @ConditionalOnProperty(prefix = "ningester.pythonChainProcessor", name = "enabled")
    protected ItemProcessor<NexusContent.NexusTile, NexusContent.NexusTile> pythonChainProcessor(RestTemplate restTemplate, Resource granule) throws IOException {
        PythonChainProcessor processor = new PythonChainProcessor(restTemplate);
        processor.setGranule(granule);
        processor.setProcessorList(applicationProperties.getPythonChainProcessor().getProcessorList());
        processor.setUriPath(applicationProperties.getPythonChainProcessor().getUriPath());

        return processor::nexusTileProcessor;
    }

}
