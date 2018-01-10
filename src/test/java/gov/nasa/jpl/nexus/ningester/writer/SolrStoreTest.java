/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;

import static org.junit.Assert.assertEquals;

public class SolrStoreTest {

    @Test
    public void testGetSolrDocFromTileSummary() {
        SolrStore solrStore = new SolrStore(null);

        NexusContent.TileSummary tileSummary = NexusContent.TileSummary.newBuilder()
                .setTileId("1")
                .setBbox(NexusContent.TileSummary.BBox.newBuilder()
                        .setLatMin(51)
                        .setLatMax(55)
                        .setLonMin(22)
                        .setLonMax(30)
                        .build())
                .setDatasetName("test")
                .setDatasetUuid("4")
                .setDataVarName("sst")
                .setGranule("test.nc")
                .setSectionSpec("0:1,0:1")
                .setStats(NexusContent.TileSummary.DataStats.newBuilder()
                        .setCount(10)
                        .setMax(50)
                        .setMin(50)
                        .setMean(50)
                        .setMaxTime(1429142399)
                        .setMinTime(1429142399)
                        .build())
                .build();

        SolrInputDocument doc = solrStore.getSolrDocFromTileSummary(tileSummary);

        assertEquals("2015-04-15T23:59:59Z", doc.get("tile_min_time_dt").getValue());
        assertEquals("2015-04-15T23:59:59Z", doc.get("tile_max_time_dt").getValue());
        assertEquals("sea_surface_temp", doc.get("table_s").getValue());
        assertEquals("POLYGON((22.000 51.000, 30.000 51.000, 30.000 55.000, 22.000 55.000, 22.000 51.000))", doc.get("geo").getValue());
        assertEquals("1", doc.get("id").getValue());
        assertEquals("4", doc.get("dataset_id_s").getValue());
        assertEquals("0:1,0:1", doc.get("sectionSpec_s").getValue());
        assertEquals("test", doc.get("dataset_s").getValue());
        assertEquals("test.nc", doc.get("granule_s").getValue());
        assertEquals("sst", doc.get("tile_var_name_s").getValue());
        assertEquals(22.0f, (Float) doc.get("tile_min_lon").getValue(), 0.01f);
        assertEquals(30.0f, (Float) doc.get("tile_max_lon").getValue(), 0.01f);
        assertEquals(51.0f, (Float) doc.get("tile_min_lat").getValue(), 0.01f);
        assertEquals(55.0f, (Float) doc.get("tile_max_lat").getValue(), 0.01f);
        assertEquals(50.0f, (Float) doc.get("tile_min_val_d").getValue(), 0.01f);
        assertEquals(50.0f, (Float) doc.get("tile_max_val_d").getValue(), 0.01f);
        assertEquals(50.0f, (Float) doc.get("tile_avg_val_d").getValue(), 0.01f);
        assertEquals(10, doc.get("tile_count_i").getValue());
        assertEquals("test!1", doc.get("solr_id_s").getValue());
    }
}
