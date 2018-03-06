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

import com.google.common.collect.Sets;
import ucar.nc2.Dimension;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SliceFileByTilesDesired implements FileSlicer {

    private Integer tilesDesired;
    private List<String> dimensions;
    private String timeDimension;

    public void setTilesDesired(Integer desired) {
        this.tilesDesired = desired;
    }

    public void setDimensions(List<String> dims) {
        this.dimensions = dims;
    }

    @Override
    public List<String> generateSlices(File inputfile) throws IOException {

        Integer timeLen = 0;
        Map<String, Integer> dimensionNameToLength;
        try (NetcdfDataset ds = NetcdfDataset.openDataset(inputfile.getAbsolutePath())) {


            dimensionNameToLength = ds.getDimensions().stream()
                    .filter(dimension -> this.dimensions.contains(dimension.getShortName()))
                    .sorted(Comparator.comparing(Dimension::getShortName, Comparator.comparingInt(dim -> this.dimensions.indexOf(dim))))
                    .collect(Collectors.toMap(Dimension::getShortName, Dimension::getLength,
                            (v1, v2) -> {
                                throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));
                            },
                            LinkedHashMap::new));

            if (this.timeDimension != null) {
                timeLen = ds.getDimensions().stream()
                        .filter(dimension -> this.timeDimension.equals(dimension.getShortName()))
                        .map(Dimension::getLength)
                        .collect(Collectors.toList()).get(0);
            }
        }

        return addTimeDimension(generateChunkBoundrySlices(tilesDesired, dimensionNameToLength), timeLen);

    }

    List<String> generateChunkBoundrySlices(Integer tilesDesired, Map<String, Integer> dimensionNameToLength) {

        List<Set<String>> dimensionBounds = dimensionNameToLength.entrySet().stream()
                .map(stringIntegerEntry -> {
                    String dimensionName = stringIntegerEntry.getKey();
                    Integer lengthOfDimension = stringIntegerEntry.getValue();
                    Integer stepSize = calculateStepSize(stringIntegerEntry.getValue(), tilesDesired, dimensionNameToLength.size());
                    Set<String> bounds = new LinkedHashSet<>();
                    for (int i = 0; i < lengthOfDimension; i += stepSize) {
                        bounds.add(
                                dimensionName + ":" +
                                        i + ":" +
                                        (i + stepSize >= lengthOfDimension ? lengthOfDimension : i + stepSize));
                    }
                    return bounds;
                }).collect(Collectors.toList());

        return Sets.cartesianProduct(dimensionBounds)
                .stream()
                .map(tileSpecAsList -> tileSpecAsList.stream().collect(Collectors.joining(",")))
                .collect(Collectors.toList());

    }

    List<String> addTimeDimension(List<String> specs, Integer timeLen) {

        if (timeLen > 0) {
            return specs.stream().map(sectionSpec ->
                    IntStream.range(0, timeLen)
                            .mapToObj(timeIndex -> this.timeDimension + ":" + timeIndex + ":" + (timeIndex + 1) + "," + sectionSpec)
                            .collect(Collectors.toList()))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } else {
            return specs;
        }
    }

    private Integer calculateStepSize(Integer lengthOfDimension, Integer chunksDesired, Integer numberOfDimensions) {
        return new Double(Math.floor(lengthOfDimension / (Math.pow(chunksDesired, (1.0 / numberOfDimensions))))).intValue();
    }

    public void setTimeDimension(String timeDimension) {
        this.timeDimension = timeDimension;
    }
}
