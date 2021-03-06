```
USAGE:
    unimelb-mf-sync [OPTIONS] <dir1> <namespace1> [<dir2> <namespace2>]

DESCRIPTION:
    Synchronize local directory with remote asset namespace. The algorithm will ensure that the two end point sets of data are the same set, that is, the join of the two sets.  There are no deletions at either end

OPTIONS:
    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.
    --mf.host <host>                          Mediaflux server host.
    --mf.port <port>                          Mediaflux server port.
    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>          Mediaflux user credentials.
    --mf.token <token>                        Mediaflux secure identity token.
    --csum-check                              Files are equated if the name and size are the same. In addition, with this argument, you can optionally compute the CRC32 checksumk to decide if two files are the same.
    --nb-queriers <n>                         Number of query threads. Defaults to 1
    --nb-workers <n>                          Number of concurrent worker threads to download data. Defaults to 1
    --nb-retries <n>                          Retry times when error occurs. Defaults to 0
    --batch-size <size>                       Size of the query result. Defaults to 1000
    --daemon                                  Run as a daemon.
    --daemon-port <port>                      Daemon listener port if running as a daemon. Defaults to 9761
    --daemon-scan-interval <seconds>          Time interval (in seconds) between scans of source directories. Defaults to 60 seconds.
    --log-dir <dir>                           Path to the directory for log files. No logging if not specified.
    --notify <email-addresses>                When completes, send email notification to the recipients(comma-separated email addresses if multiple). Not applicable for daemon mode.
    --quiet                                   Do not print progress messages.
    --help                                    Prints usage.

POSITIONAL ARGUMENTS:
    <dir>                                     Local directory path.
    <namespace>                               Corresponding remote asset namespace path.

    unimelb-mf-sync --mf.config ~/.Arcitecta/mflux.cfg --nb-queriers 2 --nb-workers 4 ~/Documents/foo /projects/proj-1128.1.59/foo
```
