package com.emc.eNas.cloudfoundry.broker.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;

import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;

import com.emc.eNas.cloudfoundry.broker.config.eNasBrokerConfigConstants;
import com.emc.eNas.cloudfoundry.broker.config.eNasClientException;
import com.emc.eNas.cloudfoundry.broker.model.ServicePlan;

import com.emc.eNas.cloudfoundry.broker.model.ServiceOffer;

public class eNasServiceInstanceService implements ServiceInstanceService {

	@Autowired
	eNasService eNasservice;

	@Override
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest serviceInstanceRequest)
			throws ServiceBrokerException, ServiceInstanceExistsException {
		// Name of the file share to get created.
		String serviceInstanceId = serviceInstanceRequest.getServiceInstanceId();
		// Create File Share service
		String serviceDefinitionId = serviceInstanceRequest.getServiceDefinitionId();
		String planId = serviceInstanceRequest.getPlanId();
		try {
			ServiceOffer service = eNasservice.lookupServiceDefinition(serviceDefinitionId);
			String serviceType = (String) service.getType();

			if (eNasBrokerConfigConstants.FILE_SHARE.equals(serviceType)) {
				eNasservice.createFileShare(serviceInstanceId, planId);
			} else {
				throw new eNasClientException("Service Type :" + serviceType + " offering not available");

			}
			return new CreateServiceInstanceResponse();
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}

	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		String serviceDefinitionId = request.getServiceDefinitionId();
		try {
			ServiceOffer service = eNasservice.lookupServiceDefinition(serviceDefinitionId);

			String serviceType = service.getType();
			if (eNasBrokerConfigConstants.FILE_SHARE.equals(serviceType)) {
				// eNasservice.deleteFileShare();
			} else {
				throw new eNasClientException("Service Type :" + serviceType + " offering not available");

			}
			return new DeleteServiceInstanceResponse();
		} catch (Exception e) {
			throw new ServiceBrokerException(e);
		}
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
