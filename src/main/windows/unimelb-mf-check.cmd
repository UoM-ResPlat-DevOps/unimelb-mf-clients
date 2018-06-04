@echo off

cmd /k java -cp "%~dp0\unimelb-mf-clients.jar" unimelb.mf.client.sync.cli.MFCheck %*
