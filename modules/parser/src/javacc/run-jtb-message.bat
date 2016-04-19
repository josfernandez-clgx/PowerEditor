@echo off

SET JTB_DIR=c:\jtb122

SET PACKAGE=com.mindbox.pe.server.parser.jtb.message

cd message

call %JTB_DIR%\bin\jtb -p %PACKAGE% -jd -pp -printer ..\Message.jj

cd ..
