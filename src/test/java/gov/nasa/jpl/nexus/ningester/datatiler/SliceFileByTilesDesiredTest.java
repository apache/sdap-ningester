/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.datatiler;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
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
}
