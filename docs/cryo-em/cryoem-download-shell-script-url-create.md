### cryoem-download-shell-script-url-create

This is a shell script to execute **unimelb.asset.download.shell.script.create** service on Mediaflux server to generate a sharable url to download shell scripts to download specify data collections in Mediaflux.

#### I. Install

1. Run the following command in Linux terminal to download the script
```
wget https://raw.githubusercontent.com/UoM-ResPlat-DevOps/unimelb-mf-scripts/master/unix/cryo-em/cryoem-download-shell-script-url-create
```
or if you are on Mac OS and wget is not available, run the following command in terminal:
```
curl -o cryoem-download-shell-script-url-create https://raw.githubusercontent.com/UoM-ResPlat-DevOps/unimelb-mf-scripts/master/unix/cryo-em/cryoem-download-shell-script-url-create
```
2. Set the script to be executable
```
chmod +x cryoem-download-shell-script-url-create
```

#### II. Usage
```Usage:
    cryoem-download-shell-script-url-create [-h|--help] [--expire-days <number-of-days>] [--ncsr <ncsr>] [--overwrite] [--quiet] <namespace>

Options:
    -h | --help                       prints usage.
    --email <addresses>               specify the email recipient of the generated url. Can be comma-separated if there are more than one.
    --expire-days <number-of-days>    expiry of the auth token. Defaults to 14 days.
    --overwrite                       overwrite if output file exists.
    --quiet                           do not print output message.

Positional arguments:
    <namespace>                       Mediaflux asset namespace to be downloaded by the scripts. Can be multiple, but must be from the same project.

Examples:
    cryoem-download-shell-script-url-create --email user1@unimelb.edu.au --expire-days 10 proj-abc-1128.4.999/RAW_DATA proj-abc-1128.4.999/PROCESSED_DATA
```
