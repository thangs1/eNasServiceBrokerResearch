package com.emc.eNas.cloudfoundry.broker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "broker")
@Configuration
public class eNasBrokerConfiguration {

	private String namespace;
	private String managementProvider;
	private String port;
	private Boolean SSLEnabled;
	private String username;
	private String password;
	private String fileSystemId;
	
	public eNasBrokerConfiguration() {
		System.out.println();
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getManagementprovider() {
		return managementProvider;
	}

	public void setManagementprovider(String managementProvider) {
		this.managementProvider = managementProvider;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Boolean getSSLEnabled() {
		return SSLEnabled;
	}

	public void setSSLEnabled(Boolean sSLEnabled) {
		SSLEnabled = sSLEnabled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFileSystemId() {
		return fileSystemId;
	}

	public void setFileSystemId(String fileSystemId) {
		this.fileSystemId = fileSystemId;
	}

}
