package com.emc.eNas.cloudfoundry.broker.service;

import javax.cim.CIMArgument;
import javax.cim.CIMDataType;
import javax.cim.CIMObjectPath;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.emc.eNas.cloudfoundry.broker.config.CatalogConfiguration;
import com.emc.eNas.cloudfoundry.broker.config.eNasBrokerConfiguration;
import com.emc.eNas.cloudfoundry.broker.config.eNasClientException;
import com.emc.eNas.cloudfoundry.broker.model.ServiceOffer;

public class eNasService {

	@Autowired
	private CatalogConfiguration catalog;

	@Autowired
	private eNasBrokerConfiguration eNasBroker;

	public ServiceOffer lookupServiceDefinition(String serviceDefinitionId) throws eNasClientException {
		ServiceOffer service = catalog.findServiceDefinition(serviceDefinitionId);
		if (service == null)
			throw new eNasClientException("ServiceDefinition not found :" + serviceDefinitionId);
		return service;
	}

	private WBEMClient instantiateWBEMClient() {
		WBEMClient clientObj = null;
		/*
		 * The host to connect to. In the form of a WBEM URL. Make sure the WBEM
		 * Server is running before trying this example.
		 */

		try {
			/*
			 * Create an object path using the host variable.
			 */
			CIMObjectPath cns = new CIMObjectPath(eNasBroker.getManagementprovider());
			/*
			 * Create the principal - used for authentication/authorization
			 */
			UserPrincipal up = new UserPrincipal(eNasBroker.getUsername());
			/*
			 * Create the credential - used for authentication/authorization
			 */
			PasswordCredential pc = new PasswordCredential(eNasBroker.getPassword());
			/*
			 * Add the principal and credential to the subject.
			 */
			Subject s = new Subject();
			s.getPrincipals().add(up);
			s.getPrivateCredentials().add(pc);
			/*
			 * Create a CIM client connection using the either CIM-XML or
			 * WS-Management protocol
			 */
			clientObj = WBEMClientFactory.getClient("CIM-XML");
			clientObj.initialize(cns, s, null);
		} catch (WBEMException e) {

		}
		return clientObj;

	}

	private void closeConnection(WBEMClient client) {
		client.close();
	}

	public void createFileShare(String serviceInstanceId, String planId) {
		CloseableIterator<CIMObjectPath> fileSystemPaths = null;
		CloseableIterator<CIMObjectPath> fileExportService = null;
		WBEMClient client = null;
		try

		{

			client = instantiateWBEMClient();
			CIMObjectPath fileSystemPath = null;
			fileSystemPaths = client.enumerateInstanceNames(new CIMObjectPath(eNasBroker.getFileSystemId()));
			while (fileSystemPaths.hasNext()) {
				fileSystemPath = fileSystemPaths.next();
				if (fileSystemPath.toString().toLowerCase().contains(eNasBroker.getFileSystemId())) {
					break;
				}
			}
			CIMObjectPath fileExportServicePath = null;
			fileExportService = client
					.enumerateInstanceNames(new CIMObjectPath("root/emc/celerra:Celerra_FileExportService"));
			if (fileExportService.hasNext()) {
				fileExportServicePath = fileExportService.next();
			}

			CIMArgument[] argArray = new CIMArgument[] { string("SharedElementPath", "/"+serviceInstanceId),
					reference("Root", fileSystemPath) };

			CIMArgument[] outArgs = new CIMArgument[5];

			// Create FileShare;
			client.invokeMethod(fileExportServicePath, "CreateExportedShare", argArray, outArgs);

			// FileShare instance will be available in the outArgs.

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != fileSystemPaths)
				fileSystemPaths.close();
			if (null != fileExportService)
				fileExportService.close();
			if (null != client)
				closeConnection(client);

		}
	}

	private CIMArgument<CIMObjectPath> reference(String name, CIMObjectPath path) {
		return build(name, path, CIMDataType.getDataType(path));
	}

	public CIMArgument<String> string(String name, String value) {
		return build(name, value, CIMDataType.STRING_T);
	}

	private <T> CIMArgument<T> build(String name, T value, CIMDataType dataType) {
		CIMArgument<T> arg;
		try {
			arg = new CIMArgument<>(name, dataType, value);
		} catch (Exception e) {
			throw new IllegalStateException("Problem getting input arguments: ", e);
		}
		return arg;
	}

}
