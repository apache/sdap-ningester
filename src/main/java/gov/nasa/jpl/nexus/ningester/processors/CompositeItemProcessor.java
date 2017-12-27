/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

public class CompositeItemProcessor<I, O> extends org.springframework.batch.item.support.CompositeItemProcessor<I, O> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private List<String> processorBeanNames;

    public CompositeItemProcessor(List<String> processorBeanNames) {
        this.processorBeanNames = processorBeanNames;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        List<ItemProcessor<I, O>> delegates = new ArrayList<>();
        for (String processorBeanName : processorBeanNames) {
            delegates.add(applicationContext.getBean(processorBeanName, ItemProcessor.class));
        }

        setDelegates(delegates);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
