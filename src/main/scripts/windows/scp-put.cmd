@echo off

cmd /k java -cp "%~dp0..\lib\unimelb-mf-clients.jar" unimelb.mf.client.ssh.cli.SCPPutCLI %*
