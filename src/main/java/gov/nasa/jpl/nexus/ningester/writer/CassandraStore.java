/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.springframework.data.cassandra.core.CassandraOperations;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CassandraStore implements DataStore {

    private CassandraOperations cassandraTemplate;

    //TODO This will be refactored at some point to be dynamic per-message. Or maybe per-group.
    private String tableName = "sea_surface_temp";

    public CassandraStore(CassandraOperations cassandraTemplate) {
        this.cassandraTemplate = cassandraTemplate;
    }

    @Override
    public void saveData(List<? extends NexusContent.NexusTile> nexusTiles) {

        String query = "insert into " + this.tableName + " (tile_id, tile_blob) VALUES (?, ?)";
        cassandraTemplate.ingest(query, nexusTiles.stream()
                .map(nexusTile -> getCassandraRowFromTileData(nexusTile.getTile()))
                .collect(Collectors.toList()));
    }

    private List<Object> getCassandraRowFromTileData(NexusContent.TileData tile) {

        UUID tileId = UUID.fromString(tile.getTileId());
        return Arrays.asList(tileId, ByteBuffer.wrap(tile.toByteArray()));
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
