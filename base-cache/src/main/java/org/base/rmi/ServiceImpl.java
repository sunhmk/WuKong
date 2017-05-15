package org.base.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServiceImpl extends UnicastRemoteObject implements IService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private String name;

	public ServiceImpl(String name) throws RemoteException {
		this.name = name;
	}

	@Override
	public String service(String content) {
		return "server >> " + content;
	}
}
