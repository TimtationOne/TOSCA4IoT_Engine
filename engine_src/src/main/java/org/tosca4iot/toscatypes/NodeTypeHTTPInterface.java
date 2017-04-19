package org.tosca4iot.toscatypes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.tosca4iot.webinterface.HttpRequest;

public class NodeTypeHTTPInterface extends NodeTypeInterface {
	public NodeTypeHTTPInterface(ServiceTemplate serviceTemplate) {
		super(serviceTemplate);
	}
	private String jsonBody;
	private String jsonBodyFilePath;
	private String jsonBodyRefFilePath;
	private String url;
	@Override
	public void executeInterfaceMethods() {
		HttpRequest request = new HttpRequest();
		String deviceURL= 
				super.getServiceTemplate().getProperty("HTTP_Protocol")+"://"+
				super.getServiceTemplate().getProperty("IP")+":"+
				super.getServiceTemplate().getProperty("HTTP_Port");
		
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(jsonBodyFilePath));
			String osFile = new String(encoded, StandardCharsets.UTF_8); 
			request.send(deviceURL+"/os", "application/json", "POST", osFile );
			request.send(deviceURL+"/reboot", "application/json", "POST", "");
			Thread.sleep(120000);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	public String getJsonBody() {
		return jsonBody;
	}
	public void setJsonBody(String jsonBody) {
		this.jsonBody = jsonBody;
	}
	public String getJsonBodyFilePath() {
		return jsonBodyFilePath;
	}
	public void setJsonBodyFilePath(String jsonBodyFilePath) {
		this.jsonBodyFilePath = jsonBodyFilePath;
	}
	public String getJsonBodyRefFilePath() {
		return jsonBodyRefFilePath;
	}
	public void setJsonBodyRefFilePath(String jsonBodyRefFilePath) {
		this.jsonBodyRefFilePath = jsonBodyRefFilePath;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
