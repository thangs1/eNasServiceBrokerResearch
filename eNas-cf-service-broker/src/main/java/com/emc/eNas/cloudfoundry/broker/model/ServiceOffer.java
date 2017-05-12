package com.emc.eNas.cloudfoundry.broker.model;

import org.springframework.cloud.servicebroker.model.DashboardClient;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ServiceOffer {
	private String id;
	private String name;
	private String description;
	private String type;
	private List<ServicePlan> plans;

	public ServiceOffer() {
		super();
	}

	public ServiceOffer(String id, String name, String description, Map<String, Object> serviceSettings,
			List<ServicePlan> plans) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;

		this.plans = plans;

	}

	public ServiceDefinition unproxy() {
		List<Plan> realPlans = null;
		if (plans != null) {
			realPlans = plans.stream().map(p -> p.unproxy(id, name, description, type)).collect(Collectors.toList());
		}

		return new ServiceDefinition(id, name, description, true, realPlans);

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ServicePlan> getPlans() {
		return plans;
	}

	public void setPlans(List<ServicePlan> plans) {
		this.plans = plans;
	}

	public ServicePlan findPlan(String planId) {
		return plans.stream().filter(p -> p.getId().equals(planId)).findFirst().get();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ServiceOffer lookupServiceDefinition(String serviceDefinitionId) {
		// TODO Auto-generated method stub
		return null;
	}
}