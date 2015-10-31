package org.opencds.config.migrate.utilities;

import java.io.InputStream;
import java.util.List;

public interface ResourceUtility {

	String getContents(String source);

	List<String> listMatchingResources(final String path,
			final String startsWith, final String endsWith);

	InputStream getResourceAsInputStream(String string);

    String renormalizeSeparator(String location);

}
