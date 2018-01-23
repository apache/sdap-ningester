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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddTimeFromGranuleName {


    private Pattern regex;
    private SimpleDateFormat dateFormat;

    public AddTimeFromGranuleName(String regex, String dateFormat) {
        this.regex = Pattern.compile(regex);
        this.dateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public NexusTile setTimeFromGranuleName(NexusTile inputTile) {

        NexusTile.Builder outTileBuilder = NexusTile.newBuilder().mergeFrom(inputTile);

        switch (inputTile.getTile().getTileTypeCase()) {
            case GRID_TILE:

                String granuleName = inputTile.getSummary().getGranule();
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

                String dateTimeString = granuleNameMatcher.group(1);
                Date dateTime = null;
                try {
                    dateTime = dateFormat.parse(dateTimeString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                Long secondsSinceEpoch = (dateTime.getTime() / 1000);

                outTileBuilder.getTileBuilder().getGridTileBuilder().setTime(secondsSinceEpoch);
                outTileBuilder.getSummaryBuilder().getStatsBuilder().setMinTime(secondsSinceEpoch);
                outTileBuilder.getSummaryBuilder().getStatsBuilder().setMaxTime(secondsSinceEpoch);
                break;
            default:
                throw new UnsupportedOperationException("Can only handle GridTile at this time.");
        }

        return outTileBuilder.build();
    }
}