/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

An overview of the capabilities of ICE is available http://www.hln.com/ice/. This distribution comes bundled with OpenCDS 7.0.1 libraries. The ICE software will continually update to the latest versions of OpenCDS as they become available. The out of the box (default) immunization rules documentation, as well as technical documentation for ICE and CAT, can be found at http://www.cdsframework.org/.

This version of ICE /requires/ Java 25. Versions of Java will not work.

If running under Tomcat, it is recommended that at least 2GB of memory be allocated to a Tomcat 10 or Tomcat 11 instance. 

To install the software, please follow the directions at https://cdsframework.atlassian.net/wiki/display/ICE/Installing+ICE to configure and interact with the service. The out-of-the-box working ICE service executable is available at this location.

Alternatively, to build the software via source:

1) Edit src/main/resources/application.yml to suit your installation, taking care to only modify entries you're familiar with. If you are unsure, the configuration will work without any changes.

2) Using Java 25 and the latest release of Maven 3.9, compile the software:
    $ mvn clean
    $ mvn install

3) The service can either be run as a Spring Boot or Tomcat application.
           + To run as a Spring Boot application:
               $ java -jar target/opencds-decision-support-service.war org.cdsframework.ice.IceApplication
       or
           + To run the service under Tomcat 11 or Tomcat 10: 
               Copy the file target/opencds-decision-support-service.tar to the Tomcat webapps directory



