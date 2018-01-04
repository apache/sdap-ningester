/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import com.google.common.io.Files;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class GenerateTileId {

    private String salt = "";

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public NexusContent.NexusTile addTileId(NexusContent.NexusTile inputTile){

        NexusContent.NexusTile.Builder outTileBuilder = NexusContent.NexusTile.newBuilder().mergeFrom(inputTile);
        String granuleFileName = inputTile.getSummary().getGranule();
        Path granulePath = null;
        try {
            granulePath = Paths.get(new URI(granuleFileName));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String granuleName = Files.getNameWithoutExtension(granulePath.getFileName().toString());
        String spec = inputTile.getSummary().getSectionSpec();

        String tileId = UUID.nameUUIDFromBytes((granuleName + spec + salt).getBytes()).toString();
        outTileBuilder.getSummaryBuilder().setTileId(tileId);
        outTileBuilder.getTileBuilder().setTileId(tileId);

        return outTileBuilder.build();
    }


}

