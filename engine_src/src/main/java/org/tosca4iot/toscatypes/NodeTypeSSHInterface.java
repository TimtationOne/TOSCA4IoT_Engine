package org.tosca4iot.toscatypes;

import java.io.IOException;

import org.tosca4iot.webinterface.SSH;

import com.jcraft.jsch.JSchException;

public class NodeTypeSSHInterface extends NodeTypeInterface {
	public NodeTypeSSHInterface(ServiceTemplate serviceTemplate) {
		super(serviceTemplate);
		// TODO Auto-generated constructor stub
	}
	private boolean install;
	private boolean config;
	private boolean start;
	
	private String installRefFilePath;
	private String configRefFilePath;
	private String startRefFilePath;
	
	private String installFilePath;
	private String configFilePath;
	private String startFilePath;

	@Override
	public void executeInterfaceMethods() {
		String host = super.getServiceTemplate().getProperty("IP");
		String user = super.getServiceTemplate().getProperty("Rasp_User");
		String password = super.getServiceTemplate().getProperty("Rasp_Password");
		SSH transferClient = new SSH(host,user,password);
		try {
			String targetPath = "/home/"+user+"/tosca4iot/scripts";
			transferClient.execCommand("mkdir /home/"+user+"/tosca4iot ; mkdir "+targetPath);
			if(install){
				transferClient.fileTransfer(installFilePath, targetPath);
				String installScriptPath = targetPath+"/"+installFilePath.split("/")[installFilePath.split("/").length-1];
				transferClient.execCommand("sudo sh "+installScriptPath);
			}
			if(config){
				String targetURL=
						super.getServiceTemplate().getProperty("OPCUA_Protocol")+"://"+
						super.getServiceTemplate().getProperty("Target_IP")+":"+
						super.getServiceTemplate().getProperty("OPCUA_Port");
				transferClient.fileTransfer(configFilePath, targetPath);
				String configScriptPath = targetPath+"/"+configFilePath.split("/")[configFilePath.split("/").length-1];
				transferClient.execCommand("sudo sh "+configScriptPath+" "+targetURL);
			}
			if(start){
				transferClient.fileTransfer(startFilePath, targetPath);
				String startScriptPath = targetPath+"/"+startFilePath.split("/")[startFilePath.split("/").length-1];
				transferClient.execCommand("sudo sh "+startScriptPath);
			}
		} catch (JSchException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getInstallRefFilePath() {
		return installRefFilePath;
	}
	public void setInstallRefFilePath(String installRefFilePath) {
		install = true;
		this.installRefFilePath = installRefFilePath;
	}
	public String getConfigRefFilePath() {
		return configRefFilePath;
	}
	public void setConfigRefFilePath(String configRefFilePath) {
		config = true;
		this.configRefFilePath = configRefFilePath;
	}
	public String getStartRefPath() {
		return startRefFilePath;
	}
	public void setStartRefFilePath(String startRefFilePath) {
		start = true;
		this.startRefFilePath = startRefFilePath;
	}
	public String getInstallFilePath() {
		return installFilePath;
	}
	public void setInstallFilePath(String installFilePath) {
		this.installFilePath = installFilePath;
	}
	public String getConfigFilePath() {
		return configFilePath;
	}
	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}
	public String getStartFilePath() {
		return startFilePath;
	}
	public void setStartFilePath(String startFilePath) {
		this.startFilePath = startFilePath;
	}
	
}
