# unimelb-mf-clients

## I. Utilities (developed by Resplat@UniMelb)

* **mflux.cfg** file: All the **unimelb-mf-** tools and **aterm** tools supports argument **--mf.config** to specify Mediaflux server details and user credentails. See sample [mflux.cfg](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/src/main/config/samples/mflux.cfg) file.

### 1. unimelb-mf-download

A command line tool to download data from Mediaflux.

* **[Command manual for unimelb-mf-download](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/unimelb-mf-download.md)**

### 2. unimelb-mf-upload

A command line tool to upload data to Mediaflux.

* **[Command manual for unimelb-mf-upload](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/unimelb-mf-upload.md)**

### 3. unimelb-mf-sync

A command line tool to do two-way (upload&download) synchronization between local directory and remote asset namespace in Mediaflux.

* **[Command manual for unimelb-mf-sync](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/unimelb-mf-sync.md)**

### 4. unimelb-mf-check

A command line tool to compare files in local directory with the assets in remote asset namespace in Mediaflux.

* **[Command manual for unimelb-mf-check](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/unimelb-mf-check.md)**


## II. Mediaflux Aterm Based Utilities

### 1. aterm
**aterm** (or **aterm.cmd** on Windows) is a script provides a command line Mediaflux terminal. Via **aterm** you can execute any Mediaflux service or built-in commands. (e.g. **download**, **import**, **source**)

  * [User guide for aterm](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/aterm.md)

### 2. aterm-download
**aterm-download** (or **aterm-download.cmd** on Windows) script provides a command line tool to download data from Mediaflux server. 

  * [User guide for aterm-download](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/aterm-download.md)





