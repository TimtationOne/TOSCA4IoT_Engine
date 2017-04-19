package org.tosca4iot.toscatypes;

public abstract class NodeTypeInterface {
	private static String name;
	private ServiceTemplate serviceTemplate;
	public NodeTypeInterface(ServiceTemplate serviceTemplate){
		this.serviceTemplate=serviceTemplate;
	}
	
	public abstract void executeInterfaceMethods();
	public static String getName() {
		return name;
	}

	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}
	
}
