package org.tosca4iot.toscatypes;

import java.util.HashMap;
import java.util.Map;

public class NodeType {
	private String name;
	private ServiceTemplate serviceTemplate;
	private String fileName;
	private String implementationFileName;
	private NodeTypeInterface lifecycleInterface;
	private Map<String,String> localProperties = new HashMap<String,String>();;
	private NodeType connectedTo=null;
	
	public NodeType(String name,ServiceTemplate serviceTemplate){
		this.name = name;
		this.serviceTemplate=serviceTemplate;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public NodeTypeInterface getLifecycleInterface() {
		return lifecycleInterface;
	}
	public void setLifecycleInterface(NodeTypeInterface lifecycleInterface) {
		this.lifecycleInterface = lifecycleInterface;
	}
	
	public void executeProvisioning(){
		this.lifecycleInterface.executeInterfaceMethods();
	}

	public String getImplementationFileName() {
		return implementationFileName;
	}

	public void setImplementationFileName(String implementationFileName) {
		this.implementationFileName = implementationFileName;
	}

	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}
	public String getProperty(String key) {
		String theProperty = localProperties.get(key);
		if(theProperty==null){
			this.serviceTemplate.getProperty(key);
		}
		return localProperties.get(key) ;
	}
	public void addProperty(String key, String value) {
		localProperties.put(key, value);
		this.serviceTemplate.addProperty(key, value);
	}
	
	public void conntectsToCheck(){
		if(this.connectedTo!=null){
			System.out.println("not null I should add someThing");
			addProperty("targetIP",this.connectedTo.getServiceTemplate().getProperty("IP-Address"));
		}
	}

	public NodeType getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(NodeType connectedTo) {
		this.connectedTo = connectedTo;
	}
	
	public NodeType getCopy(){
		NodeType clone = new NodeType(this.name,this.serviceTemplate);
		clone.setConnectedTo(this.connectedTo);
		clone.setImplementationFileName(this.implementationFileName);
		clone.setLifecycleInterface(this.lifecycleInterface);
		return clone;
	}
	
}
