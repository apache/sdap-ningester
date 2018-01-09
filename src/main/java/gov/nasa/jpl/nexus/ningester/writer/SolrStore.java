/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.writer;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.nasa.jpl.nexus.ingest.wiretypes.NexusContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrOperations;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SolrStore implements MetadataStore {
    private Environment environment;
    private SolrOperations solr;

    private Logger log = LoggerFactory.getLogger(SolrStore.class);

    //TODO This will be refactored at some point to be dynamic per-message. Or maybe per-group.
    private String tableName = "sea_surface_temp";

    private static final SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        iso.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public SolrStore(SolrOperations solr) {
        this.solr = solr;
    }

    @Resource
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void saveMetadata(Collection<NexusContent.NexusTile> nexusTiles) {

        List<SolrInputDocument> solrdocs = nexusTiles.stream()
                .map(nexusTile -> getSolrDocFromTileSummary(nexusTile.getSummary()))
                .collect(Collectors.toList());
        solr.saveDocuments(solrdocs, environment.getProperty("solrCommitWithin", Integer.class, 1000));
    }

    public SolrInputDocument getSolrDocFromTileSummary(NexusContent.TileSummary summary) {

        NexusContent.TileSummary.BBox bbox = summary.getBbox();
        NexusContent.TileSummary.DataStats stats = summary.getStats();

        Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startCal.setTime(new Date(stats.getMinTime() * 1000));
        Calendar endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        endCal.setTime(new Date(stats.getMaxTime() * 1000));

        String minTime = iso.format(startCal.getTime());
        String maxTime = iso.format(endCal.getTime());

        String geo = determineGeo(summary);

        String granuleFileName = Paths.get(summary.getGranule()).getFileName().toString();

        Map<String, Object> doc = new HashMap<>();
        doc.put("table_s", tableName);
        doc.put("geo", geo);
        doc.put("id", summary.getTileId());
        doc.put("solr_id_s", summary.getDatasetName() + "!" + summary.getTileId());
        doc.put("dataset_id_s", summary.getDatasetUuid());
        doc.put("sectionSpec_s", summary.getSectionSpec());
        doc.put("dataset_s", summary.getDatasetName());
        doc.put("granule_s", granuleFileName);
        doc.put("tile_var_name_s", summary.getDataVarName());
        doc.put("tile_min_lon", bbox.getLonMin());
        doc.put("tile_max_lon", bbox.getLonMax());
        doc.put("tile_min_lat", bbox.getLatMin());
        doc.put("tile_max_lat", bbox.getLatMax());
        doc.put("tile_min_time_dt", minTime);
        doc.put("tile_max_time_dt", maxTime);
        doc.put("tile_min_val_d", stats.getMin());
        doc.put("tile_max_val_d", stats.getMax());
        doc.put("tile_avg_val_d", stats.getMean());
        doc.put("tile_count_i", Long.valueOf(stats.getCount()).intValue());

        summary.getGlobalAttributesList().forEach(attribute ->
                doc.put(attribute.getName(), attribute.getValuesCount() == 1 ? attribute.getValues(0) : attribute.getValuesList())
        );

        return toSolrInputDocument(doc);
    }

    private String determineGeo(NexusContent.TileSummary summary) {
        //Solr cannot index a POLYGON where all corners are the same point or when there are only 2 distinct points (line).
        //Solr is configured for a specific precision so we need to round to that precision before checking equality.
        Integer geoPrecision = environment.getProperty("solrGeoPrecision", Integer.class, 3);

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
}
