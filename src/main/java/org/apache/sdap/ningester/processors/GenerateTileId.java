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


package org.apache.sdap.ningester.processors;

import com.google.common.io.Files;
import org.apache.sdap.nexusproto.NexusTile;

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

    public NexusTile addTileId(NexusTile inputTile) {

        NexusTile.Builder outTileBuilder = NexusTile.newBuilder().mergeFrom(inputTile);
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

