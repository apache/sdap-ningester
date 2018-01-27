

Docker command to run a job (Docker-for-Mac only). Replace `<path to ningester>` with the path on your local workstation to the ningester github project. 

    docker run -it --rm -v <path to ningester>/docker/example_config/:/config/ -v <path to ningester>/src/test/resources/granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc:/data/data.nc sdap/ningester dockermachost,solr,cassandra