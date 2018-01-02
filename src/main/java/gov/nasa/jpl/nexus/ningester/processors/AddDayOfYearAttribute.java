/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDayOfYearAttribute {

    private String regex;

    /**
     * Reuqires regex that defines EXACTLY one capturing group that contains the day of year when matched with the
     * granule name.
     *
     * @param regex Regex used to match against the granule name. Must define exactly one capturing group that captures
     *              the day of year.
     */
    @Autowired
    public AddDayOfYearAttribute(String regex) {
        this.regex = regex;
    }

    /**
     * Uses regex to extract a match from the granule name that contains the day of year.
     *
     * @param nexusTile The tile to process
     * @return The processed tile
     */
    public NexusContent.NexusTile setDayOfYearFromGranuleName(NexusContent.NexusTile nexusTile) {

        Pattern thePattern = Pattern.compile(this.regex);


        String granuleName = nexusTile.getSummary().getGranule();
        Matcher granuleNameMatcher = thePattern.matcher(granuleName);
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
        NexusContent.NexusTile.Builder newTileBuilder = NexusContent.NexusTile.newBuilder().mergeFrom(nexusTile);
        NexusContent.TileSummary.Builder newTileSummaryBuilder = newTileBuilder.getSummaryBuilder();
        newTileSummaryBuilder.addGlobalAttributes(
                NexusContent.Attribute.newBuilder()
                        .setName("day_of_year_i")
                        .addValues(dayOfYear)
                        .build()
        );
        newTileBuilder.setSummary(newTileSummaryBuilder);

        return newTileBuilder.build();

    }

}
