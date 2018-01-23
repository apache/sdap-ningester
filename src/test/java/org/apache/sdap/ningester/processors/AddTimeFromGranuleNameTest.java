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

import org.apache.sdap.nexusproto.GridTile;
import org.apache.sdap.nexusproto.NexusTile;
import org.apache.sdap.nexusproto.TileData;
import org.apache.sdap.nexusproto.TileSummary;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;


public class AddTimeFromGranuleNameTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSuccessfulMatch() {
        String regex = "^.*(\\d{7})\\.";
        String dateFormat = "yyyyDDD";

        String granuleName = "A2012001.L3m_DAY_NSST_sst_4km.nc";
        Long expectedTime = 1325376000L; // 01/01/2012 00:00:00 in epoch time
        NexusTile nexusTile = NexusTile.newBuilder().setSummary(
                TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).setTile(
                TileData.newBuilder()
                        .setGridTile(
                                GridTile.newBuilder(

                                ).build()
                        ).build()
        ).build();

        AddTimeFromGranuleName processor = new AddTimeFromGranuleName(regex, dateFormat);

        NexusTile result = processor.setTimeFromGranuleName(nexusTile);

        assertThat(result.getTile().getGridTile().getTime(), is(expectedTime));
        assertThat(result.getSummary().getStats().getMinTime(), is(expectedTime));
        assertThat(result.getSummary().getStats().getMaxTime(), is(expectedTime));
    }

    @Test
    public void testUnparseable() {
        String regex = "^.*(\\d{7})\\.";
        String dateFormat = "yyyyDDDss";

        String granuleName = "A2012001.L3m_DAY_NSST_sst_4km.nc";

        thrown.expect(RuntimeException.class);
        thrown.expectCause(isA(ParseException.class));

        NexusTile nexusTile = NexusTile.newBuilder().setSummary(
                TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).setTile(
                TileData.newBuilder()
                        .setGridTile(
                                GridTile.newBuilder(

                                ).build()
                        ).build()
        ).build();

        AddTimeFromGranuleName processor = new AddTimeFromGranuleName(regex, dateFormat);

        NexusTile result = processor.setTimeFromGranuleName(nexusTile);

    }
}