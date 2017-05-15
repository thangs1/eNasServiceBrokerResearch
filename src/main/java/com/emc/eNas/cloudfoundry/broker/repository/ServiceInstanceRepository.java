package com.emc.eNas.cloudfoundry.broker.repository;

import org.springframework.data.repository.CrudRepository;

import com.emc.eNas.cloudfoundry.broker.model.ServiceInstance;

/**
 * Created by pivotal on 6/26/14.
 */
public interface ServiceInstanceRepository extends CrudRepository<ServiceInstance, String> {
}
