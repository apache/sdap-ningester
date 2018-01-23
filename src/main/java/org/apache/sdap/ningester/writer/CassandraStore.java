/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.sdap.ningester.writer;

import org.apache.sdap.nexusproto.NexusTile;
import org.apache.sdap.nexusproto.TileData;
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
    public void saveData(List<? extends NexusTile> nexusTiles) {

        String query = "insert into " + this.tableName + " (tile_id, tile_blob) VALUES (?, ?)";
        cassandraTemplate.ingest(query, nexusTiles.stream()
                .map(nexusTile -> getCassandraRowFromTileData(nexusTile.getTile()))
                .collect(Collectors.toList()));
    }

    private List<Object> getCassandraRowFromTileData(TileData tile) {

        UUID tileId = UUID.fromString(tile.getTileId());
        return Arrays.asList(tileId, ByteBuffer.wrap(tile.toByteArray()));
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
