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
package org.apache.sdap.ningester.datatiler;

import org.apache.sdap.nexusproto.NexusTile;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class NetCDFItemReaderTest {

    @Test
    public void testOpen() {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));

        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc"));

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        assertTrue(context.containsKey(NetCDFItemReader.CURRENT_TILE_SPEC_INDEX_KEY));
    }

    @Test
    public void testReadOne() throws Exception {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));

        Resource testResource = new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc");
        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(testResource);

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        NexusTile result = reader.read();

        assertThat(result.getSummary().getSectionSpec(), is("lat:0:10,lon:0:20"));
        assertThat(result.getSummary().getGranule(), is(testResource.getURL().toString()));

    }

    @Test
    public void testReadAll() {
        Integer tilesDesired = 5184;

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(tilesDesired);
        slicer.setDimensions(Arrays.asList("lat", "lon"));
        slicer.setTimeDimension("time");

        Resource testResource = new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc");
        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(testResource);

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        List<NexusTile> results = new ArrayList<>();
        NexusTile result;
        while ((result = reader.read()) != null) {
            results.add(result);
        }

        assertThat(results.size(), is(tilesDesired));

    }

}
