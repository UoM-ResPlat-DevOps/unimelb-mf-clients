```
USAGE:
    unimelb-mf-upload [OPTIONS] --namespace <dst-namespace> <src-dir1> [<src-dir2>...]

DESCRIPTION:
    Upload local files to Mediaflux.

OPTIONS:
    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.
    --mf.host <host>                          Mediaflux server host.
    --mf.port <port>                          Mediaflux server port.
    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>          Mediaflux user credentials.
    --mf.token <token>                        Mediaflux secure identity token.
    --namespace <dst-namespace>               The destination asset namespace in Mediaflux.
    --csum-check                              If file exists, generate CRC32 checksum and compare with asset checksum before overwriting.
    --nb-queriers <n>                         Number of query threads. Defaults to 1
    --nb-workers <n>                          Number of concurrent worker threads to download data. Defaults to 1
    --nb-retries <n>                          Retry times when error occurs. Defaults to 0
    --batch-size <size>                       Size of the query result. Defaults to 1000
    --daemon                                  Run as a daemon.
    --daemon-port <port>                      Daemon listener port if running as a daemon. Defaults to 9761
    --daemon-scan-interval <seconds>          Time interval between scans of source directories. Defaults to 60
    --quiet                                   Do not print progress messages.

POSITIONAL ARGUMENTS:
    <src-dir>                                 Source directory to upload.

EXAMPLES:
    unimelb-mf-upload --mf.config ~/.Arcitecta/mflux.cfg --nb-queriers 2 --nb-workers 4  --namespace /projects/proj-1128.1.59 ~/Documents/foo ~/Documents/bar
    ```
