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


package org.apache.sdap.ningester.processors;

import org.apache.sdap.nexusproto.NexusTile;
import org.apache.sdap.nexusproto.TileData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GenerateTileIdTest {


    @Test
    public void testGenerateId() {

        String granuleFileName = "file:/path/to/some/file/19960421120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc";
        String sectionSpec = "time:0:1,lat:140:160,lon:640:680";

        String expectedId = "c031a9c4-9e1d-32e9-9d5c-d2497ce74920";

        NexusTile.Builder inputTileBuilder = NexusTile.newBuilder();
        inputTileBuilder.getSummaryBuilder().setGranule(granuleFileName);
        inputTileBuilder.getSummaryBuilder().setSectionSpec(sectionSpec);
        inputTileBuilder.setTile(TileData.newBuilder());

        GenerateTileId processor = new GenerateTileId();

        NexusTile result = processor.addTileId(inputTileBuilder.build());

        assertThat(result.getSummary().getTileId(), is(expectedId));
        assertThat(result.getTile().getTileId(), is(expectedId));

    }

    @Test
    public void testGenerateIdWithSalt() {

        String granuleFileName = "file:/path/to/some/file/CCMP_Wind_Analysis_19990928_V02.0_L3.0_RSS.nc";
        String sectionSpec = "time:3:4,longitude:174:261,latitude:152:190";
        String salt = "wind_u";

        String expectedId = "48da50ef-e92c-3562-89f9-470561a06482";

        NexusTile.Builder inputTileBuilder = NexusTile.newBuilder();
        inputTileBuilder.getSummaryBuilder().setGranule(granuleFileName);
        inputTileBuilder.getSummaryBuilder().setSectionSpec(sectionSpec);
        inputTileBuilder.setTile(TileData.newBuilder());

        GenerateTileId processor = new GenerateTileId();
        processor.setSalt(salt);

        NexusTile result = processor.addTileId(inputTileBuilder.build());

        assertThat(result.getSummary().getTileId(), is(expectedId));
        assertThat(result.getTile().getTileId(), is(expectedId));

    }

}

