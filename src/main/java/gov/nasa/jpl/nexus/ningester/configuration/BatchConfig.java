package gov.nasa.jpl.nexus.ningester.configuration;

import gov.nasa.jpl.nexus.ningester.configuration.properties.ApplicationProperties;
import gov.nasa.jpl.nexus.ningester.datatiler.FileSlicer;
import gov.nasa.jpl.nexus.ningester.datatiler.NetCDFItemReader;
import gov.nasa.jpl.nexus.ningester.processors.CompositeItemProcessor;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    protected JobBuilderFactory jobs;

    @Autowired
    protected StepBuilderFactory steps;

    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected ApplicationContext context;

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobs.get("testNc4").start(step1).build();
    }

    @Bean
    @JobScope
    protected Resource granule(ResourceLoader resourceLoader, @Value("#{jobParameters['granule']}") String granuleLocation) {
        return resourceLoader.getResource(granuleLocation);
    }

    @Bean
    @JobScope
    protected ItemStreamReader<NexusContent.NexusTile> reader(FileSlicer fileSlicer, Resource granule) {
        NetCDFItemReader reader = new NetCDFItemReader(fileSlicer);
        reader.setResource(granule);
        return reader;
    }

    @Bean
    @JobScope
    protected ItemProcessor<NexusContent.NexusTile, NexusContent.NexusTile> processor() {
        return new CompositeItemProcessor<>(applicationProperties.getTileProcessors());
    }

    @Bean
    @JobScope
    protected ItemWriter<NexusContent.NexusTile> writer() {
        return items -> {
            for (NexusContent.NexusTile item : items) {
                System.out.println("Got tile");
            }
        };
    }

    @Bean
    @JobScope
    protected Step step1(ItemStreamReader<NexusContent.NexusTile> reader, ItemProcessor<NexusContent.NexusTile, NexusContent.NexusTile> processor, ItemWriter<NexusContent.NexusTile> writer) {
        return steps.get("step1")
                .<NexusContent.NexusTile, NexusContent.NexusTile>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer).build();
    }

}
