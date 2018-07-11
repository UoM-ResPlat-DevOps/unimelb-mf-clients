# aterm-import

**aterm-import** (or **aterm-import.cmd** on Windows) script is a command line tool to import(upload) local data files into Mediaflux server. 
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
aterm-import: 
	synopsis:
		Imports one or more files using a specified profile.

	usage:
		aterm-import [<args>] <file> [<create-args>]

	arguments:
		-archive <level>
			[optional] If specified, the given file/directory will be packaged as an AAR archive at the given compression level. 
			-archive cannot be specified if using a profile. Level in the range [0..9].
		-analyze <true|false>
			[optional] Used to control whether content analysis is enabled or not. Defaults to 'true'.
		-lp <local profile>
			[optional] A local profile (file) containing a specification for importation.
		-mode [test|live]
			[optional] Is this a test or a live import? Test import can be used to check whether a profile is correct. Defaults to 'live'.
		-ncsr <nb>
			[optional] The number of concurrent server requests. A number in the range [1,infinity].
			Defaults to 1. Concurrent requests can increase performance as data is uploaded parallel to request processing.
		-name <name>
			[optional] If importing as an archive, then the default name is the name of the file/directory.
			The name of the asset may be explicity set using the -name argument.
		-namespace <namespace>
			[optional] The asset namespace to import into. Defaults to the root.
		-onerror [abort|continue]
			[optional] If there is an importation error, what should happen? Defaults to 'abort'.
		-onlocalerror [abort|continue]
			[optional] If there is an error accessing or opening a local file (e.g. permissions, etc), what should happen? Defaults to 'abort'.
		-task-name <task name>
			[optional] Specifies the custom name for the task that monitors the progress of the import. User may track the progress of the task by using server.task.named.describe :name <task name>.
		-task-remove-after <hours>
			[optional] Used to specify how many hours after the import is complete do we want the monitoring task to be removed from the system. Defaults to '0' hours, i.e. now.
		-task-batch-size <batch size>
			[optional] When used task that monitors the progress of the import will update the progress after 'task-batch-size' of work units were completed. Defaults to '100' work units.
		-task-count-files <true|false>
			[optional] Specifies if the files (and file sizes) should be counted before the import begins. This is used by task that tracks the progress of the import so that it can know total number of work units (file transfers and/or bytes transferred). Defaults to 'false'.
		-task-report-bytes <true|false>
			[optional] Specifies if the task should include bytes transferred as well when updating progress, not just files transferred. If set to true, bytes transferred will be reported once every second. Defaults to 'false'.
		-qtime <secs>
			[optional] Specifies the minimum time (in seconds) a file (or directory) must be quiescent before it will be imported. Defaults to 0.
		-variable <name>=<value>
			[optional] If set adds a consumer service variable to be passed through to consumers.
		-verbose [true|false]
			[optional] If set to true, will display those files being consumed. Defaults to false.
		-parent <parent id>
			[optional] Specifies the asset id of the parent collection asset. If specified all assets created by the import will be added to the collection with the specified asset id
		-create-parent [true|false]
			[optional] If set to true, new collection asset will be created in the specified namespace. This collection will be used as a parent to all imported assets. This option is ignored if -parent is used.
		-parent-name <name>
			[optional] The name of the parent collection asset may be explicity set using the -parent-name argument. Only used when -create-parent set to true
		-parent-namespaces <namespace>
			[optional] The namespace where parent asset should be created. Defaults to the value of -namespace if used, otherwise root. Only used when -create-parent set to true.
		-import-empty-folders [true|false]
			[optional] Specifies whether or not should empty folders be imported. If set to true, namespaces will be created for empty folders. Defaults to false.
		<file>
			File or directory to import.
		<meta>
			[optional] Common metadata for all of the created assets.
```

## 3. Examples

### 3.1. Upload a local directory to Mediaflux:
* On Mac OS, Linux or Unix:
  * **./aterm-import -namespace /projects/my-project /Users/wilson/dir-to-upload**
* On Windows:
  * **aterm-import.cmd -namespace /projects/my-project c:\users\wilson\dir-to-upload**

### 3.2. Upload data with multiple threads. 
* The commands below use 4 threads to download data. The number of threads should normally be less than the number of your CPU cores.
  * On Mac OS, Linux or Unix:
    * **./aterm-import -ncsr 4 -namespace /projects/my-project /Users/wilson/dir-to-upload**
  * On Windows:
    * **aterm-import.cmd -ncsr 4 -namespace /projects/my-project c:\users\wilson\dir-to-upload**

