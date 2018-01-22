/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

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
