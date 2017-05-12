package com.emc.eNas.cloudfoundry.broker.config;

public class eNasClientException extends Exception{
	
	public eNasClientException(Exception e) {
		super(e);
	}
	
	public eNasClientException(String error) {
		super(error);
	}

}
