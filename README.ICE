/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */
 

ICE is open source software and in active development. Please check in to http://www.cdsframework.org/ for the latest status, or e-mail questions to ice@hln.com. 

An overview of the capabilities of ICE is available http://www.hln.com/ice/. This distribution comes bundled with OpenCDS 2.0.5, which was released on August 2015. The ICE software will continually update to the latest versions of OpenCDS as they become available. The out of the box (default) immunization rules documentation, as well as technical documentation for ICE and CAT, can be found at http://www.cdsframework.org/.

This version of ICE requires Java 7, and has been tested under the latest version of Tomcat 7 and Tomcat 8. OpenCDS is known to run under Tomcat 9 but has not yet been tested with ICE. 

It is recommended that at least 1GB of memory be allocated to the Tomcat instance, though in our internal performance tests, we have noted a significant performance increase when allocating 2GB of memory.

To install the software, please follow the directions at https://cdsframework.atlassian.net/wiki/display/ICE/Installing+ICE to configure and interact with the service. The out-of-the-box working ICE service executable is available at this location.

Alternatively, to build the software via source:
1) Edit opencds/opencds-parent/opencds.properties to suit your installation.
    + Set km.threads to specify the number of threads allocated to each knowledgeModule. Recommended setting: twice the number of CPU cores available to this instance for Linux; equal to the number of CPU cores for Windows
    + Set knowledge-repository.path to the location of the ICE knowledge repository as follows: /path/to/opencds-ice-service-data/src/main/resources
    + Set the log4j.logfile property

2) Copy opencds.properties to the $HOME/.opencds/, where $HOME is the "home" directory of the application server instance.

3) Edit opencds/opencds-parent/opencds-decision-support-service/src/main/resources/ice.properites: 
    + Set ice_knowledge_repository_location to the same location as knowledge-repository.path of opencds.properties file

4) Edit opencds/opencds-parent/opencds-decision-support-service/src/main/resources/log4j.properties to suit your installation

5) Compile the software:
    $ cd opencds/opencds-parent
    $ mvn clean
    $ mvn -P drools-5.5 -Dmaven.test.skip=true install

