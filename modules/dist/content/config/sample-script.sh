#!/bin/sh

#----------------------------------------
# sample-script.sh
#
# A sample post deploy script for UNIX
#----------------------------------------

# Set the path to a post script log file
LOG_FILE=/opt/mindbox/MBXProjects/PowerEditor/log/post-deploy.log

# Get deploy status from the 1st argument
DEPLOY_STATUS=$1

# Get deploy directory from the 2nd argument
DEPLOY_DIRPATH=$2

cd $DEPLOY_DIR
date >> $LOG_FILE

# Write status from environment variable
echo "Deployed KB_STATUS=$KB_STATUS" >> $LOG_FILE

# Write deploy dir from environment variable
echo "Deploy path = $DEPLOY_DIR" >> $LOG_FILE

# Write status from argument
echo "Deployed DEPLOY_STATUS=$DEPLOY_STATUS" >> $LOG_FILE

# Write deploy dir from argument
echo "Copying files from $DEPLOY_DIRPATH..." >> $LOG_FILE

echo "DONE" >> $LOG_FILE
