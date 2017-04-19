package org.tosca4iot.toscatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ServiceTemplate {
	private String connectApp;
	private List<NodeType> theNodeTypes;
	private Map<String,String> theProperties = new HashMap<String,String>();
	public ServiceTemplate (String connectApp){
		this.connectApp=connectApp;
		theNodeTypes = new ArrayList<NodeType>();
		NodeType connectAppNodeType = new NodeType(connectApp,this);
		theNodeTypes.add(connectAppNodeType);
	}
	
	public List<NodeType> getTheNodeTypes() {
		return theNodeTypes;
	}
	public void setTheNodeTypes(List<NodeType> theNodeTypes) {
		this.theNodeTypes = theNodeTypes;
	}
	public Map<String, String> getTheProperties() {
		return theProperties;
	}
	public String getProperty(String key) {
		return theProperties.get(key) ;
	}
	public void addProperty(String key, String value) {
		theProperties.put(key, value);
	}
	public void addProperty(String key) {
		theProperties.put(key, "");
	}
	@Override
	public String toString(){
		String objectString = "";
		objectString+="--------------------------------\n";
		objectString+="ConnectApp: "+connectApp + "\n" ;
		objectString+="NodeTemplates: "+listToString(theNodeTypes) + "\n" ;
		objectString+="Properties: "+propertiesToString() + "\n" ;
		objectString+="-------------------------------";
		return objectString;
	}
	
	private String listToString(List<NodeType> theList){
		String listString="";
		for (int i=0;i<theList.size();i++ ){
			listString+=" "+theList.get(i).getName() +" ";
		}
		return listString;
	}
	
	private String propertiesToString(){
	    Iterator<Entry<String, String>> it = theProperties.entrySet().iterator();
	    String propertyString="";
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        propertyString+=" "+pair.getKey() + "=" + pair.getValue()+" ";
	    }
		return propertyString;
	}
	
	public void connectsToCheck(){
		for (int i=0;i<this.getTheNodeTypes().size();i++ ){
			this.getTheNodeTypes().get(i).conntectsToCheck();
		}
	}

	public String getConnectApp() {
		return connectApp;
	}
	
	
}
