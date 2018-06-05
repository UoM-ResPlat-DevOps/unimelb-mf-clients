```
USAGE:
    unimelb-mf-check [OPTIONS] --direction <up|down|both> --output <output.csv> <dir1> <namespace1> [<dir2> <namespace2>...]

DESCRIPTION:
    Compare files in local diretory with assets in remote asset namespace.

OPTIONS:
    --mf.config <mflux.cfg>                   Path to the config file that contains Mediaflux server details and user credentials.
    --mf.host <host>                          Mediaflux server host.
    --mf.port <port>                          Mediaflux server port.
    --mf.transport <https|http|tcp/ip>        Mediaflux server transport, can be http, https or tcp/ip.
    --mf.auth <domain,user,password>          Mediaflux user credentials.
    --mf.token <token>                        Mediaflux secure identity token.
    --direction <up|down|both>                Direction(up/down/both).
    -o, --output <output.csv>                 Output CSV file.
    --detailed-output                         Include all files checked. Otherwise, only the missing or invalid files are included in the output.
    --no-csum-check                           Do not generate and compare CRC32 checksum.
    --nb-queriers <n>                         Number of query threads. Defaults to 1
    --nb-workers <n>                          Number of concurrent worker threads to read local file (to generate checksum) if needed. Defaults to 1
    --nb-retries <n>                          Retry times when error occurs. Defaults to 0
    --batch-size <size>                       Size of the query result. Defaults to 1000
    --quiet                                   Do not print progress messages.

POSITIONAL ARGUMENTS:
    <dir>                                     Local directory path.
    <namespace>                               Remote Mediaflux namespace path.

EXAMPLES:
    unimelb-mf-check --mf.config ~/.Arcitecta/mflux.cfg --direction down --output ~/Documents/foo-download-check.csv ~/Documents/foo /projects/proj-1.2.3/foo
```
