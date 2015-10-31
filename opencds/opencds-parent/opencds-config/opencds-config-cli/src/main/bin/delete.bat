@ECHO OFF
REM ##### Get Arguments and Options #####
REM REM Note that DOS Allows ONLY up to 9 Arguments

set cpath=.;lib\*

java -cp %cpath% -Dlog4j.configuration=resources/log4j.properties org.opencds.config.cli.RestCli --delete %*
