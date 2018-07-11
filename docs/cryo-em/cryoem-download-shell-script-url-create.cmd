@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM check java
where java >nul 2>nul
IF %errorlevel% neq 0 (
    echo Error: no java is found. Install java and retry.
    exit /b 1
)

REM script file name
set PROG=%~0

REM aterm.jar download url
set MFLUX_ATERM_URL=https://mediaflux.vicnode.org.au/mflux/aterm.jar

REM aterm.jar location
set "MFLUX_ATERM=%~dp0aterm.jar"

REM mflux.cfg location
set "MFLUX_CFG=%~dp0mflux.cfg"
if not exist "%MFLUX_CFG%" (
    echo Error: could not find %MFLUX_CFG%
    exit /b 1
)

REM aterm command prefix
set "ATERM=java -jar -Dmf.cfg=%MFLUX_CFG% %MFLUX_ATERM% nogui"

REM service name
set "SERVICE=unimelb.asset.download.shell.script.url.create"

REM command (prefix)
set "COMMAND=%ATERM% %SERVICE%"

REM default argument values
set EXPIRE_DAYS=14
set OVERWRITE=false
set VERBOSE=true

REM cryo-em project root namespace 
set NAMESPACE_ROOT=/projects/cryo-em

REM 
set PROJECT=
set ROLE=
set NAMESPACES=
set EMAILS=

REM parse arguments
:loop
if not [%~1]==[] (
    if "%~1"=="--expire-days" (
        set EXPIRE_DAYS=%~2
        shift
        shift
        goto :loop
    )
    if "%~1"=="--overwrite" (
        set OVERWRITE=true
        shift
        goto :loop
    )
    if "%~1"=="--quiet" (
        set VERBOSE=false
        shift
        goto :loop
    )
    if "%~1"=="--email" (
        set value=%~2
        set value=!value:,= !
        for %%e in (!value!) do (
            if "!EMAILS!"=="" ( set "EMAILS=:to %%e" ) else ( set "EMAILS=!EMAILS! :to %%e" )
        )
        shift
        shift
        goto :loop
    )
    if "%~1"=="-h" (
        call :usage
        exit /b 0
    )
    if "%~1"=="--help" (
        call :usage %PROG%
        exit /b 0
    )
    set ns=%~1
    if "!ns:~0,18!"=="!NAMESPACE_ROOT!/" (
        for /f "delims=/" %%c in ("!ns:~18!") do (
            set "prj=%%c"
        )
    ) else (
        for /f "delims=/" %%c in ("!ns!") do (
            set "prj=%%c"
        )
        set "ns=!NAMESPACE_ROOT!/!ns!"
    )
    if [!PROJECT!]==[] (
        set PROJECT=!prj!
        set "ROLE=!prj!:participant-a"
    ) else (
        if not "!PROJECT!"=="!prj!" (
            echo Error: cannot share namespaces from multiple projects.
            exit /b 1
        )
    )
    if [!NAMESPACES!]==[] (
        set "NAMESPACES=:namespace !ns!"
    ) else (
        set "NAMESPACES=!NAMESPACES! :namespace !ns!"
    )
    shift
    goto :loop
)

REM check if namespace is specified
if "%NAMESPACES%"=="" (
    echo Error: no namespace is specified.
    call :usage
    exit /b 1
)

REM download aterm.jar if not exist
if not exist "%MFLUX_ATERM%" (
    call :download %MFLUX_ATERM_URL% "%MFLUX_ATERM%"
    if %errorlevel% neq 0 ( exit /b 1 )
)

REM compose the command 
set "COMMAND=%COMMAND% :download ^< %NAMESPACES% :token ^< :role -type role %ROLE% :to now+%EXPIRE_DAYS%day ^> :verbose %VERBOSE% :overwrite %OVERWRITE% ^> :token ^< :perm ^< :resource -type role:namespace %PROJECT%: :access ADMINISTER ^> ^>"

if not "%EMAILS%"=="" (
    set "COMMAND=%COMMAND% :email ^< %EMAILS% ^>"
)

REM execute aterm command to generate the script url
%COMMAND%

if %errorlevel% neq 0 (
    exit /b 2
)
exit /b 0

REM function to print usage
:usage
echo=
echo Usage:
echo     %PROG% [-h^|--help] [--expire-days ^<number-of-days^>] [--ncsr ^<ncsr^>] [--overwrite] [--quiet] [--email ^<addresses^>] ^<namespace^>
echo=
echo Options:
echo     -h ^| --help                       prints usage
echo     --email ^<addresses^>               specify the email recipient of the generated url. Can be comma-separated if there are more than one. NOTE: your need quote the comma separated values like --email "user1@domain1.org,user2@domain2.org"
echo     --expire-days ^<number-of-days^>    expiry of the auth token. Defaults to ${EXPIRE_DAYS} days.
echo     --overwrite                       overwrite if output file exists.
echo     --quiet                           do not print output message.
echo=
echo Positional arguments:
echo     ^<namespace^>                       Mediaflux asset namespace to be downloaded by the scripts. Can be multiple, but must be from the same project.
echo=
echo Examples:
echo     %PROG% --email user1@unimelb.edu.au --expire-days 10 proj-abc-1128.4.999/RAW_DATA proj-abc-1128.4.999/PROCESSED_DATA
echo=
exit /b 0

REM function to download from url
:download
setlocal
set url=%~1
set out=%~2
if not exist %out% (
    where powershell >nul 2>nul
    if %errorlevel% equ 0 (
        echo downloading %out%
        powershell -command "(New-Object Net.WebClient).DownloadFile('%url%', '%out%')" >nul 2>nul
        if %errorlevel% neq 0 (
            echo Error: failed to download %url%
            exit /b 1
        )
        if not exist "%out%" (
            echo Error: failed to download %url% to %out%
            exit /b 1
        )
    ) else (
        echo Error: cannot download %out%. No powershell found.
        exit /b 1
    )
)
