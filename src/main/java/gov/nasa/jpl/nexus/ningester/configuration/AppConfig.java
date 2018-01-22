/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.configuration;

import gov.nasa.jpl.nexus.ningester.configuration.properties.ApplicationProperties;
import gov.nasa.jpl.nexus.ningester.datatiler.FileSlicer;
import gov.nasa.jpl.nexus.ningester.datatiler.SliceFileByDimension;
import gov.nasa.jpl.nexus.ningester.datatiler.SliceFileByTilesDesired;
import gov.nasa.jpl.nexus.ningester.http.NexusTileConverter;
import gov.nasa.jpl.nexus.ningester.processors.*;
import gov.nasa.jpl.nexus.ningester.writer.DataStore;
import gov.nasa.jpl.nexus.ningester.writer.MetadataStore;
import gov.nasa.jpl.nexus.ningester.writer.NexusWriter;
import org.apache.sdap.nexusproto.NexusTile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

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
    protected FileSlicer sliceFileByTilesDesired() {
        SliceFileByTilesDesired fileSlicer = new SliceFileByTilesDesired();
        fileSlicer.setDimensions(applicationProperties.getSliceFileByTilesDesired().getDimensions());
        fileSlicer.setTilesDesired(applicationProperties.getSliceFileByTilesDesired().getTilesDesired());
        fileSlicer.setTimeDimension(applicationProperties.getSliceFileByTilesDesired().getTimeDimension());
        return fileSlicer;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester", name = "tile_slicer", havingValue = "sliceFileByDimension")
    @Qualifier("fileSlicer")
    protected FileSlicer sliceFileByDimension() {
        SliceFileByDimension fileSlicer = new SliceFileByDimension();
        fileSlicer.setDimensions(applicationProperties.getSliceFileByDimension().getDimensions());
        fileSlicer.setSliceByDimension(applicationProperties.getSliceFileByDimension().getSliceByDimension());
        fileSlicer.setDimensionNamePrefix(applicationProperties.getSliceFileByDimension().getDimensionNamePrefix());
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

    @Bean
    public MetadataStore metadataStore() {
        return new MetadataStore() {
            @Override
            public void saveMetadata(List<? extends NexusTile> nexusTiles) {
            }

            @Override
            public void deleteMetadata(List<? extends NexusTile> nexusTiles) {
            }
        };
    }

    @Bean
    public DataStore dataStore() {
        return nexusTiles -> {
        };
    }

    @Bean
    public NexusWriter nexusWriter(MetadataStore metadataStore, DataStore dataStore) {
        return new NexusWriter(metadataStore, dataStore);
    }

    /*
     * Item Processor beans defined below
     */
    @Bean
    @ConditionalOnProperty(prefix = "ningester.addDatasetName", name = "enabled")
    protected AddDatasetName addDatasetNameBean() {

        AddDatasetName processor = new AddDatasetName(applicationProperties.getAddDatasetName().getDatasetName());
        return processor;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.addDayOfYearAttribute", name = "enabled")
    protected AddDayOfYearAttribute addDayOfYearAttributeBean() {

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(applicationProperties.getAddDayOfYearAttribute().getRegex());
        return processor;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.addTimeFromGranuleName", name = "enabled")
    protected AddTimeFromGranuleName addTimeFromGranuleNameBean() {

        AddTimeFromGranuleName processor = new AddTimeFromGranuleName(applicationProperties.getAddTimeFromGranuleName().getRegex(), applicationProperties.getAddTimeFromGranuleName().getDateFormat());
        return processor;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.generateTileId", name = "enabled")
    protected GenerateTileId generateTileIdBean() {

        GenerateTileId processor = new GenerateTileId();
        processor.setSalt(applicationProperties.getGenerateTileId().getSalt());
        return processor;
    }

    @Bean
    @ConditionalOnProperty(prefix = "ningester.pythonChainProcessor", name = "enabled")
    protected PythonChainProcessor pythonChainProcessorBean(RestTemplate restTemplate) {
        PythonChainProcessor processor = new PythonChainProcessor(restTemplate);
        processor.setProcessorList(applicationProperties.getPythonChainProcessor().getProcessorList());
        processor.setUriPath(applicationProperties.getPythonChainProcessor().getUriPath());

        return processor;
    }

}
