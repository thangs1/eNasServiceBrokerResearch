package com.emc.eNas.cloudfoundry.broker.repository;

import org.springframework.data.repository.CrudRepository;

import com.emc.eNas.cloudfoundry.broker.model.ServiceBinding;

public interface ServiceBindingRepository extends CrudRepository<ServiceBinding,String> {
}
