/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

public class AddDayOfYearAttributeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSuccessfulMatch() {
        String regex = "^(\\d{3})";
        String granuleName = "001.L4_5day_avhrr_clim_sst_pixelMean.nc";
        NexusContent.NexusTile nexusTile = NexusContent.NexusTile.newBuilder().setSummary(
                NexusContent.TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).build();

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(regex);

        NexusContent.NexusTile result = processor.setDayOfYearFromGranuleName(nexusTile);

        assertThat(result.getSummary().getGlobalAttributesList(), contains(
                hasProperty("name", is("day_of_year_i"))
        ));

        String actualDayOfYear = result.getSummary().getGlobalAttributes(0).getValues(0);
        assertThat(actualDayOfYear, is("001"));
    }

    @Test()
    public void testUnsuccessfulMatch() {

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("regex did not match granuleName.");

        String regex = "^(\\d{3})";
        String granuleName = "L4_5day_avhrr_clim_sst_pixelMean.nc";
        NexusContent.NexusTile nexusTile = NexusContent.NexusTile.newBuilder().setSummary(
                NexusContent.TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).build();

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(regex);

        processor.setDayOfYearFromGranuleName(nexusTile);
    }

    @Test()
    public void testUnsuccessfulGroupMatch() {

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("regex does not have exactly one capturing group.");

        String regex = "^\\d{3}";
        String granuleName = "001.L4_5day_avhrr_clim_sst_pixelMean.nc";
        NexusContent.NexusTile nexusTile = NexusContent.NexusTile.newBuilder().setSummary(
                NexusContent.TileSummary.newBuilder()
                        .setGranule(granuleName)
                        .build()
        ).build();

        AddDayOfYearAttribute processor = new AddDayOfYearAttribute(regex);

        processor.setDayOfYearFromGranuleName(nexusTile);
    }

}
