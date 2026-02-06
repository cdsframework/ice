# ICE - Immunization Calculation Engine

ICE is open source software and in active development. This distribution comes bundled with OpenCDS 7.0.1 libraries. The ICE software will continually update to the latest versions of OpenCDS as they become available.

## Resources

- **Project Information**: https://cdsframework.atlassian.net/wiki/spaces/ICE/overview
- **Latest News**: https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/23920670/News
- **Capabilities Overview**: https://www.hln.com/ice/
- **Documentation** (default immunization rules, technical docs): http://www.cdsframework.org/
- **Commercial Support** (HLN Consulting): https://www.hln.com/ice/
- **Questions**: ice@hln.com

### Mailing List

If you are interested in receiving periodic e-mails pertinent to ICE, you may [subscribe to join the ICE Announcements E-mail List](https://docs.google.com/forms/d/e/1FAIpQLSeeeOLj7arRnJUl4vBtJZsCDKOcP-76vlL0PP-KduGglxJdWQ/viewform). We send e-mail to this list when there is important information to share about new releases or relevant to the operation of ICE. This is a low-volume list and we do not share group members' e-mail addresses with anyone.

## Requirements

This version of ICE **requires Java 25**. Prior versions of Java will not work.

## Installation

To install the software, please follow the directions at [Installing ICE](https://cdsframework.atlassian.net/wiki/spaces/ICE/pages/18972687/Installing+ICE) to configure and interact with the service. The out-of-the-box working ICE service executable is available at this location.

ICE can be run either as a **standalone Spring Boot service** or **deployed to a Tomcat servlet container**.

## Building from Source

1. Edit `src/main/resources/application.yml` to suit your installation, taking care to only modify entries you're familiar with. If you are unsure, the configuration will work without any changes.

2. Using Java 25 and the latest release of Maven 3.9, compile the software:
   ```bash
   mvn clean install
   ```

## Running ICE

### Option A: Standalone Spring Boot Service

Run the application directly using Maven or the generated WAR file. It is recommended to allocate at least 2GB of memory.

**Using Maven:**
```bash
MAVEN_OPTS="-Xmx2g" mvn spring-boot:run
```

**Using the WAR file directly:**
```bash
java -Xmx2g -jar target/opencds-decision-support-service.war
```

The service will start on port 8080 by default. To change the port, add to `application.yml`:
```yaml
server:
  port: 9080
```

Logs are directed to standard output by default. You can configure logging in `application.yml`.

### Option B: Deploying to Tomcat

If running under Tomcat, it is recommended that at least 2GB of memory be allocated to a Tomcat 11 or Tomcat 10 instance.

To install into Tomcat:
1. Shut down Tomcat, if running
2. Copy `target/opencds-decision-support-service.war` to the Tomcat webapps directory
3. Start Tomcat

Logs are directed to `catalina.out`. We suggest configuring Tomcat to rotate and/or expunge logs at periodic intervals.

## License

Copyright (C) 2026 New York City Department of Health and Mental Hygiene, Bureau of Immunization
Contributions by HLN Consulting, LLC

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more details.

The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction, limitation, and warranty) complete irrevocable access and rights to this project.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For more information about this software, see http://www.hln.com/ice or send correspondence to ice@hln.com.