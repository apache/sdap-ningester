package gov.nasa.jpl.nexus.ningester.datatiler;

import org.apache.sdap.nexusproto.NexusTile;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class NetCDFItemReaderTest {

    @Test
    public void testOpen() {
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
    public void testReadOne() throws Exception {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));

        Resource testResource = new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc");
        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(testResource);

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        NexusTile result = reader.read();

        assertThat(result.getSummary().getSectionSpec(), is("lat:0:10,lon:0:20"));
        assertThat(result.getSummary().getGranule(), is(testResource.getURL().toString()));

    }

    @Test
    public void testReadAll() {
        Integer tilesDesired = 5184;

        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(tilesDesired);
        slicer.setDimensions(Arrays.asList("lat", "lon"));
        slicer.setTimeDimension("time");

        Resource testResource = new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc");
        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setResource(testResource);

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        List<NexusTile> results = new ArrayList<>();
        NexusTile result;
        while ((result = reader.read()) != null) {
            results.add(result);
        }

        assertThat(results.size(), is(tilesDesired));

    }

}
