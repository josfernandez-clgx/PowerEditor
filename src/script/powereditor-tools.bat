@echo off

REM ---------------------------------------
REM Script to run PowerEditor Tools
REM ---------------------------------------

REM Make sure this directory contains jar files
SET LIBDIR=.

java -classpath %LIBDIR%\powereditor-tools.jar;%LIBDIR%\log4j-1.2.8.jar -Xms4m -Xmx128m com.mindbox.pe.tools.PowerEditorTool
