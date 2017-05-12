package com.emc.eNas.cloudfoundry.broker.service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

import com.emc.eNas.cloudfoundry.broker.config.eNasBrokerConfigConstants;
import com.emc.eNas.cloudfoundry.broker.config.eNasClientException;
import com.emc.eNas.cloudfoundry.broker.model.ServiceOffer;
import com.emc.eNas.cloudfoundry.broker.model.ServicePlan;


public class eNasServiceInstanceBindingService implements ServiceInstanceBindingService {

	@Autowired
	eNasService eNasservice;

	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {
		String instanceId = request.getServiceInstanceId();
		String bindingId = request.getBindingId();
		String serviceDefinitionId = request.getServiceDefinitionId();

		Map<String, Object> credentials = new HashMap<>();
		Map<String, Object> parameters = request.getParameters();

		try {

			ServiceOffer service = eNasservice.lookupServiceDefinition(serviceDefinitionId);
			ServicePlan plan = service.findPlan(request.getPlanId());

			String serviceType = service.getType();

			if (eNasBrokerConfigConstants.FILE_SHARE.equals(serviceType)) {
				// eNaseService.createUser(); USe bindingId here as the user
				// name. insytanceID as fileshare.

			} else {
				throw new eNasClientException("Service Type :" + serviceType + " offering not available");
			}

		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
		return new CreateServiceInstanceAppBindingResponse();

	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		String bindingId = request.getBindingId();
		String instanceId = request.getServiceInstanceId();
		String serviceDefinitionId = request.getServiceDefinitionId();
		ServiceOffer service = null;
		try {
			service = eNasservice.lookupServiceDefinition(serviceDefinitionId);
		} catch (eNasClientException e1) {

		}
		String serviceType = service.getType();
		try {
			if (eNasBrokerConfigConstants.FILE_SHARE.equals(serviceType)) {
				// eNasService.remove USer permission and delete user if
				// possible.
			}

		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}

	}

}
