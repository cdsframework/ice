package org.opencds.service;

import spock.lang.Specification;

public class ReloadConfigFunctionalSpec extends Specification {

    private final String URL = "http://localhost:38080/opencds-decision-support-service/config/v1/reload"

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

        then:"reloading is forbidden for strategy == classpath"
        response.startsWith "Server returned HTTP response code: 403 for URL"
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
        response.startsWith "Server returned HTTP response code: 403 for URL"
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