package gov.nasa.jpl.nexus.ningester.datatiler;

import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NetCDFItemReaderTest  {

    @Test
    public void testOpen() throws IOException {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));

        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setNetCDFFile(new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc").getFile());

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        assertTrue(context.containsKey(NetCDFItemReader.CURRENT_TILE_SPEC_INDEX_KEY));
    }

    @Test
    public void testRead() throws Exception {
        SliceFileByTilesDesired slicer = new SliceFileByTilesDesired();
        slicer.setTilesDesired(5184);
        slicer.setDimensions(Arrays.asList("lat", "lon"));

        NetCDFItemReader reader = new NetCDFItemReader(slicer);
        reader.setNetCDFFile(new ClassPathResource("granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc").getFile());

        ExecutionContext context = new ExecutionContext();
        reader.open(context);

        String result = reader.read();

        assertNotNull(result);

    }
}
