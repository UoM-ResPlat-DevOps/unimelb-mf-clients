# aterm-download

**aterm-download** (or **aterm-download.cmd** on Windows) script provides a command line tool to download data from Mediaflux server. 
It requires **aterm.jar**, which can be downloaded by the script automatically. Also it requires the configruation file: **mflux.cfg**, which includes the Mediaflux server details and user credentials.

## 1. Configuration

### aterm.jar
* The script downloads **aterm.jar** automatically to script directory.
* You can modify the script to specify a different location:
  * on Linux/Mac OS/Unix, modify aterm file:
    * **export MFLUX_ATERM=/path/to/aterm.jar**
  * on Windows, modify aterm.cmd file:
    * **SET MFLUX_ATERM=x:\path\to\aterm.jar**
  * On Unix systems you must make the script executable (with **chmod u+rwx <script>**) to execute it directly (else use the **source** command to execute it)

### mflux.cfg
* You **must** specify the Mediaflux server details and user credentails (or secure identity token) in the **mflux.cfg** file.
* the script will try the following locations to find **mflux.cfg**:
  1. use the file specified by **$MFLUX_CFG** (or **%MFLUX_CFG%** on Windows) if the file exists;
  2. use the file in **$HOME/.Arcitecta/mflux.cfg** (or **%USERPROFILE%/Arcitecta/mflux.cfg**) if the file exists
  3. use the file **mflux.cfg** in the script directory if exists.

## 2. Synopsis & Usage
```
aterm-download: 
	synopsis:
		Exports one or more assets using a specified profile.

	usage:
		aterm-download [<args>] <file> [<create-args>]

	arguments:
		-lp <local profile>
			[optional] A local profile (ecp) containing a specification for the export.
		-mode [test|live]
			[optional] Is this a test or a live export? Test export can be used to check whether a profile is correct. Defaults to 'live'.
		-ncsr <nb>
			[optional] The number of concurrent server requests. A number in the range [1,infinity].
			Defaults to 1. Concurrent requests can increase performance as data is downloaded parallel to request processing.
		-where <query>
			[optional] Query that will return the assets for export/download. Any query conforming to AQL is valid. Must be specified if 'namespace' argument is omitted.
		-namespace <namespace>
			[optional] The asset namespace to export. Must be specified if 'where' argument is omitted
		-onerror [abort|continue]
			[optional] If there is an export error, what should happen? Defaults to 'abort'.
		-onlocalerror [abort|continue]
			[optional] If there is an error accessing or opening a local file (e.g. permissions, etc), what should happen? Defaults to 'abort'.
		-task-name <task name>
			[optional] Specifies the custom name for the task that monitors the progress of the export. User may track the progress of the task by using server.task.named.describe :name <task name>.
		-task-remove-after <hours>
			[optional] Used to specify how many hours after the export is complete do we want the monitoring task to be removed from the system. Defaults to '0' hours, i.e. now.
		-task-batch-size <batch size>
			[optional] When used task that monitors the progress of the export will update the progress after 'task-batch-size' of work units were completed. Defaults to '100' work units.
		-task-count-assets <true|false>
			[optional] Specifies if the assets should be counted before the export begins. This is used by task that tracks the progress of the export so that it can know total number of work units (file transfers). Defaults to 'false'.
		-task-report-bytes <true|false>
			[optional] Specifies if the task should include bytes transferred as well when updating progress, not just assets transferred. If set to true, bytes transferred will be reported once every second. Defaults to 'false'.
		-verbose [true|false]
			[optional] If set to true, will display those files being consumed. Defaults to false.
		-export-empty-namespaces [true|false]
			[optional] Specifies whether or not to export empty namespaces. If set to true, folders will be created for empty namespaces. This only works in conjunction with -namespace argument. It will be ignored if either of -lp or -where arguments are provided. Defaults to false.
		-folder-layout [none|collection]
			[optional] Specifies the folder layout for exported files. Ignored if '-lp' provided. Defaults to 'collection'.
		-filename-collisions [skip|rename|overwrite]
			[optional] Specifies how to handle filename collisions. Ignored if '-lp' provided. Defaults to 'rename'.
		-ns-parent-number
			[optional] When folder layout is set to 'collection' this argument specifies the number of collection parents to include. Defaults to infinity, i.e. all parents.
```

## 3. Examples

### 3.1. Download data from a remote Mediaflux namespace(directory) to the a directory in local files system:
* On Mac OS, Linux or Unix:
  * **./aterm-download -namespace /projects/my-project /Users/wilson/Downloads**
* On Windows:
  * **aterm-download.cmd download -namespace /projects/my-project c:\users\wilson\Downloads**

### 3.2. Download data concurrently with multiple threads. 
* The commands below use 4 threads to download data. The number of threads should normally be less than the number of your CPU cores.
  * On Mac OS, Linux or Unix:
    * **./aterm-download -ncsr 4 -namespace /projects/my-project /Users/wilson/Downloads**
  * On Windows:
    * **aterm-download.cmd -ncsr 4 -namespace /projects/my-project c:\users\wilson\Downloads**

