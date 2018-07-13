GitHub repository: https://github.com/apache/incubator-sdap-ningester

# How to run this image

This image is very similar to the [non-aws image](../README.md). The difference is that the data files and configuration files are not mounted as volumes, instead they are downloaded from S3. 

The basic command is:

    docker run -it --rm -v <path to aws credentials>:/home/ningester/.aws/credentials sdap/ningester:aws <location to configuration on s3> <location to data on s3> <profiles to activate>

Replacing the following:

  - `<location to configuration on s3>` should be an s3:// Url to the configuration for the job hosted in S3
  - `<path to aws credentials>` should be the absolute path on the host to the aws credential files that has access to the S3 bucket/object. This can also be specified using [environment variables](https://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html)
  - `<location to data on s3>` should be an s3:// Url to the location of the data file hosted in S3
  - `<granule name>` should be the filename of the granule
  - `<profiles to activate>` is a comma-separated list of profiles to activate
  
The [ExampleJob](example_config/ExampleJob.yml) file shows an example Job configuration that would ingest an AVHRR granule.

## Examples

A few example commands are shared here.

### Docker for Mac

The [ConnectionSettings-DockerForMac](example_config/ConnectionSettings-DockerForMac.yml) file shows an example of how to configure the connection settings
when running this job under Docker for Mac with Solr and Cassandra running on your host Mac.

Replace `<path to ningester>` with the path on your local workstation to the ningester github project.
This assumes you have valid AWS credentials located at `~/.aws/credentials` on your host machine
Replace `<bucket name>` with the name of an S3 bucket that contains data and config

    docker run -it --rm -v ~/.aws/credentials:/home/ningester/.aws/credentials sdap/ningester:aws.1.0.0-SNAPSHOT s3://<bucket name>/avhrr/avhrr-oi.yml s3://<bucket name>/avhrr/20150101120000-NCEI-L4_GHRSST-SSTblend-AVHRR_OI-GLOB-v02.0-fv02.0.nc aws,solr,cassandra
