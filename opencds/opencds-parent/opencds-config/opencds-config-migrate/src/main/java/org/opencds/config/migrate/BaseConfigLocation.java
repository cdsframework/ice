package org.opencds.config.migrate;

public class BaseConfigLocation {
	private final BaseConfigLocationType type;
	private final String path;
	
	public BaseConfigLocation(BaseConfigLocationType type, String location) {
		this.type = type;
		this.path = location;
	}
	
	public BaseConfigLocationType getType() {
		return type;
	}
	
	public String getPath() {
		return path;
	}
}