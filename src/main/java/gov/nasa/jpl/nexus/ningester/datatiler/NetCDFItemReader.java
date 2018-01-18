package gov.nasa.jpl.nexus.ningester.datatiler;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetCDFItemReader implements ResourceAwareItemReaderItemStream<NexusContent.NexusTile> {

    static final String CURRENT_TILE_SPEC_INDEX_KEY = "current.tile.spec.index";
    private static final Logger log = LoggerFactory.getLogger(NetCDFItemReader.class);
    private List<String> tileSpecList;
    private Integer currentTileSpecIndex;

    private Resource netCDFResource;
    private NetcdfDataset ds;
    private FileSlicer fileSlicer;

    /**
     * Constructor
     *
     * @param fileSlicer Object responsible for slicing the NetCDF file into tiles.
     */
    public NetCDFItemReader(FileSlicer fileSlicer) {
        this.fileSlicer = fileSlicer;
    }

    @Override
    public NexusContent.NexusTile read() {
        if (this.currentTileSpecIndex == this.tileSpecList.size()) {
            //End of stream
            return null;
        }
        String currentSpec = this.tileSpecList.get(this.currentTileSpecIndex);

        URL netCDFUrl = null;
        try {
            netCDFUrl = this.netCDFResource.getURL();
        } catch (IOException e) {
            throw new UnexpectedInputException("Generic IOException", e);
        }

        NexusContent.NexusTile.Builder nexusTileBuilder = NexusContent.NexusTile.newBuilder();
        nexusTileBuilder.getSummaryBuilder()
                .setSectionSpec(currentSpec)
                .setGranule(netCDFUrl.toString());
//        Map<String, String> dimensionToSpec = Arrays.stream(currentSpec.split(","))
//                .collect(Collectors.toMap(
//                        dimension -> dimension.split(":")[0],
//                        dimension -> dimension.substring(dimension.indexOf(":") + 1, dimension.length())));
//
//        Variable varToRead = this.ds.getVariables().get(0);
//        String spec = varToRead.getDimensions().stream()
//                .map(dimension -> dimensionToSpec.get(dimension.getShortName()))
//                .filter(Objects::nonNull)
//                .collect(Collectors.joining(","));
//
//        spec = ":," + spec;
//        Array data = this.ds.getVariables().get(0).read(spec);

        this.currentTileSpecIndex++;
        return nexusTileBuilder.build();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        File netCDFFile = null;
        try {
            netCDFFile = this.netCDFResource.getFile();
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }

        //Every time we open the file we generate the tile specs according to the given file slicer
        try {
            this.tileSpecList = fileSlicer.generateSlices(netCDFFile);
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }
        log.debug("Generated tile specifications for {}\nINDEX\tTILE SPECIFICATION\n{}", netCDFFile.getName(),
                IntStream.range(0, this.tileSpecList.size())
                        .mapToObj(i -> i + "\t" + this.tileSpecList.get(i))
                        .collect(Collectors.joining("\n")));

        if (!executionContext.containsKey(CURRENT_TILE_SPEC_INDEX_KEY)) {
            //Start at index 0
            this.currentTileSpecIndex = 0;
            executionContext.putInt(CURRENT_TILE_SPEC_INDEX_KEY, this.currentTileSpecIndex);
        } else {
            //Start at index location from context
            this.currentTileSpecIndex = executionContext.getInt(CURRENT_TILE_SPEC_INDEX_KEY);
        }

        //Open the resource
        try {
            this.ds = NetcdfDataset.openDataset(netCDFFile.getAbsolutePath());
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

        executionContext.putInt(CURRENT_TILE_SPEC_INDEX_KEY, this.currentTileSpecIndex);
    }

    @Override
    public void close() throws ItemStreamException {

        try {
            this.ds.close();
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }

    }

    @Override
    public void setResource(Resource resource) {
        this.netCDFResource = resource;
    }
}
