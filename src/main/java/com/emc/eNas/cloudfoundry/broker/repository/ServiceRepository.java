package com.emc.eNas.cloudfoundry.broker.repository;

import org.springframework.data.repository.CrudRepository;

import com.emc.eNas.cloudfoundry.broker.model.Service;

public interface ServiceRepository extends CrudRepository<Service, String> {
}
