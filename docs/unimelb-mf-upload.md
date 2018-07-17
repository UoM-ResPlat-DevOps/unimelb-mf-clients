```
USAGE:
    unimelb-mf-upload [OPTIONS] --namespace <dst-namespace> <src-dir1> [<src-dir2>...]

DESCRIPTION:
    Upload local files to Mediaflux.  If the file pre-exists in Mediaflux and is the same as that being uploaded, the Mediaflux asset is not modified.  However, if the files are different, then a new version of the asset will be created.   In Daemon mode, the process will only upload new files since the process last executed.

OPTIONS:
    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.
    --mf.host <host>                          Mediaflux server host.
    --mf.port <port>                          Mediaflux server port.
    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>          Mediaflux user credentials.
    --mf.token <token>                        Mediaflux secure identity token.
    --namespace <dst-namespace>               The destination asset namespace in Mediaflux.
    --csum-check                              If enabled, computes the checksum from the uploaded file and compares with that computed by the server for the Mediaflux asset.
    --nb-queriers <n>                         Number of query threads. Defaults to 1
    --nb-workers <n>                          Number of concurrent worker threads to download data. Defaults to 1
    --nb-retries <n>                          Retry times when error occurs. Defaults to 0
    --batch-size <size>                       Size of the query result. Defaults to 1000
    --daemon                                  Run as a daemon.
    --daemon-port <port>                      Daemon listener port if running as a daemon. Defaults to 9761
    --daemon-scan-interval <seconds>          Time interval (in seconds) between scans of source directories. Defaults to 60 seconds.
    --log-dir <dir>                           Path to the directory for log files. No logging if not specified.
    --notify <email-addresses>                When completes, send email notification to the recipients(comma-separated email addresses if multiple). Not applicable for daemon mode.
    --sync-delete-assets                      Delete assets that do not have corresponding local files exist.
    --hard-delete-assets                      Force the asset deletion (see --sync-delete-assets) process to hard delete assets.  Otherwise, the behaviour is controlled by server properties (whether a deletion is a soft or hard action).
    --quiet                                   Do not print progress messages.
    --help                                    Prints usage.

POSITIONAL ARGUMENTS:
    <src-dir>                                 Source directory to upload.

EXAMPLES:
    unimelb-mf-upload --mf.config ~/.Arcitecta/mflux.cfg --nb-queriers 2 --nb-workers 4  --namespace /projects/proj-1128.1.59 ~/Documents/foo ~/Documents/bar
    ```
