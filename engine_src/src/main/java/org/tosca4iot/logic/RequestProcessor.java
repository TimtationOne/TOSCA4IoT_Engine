package org.tosca4iot.logic;

import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tosca4iot.csarparsing.CsarToServiceTemplateListParser;
import org.tosca4iot.toscatypes.ServiceTemplate;

@Service
public class RequestProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);
	
	public void process(String body){
		
		JSONObject contentJson = new JSONObject(body);
		String csarName = (String)contentJson.get("CSAR");
		CsarToServiceTemplateListParser parser = new CsarToServiceTemplateListParser("/opt/tosca4iotengine/csars/"+csarName);
		List<ServiceTemplate> serviceTemplateList = parser.getServiceTemplateList();
		if(serviceTemplateList.size()==1 ){
			singleServiceTemplateGetProperties(contentJson,serviceTemplateList.get(0) );
			serviceTemplateList.get(0).connectsToCheck();
			//serviceTemplateList.get(0).startProvisioning();
		} else{
			multiServiceTemplateGetProperties(contentJson,serviceTemplateList);
			for ( int u=0; u<serviceTemplateList.size();u++ ){
				serviceTemplateList.get(u).connectsToCheck();
				//serviceTemplateList.get(u).startProvisioning();
			}
		}
	}
	
	private void singleServiceTemplateGetProperties(JSONObject contentJson, ServiceTemplate serviceTemplate){
		JSONArray properties = (JSONArray) contentJson.get("Properties");
		for ( int i=0; i <properties.length(); i++) {
			JSONObject tempJsonObject = ((JSONObject) properties.get(i));
			Iterator<?> keys = tempJsonObject.keys();
			while( keys.hasNext() ) {
			    String key = (String)keys.next();
			    if(!key.equals("ConnectApp")){
			    	serviceTemplate.addProperty(key, tempJsonObject.getString(key) );
			    }
			}
		}
	}
	
	private void multiServiceTemplateGetProperties(JSONObject contentJson, List<ServiceTemplate> serviceTemplateList){
		JSONArray properties = (JSONArray) contentJson.get("Properties");
		for ( int i=0; i <properties.length(); i++) {
			JSONObject tempJsonObject = ((JSONObject) properties.get(i));
			for ( int u=0; u<serviceTemplateList.size();u++ ){
				if(serviceTemplateList.get(u).getConnectApp().equals((String)tempJsonObject.get("ConnectApp") ) ){
					Iterator<?> keys = tempJsonObject.keys();
					while( keys.hasNext() ) {
					    String key = (String)keys.next();
					    if(!key.equals("ConnectApp")){
					    	serviceTemplateList.get(u).addProperty(key, tempJsonObject.getString(key) );
					    }
					}
				}
			}
		}
	}
	

}
