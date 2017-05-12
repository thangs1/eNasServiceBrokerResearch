package com.emc.eNas.cloudfoundry.broker.model;

import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServicePlan {
	private String id;
	private String name;
	private String description;

	private String type;

	public ServicePlan() {
		super();
	}

	public ServicePlan(String id, String name, String description, String type) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public Plan unproxy(String id, String name, String description, String type) {
		return new Plan(id, name, description);

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}