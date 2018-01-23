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

import org.apache.sdap.nexusproto.Attribute;
import org.apache.sdap.nexusproto.NexusTile;
import org.apache.sdap.nexusproto.TileSummary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDayOfYearAttribute {

    private Pattern regex;

    /**
     * Reuqires regex that defines EXACTLY one capturing group that contains the day of year when matched with the
     * granule name.
     *
     * @param regex Regex used to match against the granule name. Must define exactly one capturing group that captures
     *              the day of year.
     */
    public AddDayOfYearAttribute(String regex) {
        this.regex = Pattern.compile(regex);
    }

    /**
     * Uses regex to extract a match from the granule name that contains the day of year.
     *
     * @param nexusTile The tile to process
     * @return The processed tile
     */
    public NexusTile setDayOfYearFromGranuleName(NexusTile nexusTile) {

        String granuleName = nexusTile.getSummary().getGranule();
        Matcher granuleNameMatcher = this.regex.matcher(granuleName);
        Boolean granuleNameMatched = granuleNameMatcher.find();

        if (!granuleNameMatched) {
            throw new RuntimeException("regex did not match granuleName.");
        }

        if (granuleNameMatcher.groupCount() != 1) {
            throw new RuntimeException("regex does not have exactly one capturing group.");
        }

        if (granuleNameMatcher.group(1).length() <= 0) {
            throw new RuntimeException("group does not contain match.");
        }


        String dayOfYear = granuleNameMatcher.group(1);
        NexusTile.Builder newTileBuilder = NexusTile.newBuilder().mergeFrom(nexusTile);
        TileSummary.Builder newTileSummaryBuilder = newTileBuilder.getSummaryBuilder();
        newTileSummaryBuilder.addGlobalAttributes(
                Attribute.newBuilder()
                        .setName("day_of_year_i")
                        .addValues(dayOfYear)
                        .build()
        );
        newTileBuilder.setSummary(newTileSummaryBuilder);

        return newTileBuilder.build();

    }

}
