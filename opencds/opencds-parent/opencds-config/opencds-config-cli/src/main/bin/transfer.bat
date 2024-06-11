REM
REM Copyright 2013-2020 OpenCDS.org
REM
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM
REM      http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
@ECHO OFF
REM ##### Get Arguments and Options #####
REM REM Note that DOS Allows ONLY up to 9 Arguments

set cpath=.;lib\*

java -cp %cpath% -Dlog4j.configuration=resources/log4j.properties org.opencds.config.cli.RestCli --transfer %*
