package com.emc.eNas.cloudfoundry.broker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.emc.eNas.cloudfoundry.broker.model.ServiceOffer;

import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "catalog")
@Configuration
public class CatalogConfiguration {

	private List<ServiceOffer> services;

	public CatalogConfiguration() {
		super();
	}

	public CatalogConfiguration(List<ServiceOffer> services) {
		super();
		this.services = services;
	}

	@Bean
	public Catalog catalog() {
		return new Catalog(services.stream().map(s -> s.unproxy()).collect(Collectors.toList()));
	}

	public List<ServiceOffer> getServices() {
		return services;
	}

	public void setServices(List<ServiceOffer> services) {
		this.services = services;
	}

	public ServiceOffer findServiceDefinition(String serviceId) {
		return services.stream().filter(s -> s.getId().equals(serviceId)).findFirst().get();
	}
}