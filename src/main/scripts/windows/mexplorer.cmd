@echo off

pushd %~dp0..\..\lib
set LIB=%cd%
for %%# in ("mexplorer-*.jar") do (
   set JAR=%%~#
)
popd
java -jar %LIB%\%JAR% %*
