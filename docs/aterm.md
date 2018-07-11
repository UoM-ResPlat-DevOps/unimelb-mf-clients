# aterm

**aterm** (or **aterm.cmd** for Windows) script provides a command line Mediaflux terminal. Via **aterm** you can execute any Mediaflux service or built-in commands. (e.g. **download**, **import**, **source**)
It requires file: **aterm.jar**, which can be downloaded by this script automatically. It also requires the configruation file: **mflux.cfg**, which includes the Mediaflux server details and user credentials.

## 1. Configuration

### aterm.jar
* The script downloads **aterm.jar** automatically to script directory.
* You can modify the script to specify a different location:
  * on Linux/Mac OS/Unix, modify aterm file:
    * **export MFLUX_ATERM=/path/to/aterm.jar**
  * on Windows, modify aterm.cmd file:
    * **SET MFLUX_ATERM=x:\path\to\aterm.jar**

### mflux.cfg
* You **must** specify the Mediaflux server details and user credentails (or secure identity token) in the **mflux.cfg** file.
* the script will try the following locations to find **mflux.cfg**:
  1. use the file specified by **$MFLUX_CFG** (or **%MFLUX_CFG%** on Windows) if the file exists;
  2. use the file in **$HOME/.Arcitecta/mflux.cfg** (or **%USERPROFILE%/Arcitecta/mflux.cfg**) if the file exists
  3. use the file **mflux.cfg** in the script directory if exists.

## 2. Examples

### 2.1. Enter command line terminal, simply 
* On Mac OS, Linux or Unix:
  * **./aterm**
* On Windows:
  * **aterm.cmd**
* Type **quit** to quit the terminal.
### 2.2. Execute a Mediaflux service:
* On Mac OS, Linux or Unix:
  * **./aterm server.uuid**
* On Windows:
  * **aterm.cmd server.uuid**
### 2.3. Execute download command:
* On Mac OS, Linux or Unix:
  * **./aterm download -namespace /projects/my-project /Users/wilson/Downloads**
* On Windows:
  * **aterm.cmd download -namespace /projects/my-project c:\users\wilson\Downloads**
### 2.4. Execute import command:
* On Mac OS, Linux or Unix:
  * **./aterm import -namespace /projects/my-project /Users/wilson/dir-to-upload**
* On Windows:
  * **aterm.cmd import -namespace /projects/my-project c:\users\wilson\dir-to-upload**

## 3. Other scripts

* **NOTE:** 
  * **aterm-download** is equivalent to 
    * `aterm download`
  * **aterm-import** is equivalent to 
    * `aterm import`

