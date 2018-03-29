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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

public class SliceFileByStepSizeTest {

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testGenerateChunkBoundrySlicesWithDivisibileTiles() {

        LinkedHashMap<String, Integer> dimensionToStepSize = new LinkedHashMap<>();
        dimensionToStepSize.put("lat", 4);
        dimensionToStepSize.put("lon", 4);

        SliceFileByStepSize slicer = new SliceFileByStepSize(dimensionToStepSize);

        Map<String, Integer> dimensionNameToLength = new LinkedHashMap<>();
        dimensionNameToLength.put("lat", 8);
        dimensionNameToLength.put("lon", 8);

        List<String> result = slicer.generateChunkBoundrySlices(dimensionNameToLength);

        assertEquals(4, result.size());

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

        LinkedHashMap<String, Integer> dimensionToStepSize = new LinkedHashMap<>();
        dimensionToStepSize.put("lat", 3);
        dimensionToStepSize.put("lon", 3);

        SliceFileByStepSize slicer = new SliceFileByStepSize(dimensionToStepSize);

        Map<String, Integer> dimensionNameToLength = new LinkedHashMap<>();
        dimensionNameToLength.put("lat", 8);
        dimensionNameToLength.put("lon", 8);

        List<String> result = slicer.generateChunkBoundrySlices(dimensionNameToLength);

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
    public void testSliceFileByStepSize() throws IOException {

        LinkedHashMap<String, Integer> dimensionToStepSize = new LinkedHashMap<>();
        dimensionToStepSize.put("time", 5832);
        dimensionToStepSize.put("rivid", 1);

        Integer expectedTiles = 43; // 1 river and all times per tile. 43 total rivers

        SliceFileByStepSize slicer = new SliceFileByStepSize(dimensionToStepSize);

        Resource testResource = new ClassPathResource("granules/Qout_WSWM_729days_p0_dtR900s_n1_preonly_20160416.split.nc");

        List<String> results = slicer.generateSlices(testResource.getFile());

        assertThat(results.size(), is(expectedTiles));

        assertThat(results.get(0), is("time:0:5832,rivid:0:1"));
        assertThat(results.get(results.size() - 1), is("time:0:5832,rivid:42:43"));

    }

    @Test
    public void testSliceFileByStepSizeThrowsExceptionWithUnkownDimension() throws IOException {
        LinkedHashMap<String, Integer> dimensionToStepSize = new LinkedHashMap<>();
        dimensionToStepSize.put("badDimension", 5832);

        SliceFileByStepSize slicer = new SliceFileByStepSize(dimensionToStepSize);

        Resource testResource = new ClassPathResource("granules/Qout_WSWM_729days_p0_dtR900s_n1_preonly_20160416.split.nc");

        exceptionGrabber.expect(AssertionError.class);
        slicer.generateSlices(testResource.getFile());

    }
}
