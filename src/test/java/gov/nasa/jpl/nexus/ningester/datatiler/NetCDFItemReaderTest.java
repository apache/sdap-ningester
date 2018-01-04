package gov.nasa.jpl.nexus.ningester.datatiler;

import org.junit.Test;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NetCDFItemReaderTest  {

    @Test
    public void testOpen() throws IOException {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));

        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc"));

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        assertTrue(context.containsKey(NetCDFItemReader.CURRENT_TILE_SPEC_INDEX_KEY));
    }

    @Test
    public void testRead() throws Exception {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));

        Resource testResource = new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc");
        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(testResource);

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        NexusContent.NexusTile result = reader.read();

        assertThat(result.getSummary().getSectionSpec(), is("lat:0:10,lon:0:20"));
        assertThat(result.getSummary().getGranule(), is(testResource.getURL().toString()));

    }

    @Test
    public void testReadWithTime() throws Exception {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));
        slicer.setTimeDimension("time");

        Resource testResource = new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc");
        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(testResource);

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        NexusContent.NexusTile result = reader.read();

        assertThat(result.getSummary().getSectionSpec(), is("time:0:1,lat:0:10,lon:0:20"));
        assertThat(result.getSummary().getGranule(), is(testResource.getURL().toString()));

    }
}
