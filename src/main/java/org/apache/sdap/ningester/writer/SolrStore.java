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
import org.apache.sdap.nexusproto.TileSummary;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrOperations;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SolrStore implements MetadataStore {

    //TODO This will be refactored at some point to be dynamic per-message. Or maybe per-group.
    private static final String TABLE_NAME = "sea_surface_temp";
    private static final SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        iso.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private Integer commitWithin = 1000;
    private Integer geoPrecision = 3;
    private String collection = "nexustiles";
    private SolrOperations solr;
    private Logger log = LoggerFactory.getLogger(SolrStore.class);

    public SolrStore(SolrOperations solr) {
        this.solr = solr;
    }

    @Override
    public void saveMetadata(List<? extends NexusTile> nexusTiles) {

        List<SolrInputDocument> solrdocs = nexusTiles.stream()
                .map(nexusTile -> getSolrDocFromTileSummary(nexusTile.getSummary()))
                .collect(Collectors.toList());
        solr.saveDocuments(this.collection, solrdocs, commitWithin);
    }

    @Override
    public void deleteMetadata(List<? extends NexusTile> nexusTiles) {

        List<String> tileIds = nexusTiles.stream()
                .map(nexusTile -> nexusTile.getSummary().getDatasetName() + "!" + nexusTile.getSummary().getTileId())
                .collect(Collectors.toList());
        solr.deleteById(this.collection, tileIds);
    }

    public SolrInputDocument getSolrDocFromTileSummary(TileSummary summary) {

        TileSummary.BBox bbox = summary.getBbox();
        TileSummary.DataStats stats = summary.getStats();

        Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startCal.setTime(new Date(stats.getMinTime() * 1000));
        Calendar endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        endCal.setTime(new Date(stats.getMaxTime() * 1000));

        String minTime = iso.format(startCal.getTime());
        String maxTime = iso.format(endCal.getTime());

        String geo = determineGeo(summary);

        String granuleFileName = Paths.get(summary.getGranule()).getFileName().toString();

        SolrInputDocument inputDocument = new SolrInputDocument();
        inputDocument.addField("table_s", TABLE_NAME);
        inputDocument.addField("geo", geo);
        inputDocument.addField("id", summary.getTileId());
        inputDocument.addField("solr_id_s", summary.getDatasetName() + "!" + summary.getTileId());
        inputDocument.addField("dataset_id_s", summary.getDatasetUuid());
        inputDocument.addField("sectionSpec_s", summary.getSectionSpec());
        inputDocument.addField("dataset_s", summary.getDatasetName());
        inputDocument.addField("granule_s", granuleFileName);
        inputDocument.addField("tile_var_name_s", summary.getDataVarName());
        inputDocument.addField("tile_min_lon", bbox.getLonMin());
        inputDocument.addField("tile_max_lon", bbox.getLonMax());
        inputDocument.addField("tile_min_lat", bbox.getLatMin());
        inputDocument.addField("tile_max_lat", bbox.getLatMax());
        inputDocument.addField("tile_min_time_dt", minTime);
        inputDocument.addField("tile_max_time_dt", maxTime);
        inputDocument.addField("tile_min_val_d", stats.getMin());
        inputDocument.addField("tile_max_val_d", stats.getMax());
        inputDocument.addField("tile_avg_val_d", stats.getMean());
        inputDocument.addField("tile_count_i", Long.valueOf(stats.getCount()).intValue());

        summary.getGlobalAttributesList().forEach(attribute ->
                inputDocument.addField(attribute.getName(), attribute.getValuesCount() == 1 ? attribute.getValues(0) : attribute.getValuesList())
        );
        return inputDocument;
    }

    private String determineGeo(TileSummary summary) {
        //Solr cannot index a POLYGON where all corners are the same point or when there are only 2 distinct points (line).
        //Solr is configured for a specific precision so we need to round to that precision before checking equality.
        Integer geoPrecision = this.geoPrecision;

        BigDecimal latMin = BigDecimal.valueOf(summary.getBbox().getLatMin()).setScale(geoPrecision, BigDecimal.ROUND_HALF_UP);
        BigDecimal latMax = BigDecimal.valueOf(summary.getBbox().getLatMax()).setScale(geoPrecision, BigDecimal.ROUND_HALF_UP);
        BigDecimal lonMin = BigDecimal.valueOf(summary.getBbox().getLonMin()).setScale(geoPrecision, BigDecimal.ROUND_HALF_UP);
        BigDecimal lonMax = BigDecimal.valueOf(summary.getBbox().getLonMax()).setScale(geoPrecision, BigDecimal.ROUND_HALF_UP);

        String geo;
        //If lat min = lat max and lon min = lon max, index the 'geo' bounding box as a POINT instead of a POLYGON
        if (latMin.equals(latMax) && lonMin.equals(lonMax)) {
            geo = "POINT(" + lonMin + " " + latMin + ")";
            log.debug("{}\t{}[{}] geo={}", summary.getTileId(), summary.getGranule(), summary.getSectionSpec(), geo);
        }
        //If lat min = lat max but lon min != lon max, then we essentially have a line.
        else if (latMin.equals(latMax)) {
            geo = "LINESTRING (" + lonMin + " " + latMin + ", " + lonMax + " " + latMin + ")";
            log.debug("{}\t{}[{}] geo={}", summary.getTileId(), summary.getGranule(), summary.getSectionSpec(), geo);
        }
        //Same if lon min = lon max but lat min != lat max
        else if (lonMin.equals(lonMax)) {
            geo = "LINESTRING (" + lonMin + " " + latMin + ", " + lonMin + " " + latMax + ")";
            log.debug("{}\t{}[{}] geo={}", summary.getTileId(), summary.getGranule(), summary.getSectionSpec(), geo);
        }
        //All other cases should use POLYGON
        else {
            geo = "POLYGON((" +
                    lonMin + " " + latMin + ", " +
                    lonMax + " " + latMin + ", " +
                    lonMax + " " + latMax + ", " +
                    lonMin + " " + latMax + ", " +
                    lonMin + " " + latMin + "))";
        }

        return geo;
    }

    private SolrInputDocument toSolrInputDocument(Map<String, Object> doc) {
        SolrInputDocument solrDoc = new SolrInputDocument();

        solrDoc.putAll(doc.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            SolrInputField field = new SolrInputField(entry.getKey());
            field.setValue(entry.getValue(), 0);
            return field;
        })));

        return solrDoc;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setCommitWithin(Integer commitWithin) {
        this.commitWithin = commitWithin;
    }

    public void setGeoPrecision(Integer geoPrecision) {
        this.geoPrecision = geoPrecision;
    }
}
