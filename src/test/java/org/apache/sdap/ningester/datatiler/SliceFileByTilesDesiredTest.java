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

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

public class SliceFileByTilesDesiredTest {

    @Test
    public void testGenerateChunkBoundrySlicesWithDivisibileTiles() {

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();

        Integer tilesDesired = 4;

        Map<String, Integer> dimensionNameToLength = new LinkedHashMap<>();
        dimensionNameToLength.put("lat", 8);
        dimensionNameToLength.put("lon", 8);

        List<String> result = slicer.generateChunkBoundrySlices(tilesDesired, dimensionNameToLength);

        assertEquals(tilesDesired.intValue(), result.size());

        String[] expected = new String[]{
                "lat:0:4,lon:0:4",
                "lat:0:4,lon:4:8",
                "lat:4:8,lon:0:4",
                "lat:4:8,lon:4:8"};
        assertThat(result, containsInAnyOrder(expected));
        assertThat(result, contains(expected));

    }

    @Test
    public void testGenerateChunkBoundrySlicesWithNonDivisibileTiles() {

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();

        Integer tilesDesired = 5;

        Map<String, Integer> dimensionNameToLength = new LinkedHashMap<>();
        dimensionNameToLength.put("lat", 8);
        dimensionNameToLength.put("lon", 8);

        List<String> result = slicer.generateChunkBoundrySlices(tilesDesired, dimensionNameToLength);

        assertEquals(9, result.size());

        String[] expected = new String[]{
                "lat:0:3,lon:0:3",
                "lat:0:3,lon:3:6",
                "lat:0:3,lon:6:8",
                "lat:3:6,lon:0:3",
                "lat:3:6,lon:3:6",
                "lat:3:6,lon:6:8",
                "lat:6:8,lon:0:3",
                "lat:6:8,lon:3:6",
                "lat:6:8,lon:6:8"};
        assertThat(result, containsInAnyOrder(expected));
        assertThat(result, contains(expected));

    }

    @Test
    public void testAddTimeDimension() {

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTimeDimension("time");

        Integer tilesDesired = 4;
        Integer timeLen = 3;

        Map<String, Integer> dimensionNameToLength = new LinkedHashMap<>();
        dimensionNameToLength.put("lat", 8);
        dimensionNameToLength.put("lon", 8);

        List<String> result = slicer.generateChunkBoundrySlices(tilesDesired, dimensionNameToLength);
        result = slicer.addTimeDimension(result, timeLen);

        assertEquals(tilesDesired * timeLen, result.size());

        String[] expected = new String[]{
                "time:0:1,lat:0:4,lon:0:4",
                "time:1:2,lat:0:4,lon:0:4",
                "time:2:3,lat:0:4,lon:0:4",

                "time:0:1,lat:0:4,lon:4:8",
                "time:1:2,lat:0:4,lon:4:8",
                "time:2:3,lat:0:4,lon:4:8",

                "time:0:1,lat:4:8,lon:0:4",
                "time:1:2,lat:4:8,lon:0:4",
                "time:2:3,lat:4:8,lon:0:4",

                "time:0:1,lat:4:8,lon:4:8",
                "time:1:2,lat:4:8,lon:4:8",
                "time:2:3,lat:4:8,lon:4:8"};
        assertThat(result, containsInAnyOrder(expected));
        assertThat(result, contains(expected));

    }

    @Test
    public void testGenerateChunkBoundrySlicesWithMurDimensions() {

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();

        Integer tilesDesired = 5184;

        Map<String, Integer> dimensionNameToLength = new LinkedHashMap<>();
        dimensionNameToLength.put("lat", 17999);
        dimensionNameToLength.put("lon", 36000);

        List<String> result = slicer.generateChunkBoundrySlices(tilesDesired, dimensionNameToLength);

        assertEquals(tilesDesired + 72, result.size());

        assertThat(result, hasItems("lat:0:249,lon:0:500", "lat:0:249,lon:500:1000", "lat:17928:17999,lon:35500:36000"));

    }

    @Test
    public void testAddTimeDimensionWithMurDimensionsAndTime() {

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();

        Integer tilesDesired = 5184;

        Map<String, Integer> dimensionNameToLength = new LinkedHashMap<>();
        dimensionNameToLength.put("lat", 17999);
        dimensionNameToLength.put("lon", 36000);

        slicer.setTimeDimension("time");

        List<String> result = slicer.generateChunkBoundrySlices(tilesDesired, dimensionNameToLength);
        result = slicer.addTimeDimension(result, 1);

        assertEquals(tilesDesired + 72, result.size());

        assertThat(result, hasItems("time:0:1,lat:0:249,lon:0:500", "time:0:1,lat:0:249,lon:500:1000", "time:0:1,lat:17928:17999,lon:35500:36000"));

    }

    @Test
    public void testGenerateSlicesCcmp() throws IOException {
        Integer tilesDesired = 270;
        Integer expectedTiles = 289 * 4; // 4 time slices and 289 tiles per time slice

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(tilesDesired);
        slicer.setDimensions(Arrays.asList("latitude", "longitude"));
        slicer.setTimeDimension("time");

        Resource testResource = new ClassPathResource("granules/CCMP_Wind_Analysis_20050101_V02.0_L3.0_RSS.nc");

        List<String> results = slicer.generateSlices(testResource.getFile());

        assertThat(results.size(), is(expectedTiles));

        assertThat(results.get(0), is("time:0:1,latitude:0:38,longitude:0:87"));
        assertThat(results.get(results.size() - 1), is("time:3:4,latitude:608:628,longitude:1392:1440"));

    }

    @Test
    public void testDimensionsOrderedByInput() throws IOException {
        Integer tilesDesired = 270;
        Integer expectedTiles = 289 * 4; // 4 time slices and 289 tiles per time slice

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(tilesDesired);
        slicer.setDimensions(Arrays.asList("longitude", "latitude"));
        slicer.setTimeDimension("time");

        Resource testResource = new ClassPathResource("granules/CCMP_Wind_Analysis_20050101_V02.0_L3.0_RSS.nc");

        List<String> results = slicer.generateSlices(testResource.getFile());

        assertThat(results.size(), is(expectedTiles));

        assertThat(results.get(0), is("time:0:1,longitude:0:87,latitude:0:38"));
        assertThat(results.get(results.size() - 1), is("time:3:4,longitude:1392:1440,latitude:608:628"));

    }
}
