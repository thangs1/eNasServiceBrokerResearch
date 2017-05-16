package com.emc.eNas.cloudfoundry.broker.service;

import java.util.Map;

import javax.cim.CIMArgument;
import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger16;
import javax.cim.UnsignedInteger32;
import javax.cim.UnsignedInteger64;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientFactory;

import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.emc.eNas.cloudfoundry.broker.config.eNasBrokerConfiguration;
import com.emc.eNas.cloudfoundry.broker.config.eNasClientException;


@Service
@Component
public class eNasService {

	@Autowired
	private eNasBrokerConfiguration eNasBroker;
	
	

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
			e.printStackTrace();
		}
		return clientObj;

	}

	private void closeConnection(WBEMClient client) {
		client.close();
	}

	private final CIMObjectPath createCIMPath(String cimPath) {
		String[] items = cimPath.split(":");
		CIMObjectPath path = createInstance(items[1], items[0]);
		return path;
	}

	public static CIMObjectPath createInstance(String pObjectName, String pNamespace) {
		return new CIMObjectPath(null, null, null, pNamespace, pObjectName, null);
	}
	
	public void deleteFileSystem(String id) throws Exception {
		CloseableIterator<CIMObjectPath> fileSystemPaths = null;
		CloseableIterator<CIMObjectPath> fileSystemConfigServicePathItr = null;
		
		WBEMClient client = null;
		try {
           
			client = instantiateWBEMClient();
			CIMObjectPath fileSystemConfigServicePath = null;
			CIMObjectPath poolPath = null;
			
			fileSystemConfigServicePathItr = client.enumerateInstanceNames(createCIMPath("root/emc/celerra:Celerra_FileSystemConfigurationService"));
			
			
			while (fileSystemConfigServicePathItr.hasNext()) {
				fileSystemConfigServicePath = fileSystemConfigServicePathItr.next();
				if (null != fileSystemConfigServicePath) {
					System.out.println("File System Service :"+ fileSystemConfigServicePath);
					break;
				}
			}
			
			CIMObjectPath fileSystemPath = null;
			fileSystemPaths = client.enumerateInstanceNames(createCIMPath("root/emc/celerra:Celerra_UxfsLocalFileSystem"));
			while (fileSystemPaths.hasNext()) {
			    CIMObjectPath tempFilePath =  fileSystemPaths.next();
				
				if (tempFilePath.toString().toLowerCase().contains(id.toLowerCase())) {
					fileSystemPath = tempFilePath;
					
				    break;
				}
			}
			System.out.println("File System to delete path found :"+ fileSystemPath);
			CIMArgument[] argArray = new CIMArgument[] { 
					reference("TheElement", fileSystemPath) };
			for(CIMArgument arg : argArray) {
				System.out.println("Argument :"+ arg.getName()+ ":"+ String.valueOf(arg.getValue()));
			}

			CIMArgument[] outArgs = new CIMArgument[5];
			Object obj =  client.invokeMethod(fileSystemConfigServicePath, "SNIA_CreateExportedShare", argArray, outArgs);
			
			String str = protectedToString(obj);
			System.out.println("Status :" + str);
			
			CIMObjectPath cimJobPath =
	                getCimObjectPathFromOutputArgs(outArgs, "Job");
			for(CIMArgument arg : outArgs) {
				if(null != arg) 
				System.out.println("OutArgument :"+ arg.getName()+ ":"+ String.valueOf(arg.getValue()));
			}
			
			if(null != cimJobPath) {
			
			CIMInstance instance = client.getInstance(cimJobPath, false, false, null);
			
			
			 CIMProperty<UnsignedInteger16> percentComplete =
             (CIMProperty<UnsignedInteger16>) instance.getProperty("PercentComplete");
			 
			 while(percentComplete.getValue().intValue() < 100) {
				 instance = client.getInstance(cimJobPath, false, false, null);
					
					System.out.println("Running");
				  percentComplete =
	             (CIMProperty<UnsignedInteger16>) instance.getProperty("PercentComplete");
			 }
			
             System.out.println("Completed");
			}
		}finally {
			if (null != fileSystemPaths)
				fileSystemPaths.close();
			if (null != fileSystemConfigServicePathItr)
				fileSystemConfigServicePathItr.close();
			if (null != client)
				closeConnection(client);
		}
	}
	
	public void deleteFileShare() {
		
	}
	
	
	public void createFileShare(String serviceInstanceId, String planId) throws Exception {
		CloseableIterator<CIMObjectPath> fileSystemPaths = null;
		CloseableIterator<CIMObjectPath> fileExportService = null;
		WBEMClient client = null;
		try

		{

			client = instantiateWBEMClient();
			CIMObjectPath fileSystemPath = null;
			fileSystemPaths = client.enumerateInstanceNames(createCIMPath("root/emc/celerra:Celerra_UxfsLocalFileSystem"));
			System.out.println("Finding cerated file system :" + serviceInstanceId);
			while (fileSystemPaths.hasNext()) {
				CIMObjectPath fileSystemPath1 = fileSystemPaths.next();
				
				if (fileSystemPath1.toString().toLowerCase().contains(serviceInstanceId)) {
					fileSystemPath = fileSystemPath1;
					break;
				}
			}
			System.out.println("File System Path :"+ fileSystemPath);
			CIMObjectPath fileExportServicePath = null;
			fileExportService = client
					.enumerateInstanceNames(new CIMObjectPath("root/emc/celerra:Celerra_FileExportService"));
			if (fileExportService.hasNext()) {
				fileExportServicePath = fileExportService.next();
			}

			System.out.println("File Export Service :"+ fileExportService);
			
			CIMArgument[] argArray = new CIMArgument[] { string("SharedElementPath", "/"+serviceInstanceId),
					reference("Root", fileSystemPath) };
			
			for(CIMArgument arg : argArray) {
				System.out.println("Argument :"+ arg.getName()+ ":"+ String.valueOf(arg.getValue()));
			}

			CIMArgument[] outArgs = new CIMArgument[5];

			// Create FileShare;
			Object obj = client.invokeMethod(fileExportServicePath, "SNIA_CreateExportedShare", argArray, outArgs);
			
			String str = protectedToString(obj);
			System.out.println("Status :" + str);
			
			CIMObjectPath cimJobPath =
	                getCimObjectPathFromOutputArgs(outArgs, "Job");
			for(CIMArgument arg : outArgs) {
				if(null != arg) 
				System.out.println("OutArgument :"+ arg.getName()+ ":"+ String.valueOf(arg.getValue()));
			}
			
			if(null != cimJobPath) {
			
			CIMInstance instance = client.getInstance(cimJobPath, false, false, null);
			
			
			 CIMProperty<UnsignedInteger16> percentComplete =
             (CIMProperty<UnsignedInteger16>) instance.getProperty("PercentComplete");
			 
			 while(percentComplete.getValue().intValue() < 100) {
				 instance = client.getInstance(cimJobPath, false, false, null);
					
					System.out.println("Running");
				  percentComplete =
	             (CIMProperty<UnsignedInteger16>) instance.getProperty("PercentComplete");
			 }
			
             System.out.println("Completed");
			}
		}finally {
			if (null != fileSystemPaths)
				fileSystemPaths.close();
			if (null != fileExportService)
				fileExportService.close();
			if (null != client)
				closeConnection(client);
		}
	}

	public void createFileSystem(String serviceInstanceId, String planId) throws Exception {
		CloseableIterator<CIMObjectPath> fileSystemConfigServicePathItr = null;
		CloseableIterator<CIMObjectPath> storagePoolsItr = null;
		WBEMClient client = null;
		try {
           
			client = instantiateWBEMClient();
			CIMObjectPath fileSystemConfigServicePath = null;
			CIMObjectPath poolPath = null;
			
			fileSystemConfigServicePathItr = client.enumerateInstanceNames(createCIMPath("root/emc/celerra:Celerra_FileSystemConfigurationService"));
			
			
			while (fileSystemConfigServicePathItr.hasNext()) {
				fileSystemConfigServicePath = fileSystemConfigServicePathItr.next();
				if (null != fileSystemConfigServicePath) {
					System.out.println("File System Service :"+ fileSystemConfigServicePath);
					break;
				}
			}
			
			storagePoolsItr = client.enumerateInstanceNames(createCIMPath("root/emc/celerra:Celerra_NonPrimordialStoragePool"));
			
			
			while (storagePoolsItr.hasNext()) {
				poolPath = storagePoolsItr.next();
				if(null != poolPath) {
					System.out.println("Storage Pool :" + poolPath);
					break;
				}
			}
			
			
			
			CIMObjectPath[] paths = new CIMObjectPath[] {poolPath};
			UnsignedInteger64[] sizes = new UnsignedInteger64[] { new UnsignedInteger64("10485760") };

			CIMArgument[] argArray = new CIMArgument[] { string("ElementName", "TestFS"), uint64Array("Sizes", sizes),
					referenceArray("Pools", paths) };
			for(CIMArgument arg : argArray) {
				System.out.println("Argument :"+ arg.getName()+ ":"+ String.valueOf(arg.getValue()));
			}

			CIMArgument[] outArgs = new CIMArgument[5];

			// Create FileShare;
			Object obj = client.invokeMethod(fileSystemConfigServicePath, "SNIA_CreateFileSystem", argArray, outArgs);
			String str = protectedToString(obj);
			System.out.println("Status :" + str);
			
			CIMObjectPath cimJobPath =
	                getCimObjectPathFromOutputArgs(outArgs, "Job");
			for(CIMArgument arg : outArgs) {
				if(null != arg) 
				System.out.println("OutArgument :"+ arg.getName()+ ":"+ String.valueOf(arg.getValue()));
			}
			
			if(null != cimJobPath) {
			
			CIMInstance instance = client.getInstance(cimJobPath, false, false, null);
			
			
			 CIMProperty<UnsignedInteger16> percentComplete =
             (CIMProperty<UnsignedInteger16>) instance.getProperty("PercentComplete");
			 
			 while(percentComplete.getValue().intValue() < 100) {
				 instance = client.getInstance(cimJobPath, false, false, null);
					
					System.out.println("Running");
				  percentComplete =
	             (CIMProperty<UnsignedInteger16>) instance.getProperty("PercentComplete");
			 }
			
             System.out.println("Completed");
			}
			// FileShare instance will be available in the outArgs.
			
			createFileShare(serviceInstanceId, planId);

		}  finally {
			if (null != fileSystemConfigServicePathItr)
				fileSystemConfigServicePathItr.close();
			if (null != storagePoolsItr)
				storagePoolsItr.close();
			if (null != client)
				closeConnection(client);

		}
	}
	
	 private CIMObjectPath getCimObjectPathFromOutputArgs(CIMArgument[] outputArguments, String key) {
	        CIMObjectPath cimObjectPath = null;
	        for (CIMArgument outArg : outputArguments) {
	            if (outArg != null) {
	                if (outArg.getName().equals(key)) {
	                    cimObjectPath = (CIMObjectPath) outArg.getValue();
	                    break;
	                }
	            }
	        }
	        return cimObjectPath;
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
	
	 public CIMArgument<UnsignedInteger32> uint32(String name, int value) {
	        return build(name, new UnsignedInteger32(value), CIMDataType.UINT32_T);
	    }
	 
	 public CIMArgument<CIMObjectPath[]> referenceArray(String name, CIMObjectPath[] path) {
	        return build(name, path, CIMDataType.getDataType(path));
	    }
	
	 private String protectedToString(Object obj) {
	        String out = "";
	        if (obj != null) {
	            try {
	                out = obj.toString();
	            } catch (RuntimeException runtime) {
	                String message = "Caught an exception while trying to call obj.toString()";
	              
	            }
	        }
	        return out;
	    }
	 
	 public CIMArgument<UnsignedInteger64[]> uint64Array(String name, UnsignedInteger64[] value) {
	        return build(name, value, CIMDataType.UINT64_ARRAY_T);
	    }
	

}
