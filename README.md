# unimelb-mf-clients
A set of command line utilities to manipulate data in Mediaflux . It consists of tools to upload/download/verify data in Mediaflux system.

---

## I. Utilities (developed by Resplat@UniMelb)

### 1. unimelb-mf-upload
A command line tool to upload data to Mediaflux.
* **[Manual for unimelb-mf-upload](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/unimelb-mf-upload.md)**

---
### 2. unimelb-mf-download
A command line tool to download data from Mediaflux.
* **[Manual for unimelb-mf-download](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/unimelb-mf-download.md)**

---
### 3. unimelb-mf-check
A command line tool to compare files in local directory with the assets in remote asset namespace in Mediaflux.
* **[Manual for unimelb-mf-check](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/unimelb-mf-check.md)**

---
## II. Mediaflux Aterm Based Utilities

### 1. aterm
**aterm** (or **aterm.cmd** on Windows) is a script provides a command line Mediaflux terminal. Via **aterm** you can execute any Mediaflux service or built-in commands. (e.g. **download**, **import**, **source**)
* **[Manual for aterm](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/docs/aterm.md)**

## III. Configuration

* **Configuration file**
**mflux.cfg** file: is the configuration file for specifying Mediaflux connection details and user credentails. See sample [mflux.cfg](https://github.com/UoM-ResPlat-DevOps/unimelb-mf-clients/blob/master/src/main/config/samples/mflux.cfg) file.
  * All the command line utitlities above supports it;
  * To specify the location of **mflux.cfg** file, try one of the approaches below:
    - put **mflux.cfg** into the default location:
      * On Unix, save it to **$HOME/.Arcitecta/mflux.cfg** 
      * On Windows, save it to **%userprofile%\.Arcitecta\mflux.cfg**
    - or set environment variable MFLUX_CFG to be the location of the file before execute the command:
      * On Unix, 
        - `export MFLUX_CFG=/path/to/mflux.cfg` 
      * On Windows, 
        - `setx MFLUX_CFG=X:\path\to\mflux.cfg`
    - or insert argument `--mf.config /path/to/mflux.cfg` to the command.

* **Console interactive login**
All the above utitlites will prompt interactive login in the command line interface, if Mediaflux server details or user credentails are not specified from the configuration file or command arguments.




