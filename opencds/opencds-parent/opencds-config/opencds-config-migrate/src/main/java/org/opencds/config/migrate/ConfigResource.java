package org.opencds.config.migrate;

public class ConfigResource {
	private String location;
	private String name;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "ConfigResource: location= " + location + ", name= " + name;
	}
}