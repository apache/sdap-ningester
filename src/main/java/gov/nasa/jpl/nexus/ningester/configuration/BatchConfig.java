package gov.nasa.jpl.nexus.ningester.configuration;

import gov.nasa.jpl.nexus.ningester.datatiler.FileSlicer;
import gov.nasa.jpl.nexus.ningester.datatiler.SliceFileByTilesDesired;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  @Autowired
  private JobBuilderFactory jobs;

  @Autowired
  private StepBuilderFactory steps;

  @Bean
  public Job job(@Qualifier("step1") Step step1) {
    return jobs.get("testNc4").start(step1).build();
  }

  @Bean
  @JobScope
  protected Resource granule(ResourceLoader resourceLoader, @Value("#{jobParameters['granule']}") String granuleLocation){
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
  protected ItemWriter<String> writer() {
    ItemWriterAdapter<String> writer = new ItemWriterAdapter<>();
    writer.setTargetMethod("println");
    writer.setTargetObject(System.out);
    return writer;
  }

  @Bean
  protected Step step1(ItemReader<String> reader, ItemWriter<String> writer) {
    return steps.get("step1").<String, String>chunk(10).reader(reader).writer(writer).build();
  }
}
