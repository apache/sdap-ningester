/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import java.util.UUID;

public class GenerateTileId {

    private String salt = "";

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public NexusContent.NexusTile setTileId(NexusContent.NexusTile inputTile) {

        NexusContent.NexusTile.Builder outTileBuilder = NexusContent.NexusTile.newBuilder().mergeFrom(inputTile);
        String granuleFileName = inputTile.getSummary().getGranule();
        String granuleName = granuleFileName.substring(0, granuleFileName.length() - 3);
        String spec = inputTile.getSummary().getSectionSpec();

        String tileId = UUID.nameUUIDFromBytes((granuleName + spec + salt).getBytes()).toString();
        outTileBuilder.getSummaryBuilder().setTileId(tileId);
        outTileBuilder.getTileBuilder().setTileId(tileId);

        return outTileBuilder.build();
    }


}

