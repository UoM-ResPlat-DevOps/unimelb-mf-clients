```
USAGE:
    unimelb-mf-download [OPTIONS] --out <dst-dir> <namespace1> [<namespace2>...]

DESCRIPTION:
    Download data from Mediaflux.

OPTIONS:
    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.
    --mf.host <host>                          Mediaflux server host.
    --mf.port <port>                          Mediaflux server port.
    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>          Mediaflux user credentials.
    --mf.token <token>                        Mediaflux secure identity token.
    -o, --out <dst-dir>                       The output/destination directory.
    --overwrite                               Overwrite if the dst file exists.
    --unarchive                               Extract Arcitecta .aar files.
    --csum-check                              If file exists, generate CRC32 checksum and compare with asset checksum before overwriting.
    --nb-queriers <n>                         Number of query threads. Defaults to 1
    --nb-workers <n>                          Number of concurrent worker threads to download data. Defaults to 1
    --nb-retries <n>                          Retry times when error occurs. Defaults to 0
    --batch-size <size>                       Size of the query result. Defaults to 1000
    --daemon                                  Run as a daemon.
    --daemon-port <port>                      Daemon listener port if running as a daemon. Defaults to 9761
    --daemon-scan-interval <seconds>          Time interval between scans of source asset namespaces. Defaults to 60
    --log-dir <dir>                           Path to the directory for log files. No logging if not specified.
    --quiet                                   Do not print progress messages.

POSITIONAL ARGUMENTS:
    <namespace>                               The asset namespace to download.

EXAMPLES:
    unimelb-mf-download --mf.config ~/.Arcitecta/mflux.cfg --nb-workers 2  --out ~/Downloads /projects/proj-1128.1.59/foo /projects/proj-1128.1.59/bar
```
