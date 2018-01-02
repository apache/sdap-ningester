/*****************************************************************************
* Copyright (c) 2018 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*****************************************************************************/
package gov.nasa.jpl.nexus.ningester.processors;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddTimeFromGranuleName{


    private Pattern regex;
    private SimpleDateFormat dateFormat;

    public AddTimeFromGranuleName(String regex, String dateFormat) {
        this.regex = Pattern.compile(regex);
        this.dateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public NexusContent.NexusTile setTimeFromGranuleName(NexusContent.NexusTile inputTile) {

        NexusContent.NexusTile.Builder outTileBuilder = NexusContent.NexusTile.newBuilder().mergeFrom(inputTile);

        switch(inputTile.getTile().getTileTypeCase()){
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