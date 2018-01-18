/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.jpl.nexus.ningester.writer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent.NexusTile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by djsilvan on 6/26/17.
 */
public class S3Store implements DataStore {

    private AmazonS3 s3;
    private String bucketName;

    public S3Store(AmazonS3Client s3client, String bucketName) {
        s3 = s3client;
        this.bucketName = bucketName;
    }

    public void saveData(List<? extends NexusTile> nexusTiles) {

        for (NexusTile tile : nexusTiles) {
            String tileId = getTileId(tile);
            byte[] tileData = getTileData(tile);
            Long contentLength = (long) tileData.length;
            InputStream stream = new ByteArrayInputStream(tileData);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(contentLength);

            try {
                s3.putObject(new PutObjectRequest(bucketName, tileId, stream, meta));
            } catch (AmazonServiceException ase) {
                throw new DataStoreException("Caught an AmazonServiceException, which means your request made it "
                        + "to Amazon S3, but was rejected with an error response for some reason.", ase);
            } catch (AmazonClientException ace) {
                throw new DataStoreException("Caught an AmazonClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with S3, "
                        + "such as not being able to access the network.", ace);
            }
        }
    }

    private String getTileId(NexusTile tile) {
        return tile.getTile().getTileId();
    }

    private byte[] getTileData(NexusTile tile) {
        return tile.getTile().toByteArray();
    }
}
