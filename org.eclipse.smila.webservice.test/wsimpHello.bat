@echo off

:# SET JAVA_HOME=D:\Works\Java\jdk1.6.0_14

rem validate environment

if ""%JAVA_HOME%""=="""" goto no_java
if not exist %JAVA_HOME%\bin\wsgen.exe goto no_java2

rem run WsGen tool
%JAVA_HOME%\bin\wsimport -verbose -s code\gen -d code\bin HelloWorldService.wsdl -p org.eclipse.smila.webservice.helloworld.client

goto :exit

:no_java2
echo JAVA_HOME should point to J2SDK 1.6 installation
:error
:exit

pause
