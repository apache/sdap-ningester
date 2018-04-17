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
import org.apache.sdap.nexusproto.TileSummary;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

public class AddDayOfYearAttributeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSuccessfulMatch() {
        String regex = "^.*(\\d{3})";
        String granuleName = "file:/some/path/001.L4_5day_avhrr_clim_sst_pixelMean.nc";
        NexusTile nexusTile = NexusTile.newBuilder().setSummary(
                TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).build();

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(regex);

        NexusTile result = processor.setDayOfYearFromGranuleName(nexusTile);

        assertThat(result.getSummary().getGlobalAttributesList(), contains(
                hasProperty("name", is("day_of_year_i"))
        ));

        String actualDayOfYear = result.getSummary().getGlobalAttributes(0).getValues(0);
        assertThat(actualDayOfYear, is("001"));
    }

    @Test()
    public void testUnsuccessfulMatch() {

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("regex did not match granuleName.");

        String regex = "^(\\d{3})";
        String granuleName = "L4_5day_avhrr_clim_sst_pixelMean.nc";
        NexusTile nexusTile = NexusTile.newBuilder().setSummary(
                TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).build();

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(regex);

        processor.setDayOfYearFromGranuleName(nexusTile);
    }

    @Test()
    public void testUnsuccessfulGroupMatch() {

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("regex does not have exactly one capturing group.");

        String regex = "^\\d{3}";
        String granuleName = "001.L4_5day_avhrr_clim_sst_pixelMean.nc";
        NexusTile nexusTile = NexusTile.newBuilder().setSummary(
                TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).build();

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(regex);

        processor.setDayOfYearFromGranuleName(nexusTile);
    }

}
