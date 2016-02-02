@echo off

set target=test-install-project-wars
IF NOT "%1"=="" set target=%1

IF NOT EXIST local-build.properties (
  "%ANT_HOME%\bin\ant" -buildfile build.xml -propertyfile local-build-template.properties -logfile "%target%.log" "%target%"
) ELSE (
  "%ANT_HOME%\bin\ant" -buildfile build.xml -propertyfile local-build.properties -logfile "%target%.log" "%target%"
)
