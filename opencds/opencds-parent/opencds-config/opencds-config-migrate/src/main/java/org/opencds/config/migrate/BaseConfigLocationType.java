package org.opencds.config.migrate;

public enum BaseConfigLocationType {
	SIMPLE_FILE("file", true),
	CLASSPATH("classpath", false);
	
	private final String scheme;
	private final boolean reloadable;
	
	private BaseConfigLocationType(String scheme, boolean reloadable) {
		this.scheme = scheme;
		this.reloadable = reloadable;
	}
	
	public String getScheme() {
		return scheme;
	}
	
	public boolean isReloadable() {
	    return reloadable;
	}
	
	public static BaseConfigLocationType resolveType(String type) {
		BaseConfigLocationType bclType = null;
		for (BaseConfigLocationType t : values()) {
			if (type.equals(t.toString())) {
				bclType = t;
			}
		}
		return bclType;
	}
	
}