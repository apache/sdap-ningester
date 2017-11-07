package gov.nasa.jpl.nexus.ningester.datatiler;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import ucar.ma2.Array;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetCDFItemReader implements ItemReader<String>, ItemStream {

    private static final Logger log = LoggerFactory.getLogger(NetCDFItemReader.class);

    static final String CURRENT_TILE_SPEC_INDEX_KEY = "current.tile.spec.index";

    private List<String> tileSpecList;
    private Integer currentTileSpecIndex;

    private File netCDFFile;
    private NetcdfDataset ds;
    private FileSlicer fileSlicer;

    /**
     * Constructor
     *
     * @param fileSlicer Object responsible for slicing the NetCDF file into tiles.
     */
    @Autowired
    public NetCDFItemReader(FileSlicer fileSlicer) {
        this.fileSlicer = fileSlicer;
    }

    @Autowired
    public void setNetCDFFile(File netCDFFile) {
        this.netCDFFile = netCDFFile;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        String currentSpec = this.tileSpecList.get(this.currentTileSpecIndex);
        Map<String, String> dimensionToSpec = Arrays.stream(currentSpec.split(","))
                .collect(Collectors.toMap(
                        dimension -> dimension.split(":")[0],
                        dimension -> dimension.substring(dimension.indexOf(":") + 1, dimension.length())));

        Variable varToRead = this.ds.getVariables().get(0);
        String spec = varToRead.getDimensions().stream()
                        .map(dimension -> dimensionToSpec.get(dimension.getShortName()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(","));

        spec = ":," + spec;
        Array data = this.ds.getVariables().get(0).read(spec);

        this.currentTileSpecIndex++;
        return data.toString();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        //Every time we open the file we generate the tile specs according to the given file slicer
        this.tileSpecList = fileSlicer.generateSlices(this.netCDFFile);
        log.debug("Generated tile specifications for {}\nINDEX\tTILE SPECIFICATION\n{}", this.netCDFFile.getName(),
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
}
