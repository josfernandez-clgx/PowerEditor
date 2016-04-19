@echo off

REM 
REM Sample post deploy script for Windows
REM

REM Set path to the log file
SET LOG_FILE=c:\mindbox\MBXProjects\PowerEditor\log\post-deploy.log

SET DEPLOY_STATUS=%1
SET DEPLOY_PATH=%2

C:
CD %DEPLOY_DIR%

date /T >> %LOG_FILE% 

REM Write status and deploy dir from environment var.
if  NOT "%KB_STATUS%"=="" (
  echo Deployed KB_STATUS=%KB_STATUS% >> %LOG_FILE%
  echo Deply dir is %DEPLOY_DIR% >> %LOG_FILE%
)

REM Write status and deploy dir from arguments
echo Deploy status is %DEPLOY_STATUS% >> %LOG_FILE%
echo Copying files from %DEPLOY_PATH% >> %LOG_FILE%


echo DONE >> %LOG_FILE%
