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
package org.apache.sdap.ningester.configuration;

import org.apache.sdap.nexusproto.NexusTile;
import org.apache.sdap.ningester.configuration.properties.ApplicationProperties;
import org.apache.sdap.ningester.datatiler.FileSlicer;
import org.apache.sdap.ningester.datatiler.NetCDFItemReader;
import org.apache.sdap.ningester.processors.*;
import org.apache.sdap.ningester.writer.NexusWriter;
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
