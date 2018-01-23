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

package org.apache.sdap.ningester.writer;

import org.apache.sdap.nexusproto.NexusTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NexusWriter {

    private static final Logger log = LoggerFactory.getLogger(NexusWriter.class);
    private MetadataStore metadataStore;
    private DataStore dataStore;

    public NexusWriter(MetadataStore metadataStore, DataStore dataStore) {
        this.metadataStore = metadataStore;
        this.dataStore = dataStore;
    }

    public void saveToNexus(List<? extends NexusTile> nexusTiles) {
        if (nexusTiles.size() > 0) {
            metadataStore.saveMetadata(nexusTiles);

            try {
                dataStore.saveData(nexusTiles);

            } catch (RuntimeException e) {
                try {
                    metadataStore.deleteMetadata(nexusTiles);
                } catch (RuntimeException e2) {
                    log.error("During exception while saving data, could not rollback metadata", e2);
                }
                throw e;
            }
        }
    }

}
