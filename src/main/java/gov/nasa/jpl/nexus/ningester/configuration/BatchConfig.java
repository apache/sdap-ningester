package gov.nasa.jpl.nexus.ningester.configuration;

import gov.nasa.jpl.nexus.ningester.configuration.properties.ApplicationProperties;
import gov.nasa.jpl.nexus.ningester.datatiler.SliceFileByTilesDesired;
import gov.nasa.jpl.nexus.ningester.processors.CompositeItemProcessor;
import gov.nasa.jpl.nexus.ningester.processors.PythonChainProcessor;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    protected List<String> tileSpecifications(Resource granule) throws IOException {
        SliceFileByTilesDesired fileSlicer = new SliceFileByTilesDesired();
        fileSlicer.setDimensions(Arrays.asList("lat", "lon"));
        fileSlicer.setTilesDesired(5184);
        return fileSlicer.generateSlices(granule.getFile());
    }

    @Bean
    @JobScope
    protected ItemReader<String> reader(List<String> tileSpecifications) {
        return new ListItemReader<>(tileSpecifications);
    }

    @Bean
    @JobScope
    protected ItemProcessor<String, NexusContent.NexusTile> processor() {
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
    protected Step step1(ItemReader<String> reader, ItemProcessor<String, NexusContent.NexusTile> processor, ItemWriter<NexusContent.NexusTile> writer) {
        return steps.get("step1")
                .<String, NexusContent.NexusTile>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer).build();
    }

}
