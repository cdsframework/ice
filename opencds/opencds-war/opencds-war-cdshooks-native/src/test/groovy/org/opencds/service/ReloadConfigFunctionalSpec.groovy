package org.opencds.service

import spock.lang.Ignore
import spock.lang.Specification;

class ReloadConfigFunctionalSpec extends Specification {

    private final String URL = "http://localhost:38080/opencds-test/config/v1/reload"

    @Ignore // not using CLASSPATH strategy in this module
    def "reload fails with 403 with priviledged user due to classpath strategy"() {
        given:
        def url = new URL(URL)

        when:
        def connection = url.openConnection()
        connection.setRequestProperty('Authorization', 'Basic ' + 'username:password'.bytes.encodeBase64() as String)
        def response
        try {
            connection.responseCode
            connection.content.text
        } catch (Exception e) {
            response = e.getMessage()
        }
        println response

        then:"reloading is forbidden for strategy == classpath"
        response.startsWith "Server returned HTTP response code: 403 for URL"
    }

    def "reload passes with priviledged user"() {
        given:
        def url = new URL(URL)

        when:
        def connection = url.openConnection()
        connection.setRequestProperty('Authorization', 'Basic ' + 'username:my1pass1'.bytes.encodeBase64() as String)
        def response
        try {
            connection.responseCode
            response = connection.content.text
        } catch (Exception e) {
            response = e.getMessage()
        }

        then:
        response.startsWith "Configuration Reloaded; Timestamp: "
    }

    def "reload fails with unpriviledged user"() {
        given:
        def url = new URL(URL)

        when:
        def connection = url.openConnection()
        connection.setRequestProperty('Authorization', 'Basic ' + 'username-user:password'.bytes.encodeBase64() as String)
        def response
        try {
            connection.responseCode
            connection.content.text
        } catch (Exception e) {
            response = e.getMessage()
        }

        then:"reloading is forbidden"
        response.startsWith "Server returned HTTP response code: 401 for URL"
    }

    def "reload fails with unknown user"() {
        given:
        def url = new URL(URL)

        when:
        def connection = url.openConnection()
        connection.setRequestProperty('Authorization', 'Basic ' + 'username-joe:password'.bytes.encodeBase64() as String)
        def response
        try {
            connection.responseCode
            connection.content.text
        } catch (Exception e) {
            response = e.getMessage()
        }

        then:"reloading is auth denied"
        response.startsWith "Server returned HTTP response code: 401 for URL"
    }

    def "reload fails with no user"() {
        given:
        def url = new URL(URL)

        when:
        def connection = url.openConnection()
        def response
        try {
            connection.responseCode
            connection.content.text
        } catch (Exception e) {
            response = e.getMessage()
        }

        then:"reloading is auth denied"
        response.startsWith "Server returned HTTP response code: 401 for URL"
    }

}
