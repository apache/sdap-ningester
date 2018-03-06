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

public class SliceFileByStepSize implements FileSlicer {

    private LinkedHashMap<String, Integer> dimensionToStepSize = new LinkedHashMap<>();
    private List<String> orderedDimensions = new ArrayList<>();

    public SliceFileByStepSize(LinkedHashMap<String, Integer> dimensionToStepSize) {
        this.dimensionToStepSize.putAll(dimensionToStepSize);

        this.dimensionToStepSize.forEach((dimension, stepSize) -> orderedDimensions.add(dimension));
    }


    @Override
    public List<String> generateSlices(File inputfile) throws IOException {

        Map<String, Integer> dimensionNameToLength;
        try (NetcdfDataset ds = NetcdfDataset.openDataset(inputfile.getAbsolutePath())) {
            List<String> dimensionNames = ds.getDimensions().stream().map(Dimension::getShortName).collect(Collectors.toList());
            assert dimensionNames.containsAll(dimensionToStepSize.keySet()) : String
                    .format("Slice by dimensions must be present in dataset. Dimensions in dataset are %s. Dimensions provided %s",
                            dimensionNames, dimensionToStepSize.keySet());

            dimensionNameToLength = ds.getDimensions().stream()
                    .filter(dimension -> this.dimensionToStepSize.keySet().contains(dimension.getShortName()))
                    .sorted(Comparator.comparing(Dimension::getShortName, Comparator.comparingInt(dim -> this.orderedDimensions.indexOf(dim))))
                    .collect(Collectors.toMap(Dimension::getShortName, Dimension::getLength,
                            (v1, v2) -> {
                                throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));
                            },
                            LinkedHashMap::new));

        }


        return generateChunkBoundrySlices(dimensionNameToLength);
    }

    List<String> generateChunkBoundrySlices(Map<String, Integer> dimensionNameToLength) {

        List<Set<String>> dimensionBounds = dimensionNameToLength.entrySet().stream()
                .map(stringIntegerEntry -> {
                    String dimensionName = stringIntegerEntry.getKey();
                    Integer lengthOfDimension = stringIntegerEntry.getValue();
                    Integer stepSize = this.dimensionToStepSize.get(dimensionName);
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
}
