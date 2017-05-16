package com.emc.eNas.cloudfoundry.broker.controller;

import com.emc.eNas.cloudfoundry.broker.config.eNasBrokerConfiguration;
import com.emc.eNas.cloudfoundry.broker.model.Credentials;
import com.emc.eNas.cloudfoundry.broker.model.Service;
import com.emc.eNas.cloudfoundry.broker.model.ServiceBinding;
import com.emc.eNas.cloudfoundry.broker.model.ServiceInstance;
import com.emc.eNas.cloudfoundry.broker.repository.ServiceBindingRepository;
import com.emc.eNas.cloudfoundry.broker.repository.ServiceInstanceRepository;
import com.emc.eNas.cloudfoundry.broker.repository.ServiceRepository;
import com.emc.eNas.cloudfoundry.broker.service.eNasService;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ServiceBrokerController {

    Log log = LogFactory.getLog(ServiceBrokerController.class);

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    ServiceBindingRepository serviceBindingRepository;

    
    @Autowired
    eNasService eNsService;
    
	@Autowired
	private eNasBrokerConfiguration eNasBroker;

   @Autowired
   Cloud cloud;

    @RequestMapping("/v2/catalog")
    public Map<String, Iterable<Service>> catalog() {
        Map<String, Iterable<Service>> wrapper = new HashMap<>();
       
        System.out.println("Credentials:" + eNasBroker.getFileSystemId() + ";" + eNasBroker.getManagementprovider());
        wrapper.put("services", serviceRepository.findAll());
        return wrapper;
    }

    @RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> create(@PathVariable("id") String id, @RequestBody ServiceInstance serviceInstance) throws Exception {
        serviceInstance.setId(id);

        boolean exists = serviceInstanceRepository.exists(id);

        if (exists) {
            ServiceInstance existing = serviceInstanceRepository.findOne(id);
            if (existing.equals(serviceInstance)) {
                return new ResponseEntity<>("{}", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("{}", HttpStatus.CONFLICT);
            }
        } else {
            serviceInstanceRepository.save(serviceInstance);
            eNsService.createFileSystemService(serviceInstance.getId(), serviceInstance.getPlanId());
            return new ResponseEntity<>("{}", HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> createBinding(@PathVariable("instanceId") String instanceId,
                                                @PathVariable("id") String id,
                                                @RequestBody ServiceBinding serviceBinding) throws Exception {
        //if (!serviceInstanceRepository.exists(instanceId)) {
          //  return new ResponseEntity<Object>("{\"description\":\"Service instance " + instanceId + " does not exist!\"", HttpStatus.BAD_REQUEST);
       // }

        serviceBinding.setId(id);
        serviceBinding.setInstanceId(instanceId);

     
            Credentials credentials = new Credentials();
            credentials.setId(UUID.randomUUID().toString());
            credentials.setUri("http://" + myUri() + "/FileProvisioningAsService/" + instanceId);
            credentials.setUsername("user");
            credentials.setPassword("password");
            serviceBinding.setCredentials(credentials);
            serviceBindingRepository.save(serviceBinding);
            eNsService.createFileShareService(instanceId, id);
            return new ResponseEntity<Object>(wrapCredentials(credentials), HttpStatus.CREATED);
        
    }

    @RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBinding(@PathVariable("instanceId") String instanceId,
                                                @PathVariable("id") String id,
                                                @RequestParam("service_id") String serviceId,
                                                @RequestParam("plan_id") String planId) throws Exception {
        boolean exists = serviceBindingRepository.exists(id);

        if (exists) {
            serviceBindingRepository.delete(id);
          //  eNsService.deleteFileShare();
            return new ResponseEntity<>("{}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.GONE);
        }
    }

    @RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable("id") String id,
                                         @RequestParam("service_id") String serviceId,
                                         @RequestParam("plan_id") String planId) throws Exception {
        boolean exists = serviceRepository.exists(id);

        if (exists) {
            serviceRepository.delete(id);
         //   eNsService.deleteFileSystem(id);
            return new ResponseEntity<>("{}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.GONE);
        }
    }

    
    private String myUri() {
        ApplicationInstanceInfo applicationInstanceInfo = cloud.getApplicationInstanceInfo();
        List<Object> uris = (List<Object>) applicationInstanceInfo.getProperties().get("uris");
        return uris.get(0).toString();
    }
    

    private Map<String, Object> wrapCredentials(Credentials credentials) {
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("credentials", credentials);
        return wrapper;
    }
}
