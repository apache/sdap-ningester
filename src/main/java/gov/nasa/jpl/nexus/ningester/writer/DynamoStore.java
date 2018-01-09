/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.jpl.nexus.ningester.writer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import java.util.Collection;

/**
 * Created by djsilvan on 6/26/17.
 */
public class DynamoStore implements DataStore {

    private DynamoDB dynamoDB;
    private String tableName;
    private String primaryKey;

    public DynamoStore(AmazonDynamoDB dynamoClient, String tableName, String primaryKey) {
        dynamoDB = new DynamoDB(dynamoClient);
        this.tableName = tableName;
        this.primaryKey = primaryKey;
    }

    public void saveData(Collection<NexusContent.NexusTile> nexusTiles) {

        Table table = dynamoDB.getTable(tableName);

        for (NexusContent.NexusTile tile : nexusTiles) {
            String tileId = getTileId(tile);
            byte[] tileData = getTileData(tile);

            try {
                table.putItem(new Item().withPrimaryKey(primaryKey, tileId).withBinary("data", tileData));
            } catch (Exception e) {
                throw new DataStoreException("Unable to add item: " + tileId, e);
            }
        }
    }

    private String getTileId(NexusContent.NexusTile tile) {
        return tile.getTile().getTileId();
    }

    private byte[] getTileData(NexusContent.NexusTile tile) {
        return tile.getTile().toByteArray();
    }
}
