@echo off

SET JAVACC_DIR=C:\javacc-3.0


cd rule

call %JAVACC_DIR%\bin\javacc -STATIC:false -LOOKAHEAD:1 -debug_parser jtb.out.jj

cd ..
