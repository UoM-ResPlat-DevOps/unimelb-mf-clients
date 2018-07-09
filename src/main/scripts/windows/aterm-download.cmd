@echo off

REM where to download aterm.jar
SET ATERM_URL=https://mediaflux.vicnode.org.au/mflux/aterm.jar

REM where to save aterm.jar
SET ATERM_HOME=%USERPROFILE%\.Arcitecta
IF NOT EXIST "%ATERM_HOME%\NUL" MKDIR "%ATERM_HOME%" >NUL 2>NUL

SET MFLUX_ATERM=%ATERM_HOME%\aterm.jar
SET MFLUX_CFG=%ATERM_HOME%\mflux.cfg

REM check if mflux.cfg exists
IF NOT EXIST %MFLUX_CFG% (
	ECHO File: %MFLUX_CFG% does not exist && EXIT /B 1
)

REM check if java exists
WHERE java >NUL 2>NUL
IF %ERRORLEVEL% NEQ 0 (
    ECHO cannot find java. && EXIT /B 1
)

REM try downloading aterm.jar with PowerShell
IF NOT EXIST %MFLUX_ATERM% (
    WHERE POWERSHELL >NUL 2>NUL
    IF %ERRORLEVEL% EQU 0 (
        POWERSHELL -COMMAND "(New-Object Net.WebClient).DownloadFile('%ATERM_URL%', '%MFLUX_ATERM%')" >NUL 2>NUL
    )
)

REM try downloading aterm.jar with bitsadmin
IF NOT EXIST %MFLUX_ATERM% (
    WHERE BITSADMIN >NUL 2>NUL
    IF %ERRORLEVEL% EQU 0 (
    	BITSADMIN /TRANSFER "Download aterm.jar" %ATERM_URL% %MFLUX_ATERM% >NUL 2>NUL
    )
)

REM failed to download aterm.jar, exit
IF NOT EXIST %MFLUX_ATERM% (
    ECHO failed to download ATERM: %MFLUX_ATERM% . && EXIT /B 1
)

REM show usage if no argument or the 1st argument is -h, --help or /h
SET HELP=F
IF [%1]==[]       SET HELP=T
IF "%1"=="/h"     SET HELP=T
IF "%1"=="-h"     SET HELP=T
IF "%1"=="--help" SET HELP=T
IF %HELP%==T (
    java -jar -Dmf.cfg=%MFLUX_CFG% %MFLUX_ATERM% nogui help download && EXIT /B 0
)


REM execute download command via aterm
java -jar -Dmf.cfg=%MFLUX_CFG% %MFLUX_ATERM% nogui download %*
