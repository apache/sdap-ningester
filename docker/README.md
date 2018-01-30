GitHub repository: https://github.com/apache/incubator-sdap-ningester

# How to run this image

The basic command is:

    docker run -it --rm -v <absolute path to config directory on host>:/config/ -v <absolute path to granule on host>:/data/<granule name> sdap/ningester <profiles to activate>

Replacing the following:

  - `<absolute path to config directory on host>` should be the absolute path on the host to the configuration for the job
  - `<absolute path to granule on host>` should be the absolute path on the host to the granule intended for ingestion
  - `<granule name>` should be the filename of the granule
  - `<profiles to activate>` is a comma-separated list of profiles to activate
  
The [ExampleJob](example_config/ExampleJob.yml) file shows an example Job configuration that would ingest an AVHRR granule.

## Configuration

Upon running the image, the ningester job will scan the `/config` directory for any files that end with the `.yml` extension. Specifically it uses find:

    find /config -name "*.yml" | awk -vORS=, '{ print $1 }'
    
Therefore, to configure the job, mount your configuration files into `/config` using a Docker volume. Alternatively, configuration is loaded via Spring Boot's [relaxed binding rules](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-relaxed-binding).
So, you can also configure the job through environment variables where each 'level' of the yaml file gets replaced by an '_'.

For example, given a configuration option in yaml that looks like:

    ningester:
        tile_slicer: sliceFileByTilesDesired
        sliceFileByTilesDesired:
          tilesDesired: 1296
          timeDimension: time
          dimensions:
            - lat
            - lon

These could be replaced with the following corresponding Environment variables:

    NINGESTER_TILE_SLICER=sliceFileByTilesDesired
    NINGESTER_SLICE_FILE_BY_TILES_DESIRED_TILES_DESIRED=1296
    NINGESTER_SLICE_FILE_BY_TILES_DESIRED_TIME_DIMENSION=time
    NINGESTER_SLICE_FILE_BY_TILES_DESIRED_DIMENSIONS[0]=lat
    NINGESTER_SLICE_FILE_BY_TILES_DESIRED_DIMENSIONS[1]=lon
    
However, because ningester has a lot of configuration options, it is recommended to use the yaml option.

## Data

Ningester is designed to ingest 1 granule per run. It looks for the granule to ingest in the `/data` directory of the container image.
Use a Docker volume to mount your data into `/data`.  

The image relies on this command to find the first file in `/data` and it will use that file for ingestion:

    find /data -type f -print -quit

## Examples

A few example commands are shared here.

### Docker for Mac

The [ConnectionSettings-DockerForMac](example_config/ConnectionSettings-DockerForMac.yml) file shows an example of how to configure the connection settings
when running this job under Docker for Mac with Solr and Cassandra running on your host Mac.

Replace `<path to ningester>` with the path on your local workstation to the ningester github project. 

    docker run -it --rm -v <path to ningester>/docker/example_config/:/config/ -v <path to ningester>/src/test/resources/granules/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc:/data/20050101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc.nc sdap/ningester dockermachost,solr,cassandra