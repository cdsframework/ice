package org.opencds.terminology.apelon;

import java.io.InputStream;
import java.util.Map;

import org.springframework.http.converter.HttpMessageConversionException;

public interface RequestParser {

    Map<String, String> getParameters(InputStream inputStream) throws HttpMessageConversionException;

}
