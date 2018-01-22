package gov.nasa.jpl.nexus.ningester.configuration;

import gov.nasa.jpl.nexus.ningester.configuration.properties.ApplicationProperties;
import gov.nasa.jpl.nexus.ningester.datatiler.FileSlicer;
import gov.nasa.jpl.nexus.ningester.datatiler.NetCDFItemReader;
import gov.nasa.jpl.nexus.ningester.processors.*;
import gov.nasa.jpl.nexus.ningester.writer.NexusWriter;
import org.apache.sdap.nexusproto.NexusTile;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableBatchProcessing
@Import(AppConfig.class)
public class BatchConfig {

    public static final String NINGESTER_JOB_NAME = "ningester";

    @Autowired
    protected JobBuilderFactory jobs;

    @Autowired
    protected StepBuilderFactory steps;

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected ApplicationContext context;

    @Bean
    public Job job(@Qualifier("ingestGranule") Step step1) {
        return jobs.get(NINGESTER_JOB_NAME).start(step1).build();
    }

    @Bean
    @JobScope
    protected Resource granule(ResourceLoader resourceLoader, @Value("#{jobParameters['granule']}") String granuleLocation) {
        return resourceLoader.getResource(granuleLocation);
    }

    @Bean
    @JobScope
    protected ItemStreamReader<NexusTile> reader(FileSlicer fileSlicer, Resource granule) {
        NetCDFItemReader reader = new NetCDFItemReader(fileSlicer);
        reader.setResource(granule);
        return reader;
    }

    @Bean
    @JobScope
    protected ItemProcessor<NexusTile, NexusTile> processor() {
        return new CompositeItemProcessor<>(applicationProperties.getTileProcessors());
    }

    @Bean
    @JobScope
    protected ItemWriter<NexusTile> writer(NexusWriter nexusWriter) {
        return nexusWriter::saveToNexus;
    }

    @Bean
    @JobScope
    protected Step ingestGranule(ItemStreamReader<NexusTile> reader, ItemProcessor<NexusTile, NexusTile> processor, ItemWriter<NexusTile> writer) {
        return steps.get("ingestGranule")
                .<NexusTile, NexusTile>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer).build();
    }

    /*
     * Item Processor beans defined below
     */
    @Bean
    @ConditionalOnBean(AddDatasetName.class)
    protected ItemProcessor<NexusTile, NexusTile> addDatasetName(AddDatasetName addDatasetNameBean) {
        return addDatasetNameBean::addDatasetName;
    }

    @Bean
    @ConditionalOnBean(AddDayOfYearAttribute.class)
    protected ItemProcessor<NexusTile, NexusTile> addDayOfYearAttribute(AddDayOfYearAttribute addDayOfYearAttributeBean) {
        return addDayOfYearAttributeBean::setDayOfYearFromGranuleName;
    }

    @Bean
    @ConditionalOnBean(AddTimeFromGranuleName.class)
    protected ItemProcessor<NexusTile, NexusTile> addTimeFromGranuleName(AddTimeFromGranuleName addTimeFromGranuleNameBean) {
        return addTimeFromGranuleNameBean::setTimeFromGranuleName;
    }

    @Bean
    @ConditionalOnBean(GenerateTileId.class)
    protected ItemProcessor<NexusTile, NexusTile> generateTileId(GenerateTileId generateTileIdBean) {
        return generateTileIdBean::addTileId;
    }

    @Bean
    @ConditionalOnBean(PythonChainProcessor.class)
    protected ItemProcessor<NexusTile, NexusTile> pythonChainProcessor(PythonChainProcessor pythonChainProcessorBean) {
        return pythonChainProcessorBean::nexusTileProcessor;
    }

}
