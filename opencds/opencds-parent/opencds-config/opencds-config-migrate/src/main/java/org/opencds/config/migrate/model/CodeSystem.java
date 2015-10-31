package org.opencds.config.migrate.model;

public class CodeSystem {
	private final String oid;
	private final String displayName;
	private final String namespace;
	private final boolean isApelonOntylog;

	public CodeSystem(String oid, String displayName, String namespace, boolean isApelonOntylog) {
		this.oid = oid;
		this.displayName = displayName;
		this.namespace = namespace;
		this.isApelonOntylog = isApelonOntylog;
	}
	
	public String getOid() {
		return oid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getNamespace() {
		return namespace;
	}

	public boolean isApelonOntylog() {
		return isApelonOntylog;
	}
}
