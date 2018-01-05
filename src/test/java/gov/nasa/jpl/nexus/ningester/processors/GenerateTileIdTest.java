/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.junit.Test;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GenerateTileIdTest {


    @Test
    public void testGenerateId() {

        String granuleFileName = "file:/path/to/some/file/19960421120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc";
        String sectionSpec = "time:0:1,lat:140:160,lon:640:680";

        String expectedId = "c031a9c4-9e1d-32e9-9d5c-d2497ce74920";

        NexusContent.NexusTile.Builder inputTileBuilder = NexusContent.NexusTile.newBuilder();
        inputTileBuilder.getSummaryBuilder().setGranule(granuleFileName);
        inputTileBuilder.getSummaryBuilder().setSectionSpec(sectionSpec);
        inputTileBuilder.setTile(NexusContent.TileData.newBuilder());

        GenerateTileId processor = new GenerateTileId();

        NexusContent.NexusTile result = processor.addTileId(inputTileBuilder.build());

        assertThat(result.getSummary().getTileId(), is(expectedId));
        assertThat(result.getTile().getTileId(), is(expectedId));

    }

    @Test
    public void testGenerateIdWithSalt() {

        String granuleFileName = "file:/path/to/some/file/CCMP_Wind_Analysis_19990928_V02.0_L3.0_RSS.nc";
        String sectionSpec = "time:3:4,longitude:174:261,latitude:152:190";
        String salt = "wind_u";

        String expectedId = "48da50ef-e92c-3562-89f9-470561a06482";

        NexusContent.NexusTile.Builder inputTileBuilder = NexusContent.NexusTile.newBuilder();
        inputTileBuilder.getSummaryBuilder().setGranule(granuleFileName);
        inputTileBuilder.getSummaryBuilder().setSectionSpec(sectionSpec);
        inputTileBuilder.setTile(NexusContent.TileData.newBuilder());

        GenerateTileId processor = new GenerateTileId();
        processor.setSalt(salt);

        NexusContent.NexusTile result = processor.addTileId(inputTileBuilder.build());

        assertThat(result.getSummary().getTileId(), is(expectedId));
        assertThat(result.getTile().getTileId(), is(expectedId));

    }

}

