package org.tosca4iot.csarparsing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.tosca4iot.toscatypes.NodeType;
import org.tosca4iot.toscatypes.NodeTypeHTTPInterface;
import org.tosca4iot.toscatypes.NodeTypeSSHInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class CsarToNodeTemplateParser {
	String rootPath;
	List<NodeType> nodeTemplateList = new ArrayList<NodeType>();
	
	public CsarToNodeTemplateParser(String csarRootPath){
		rootPath=csarRootPath;

	}
	
	public void parse(List<NodeType> nodeTemplateList){
		this.nodeTemplateList=nodeTemplateList;
		try {
			String entryDef=getEntryDef();
			getFullNodeTypeName(entryDef);
			fillNodeTypes(nodeTemplateList);
			
		} catch (ParserConfigurationException | SAXException |IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getEntryDef() throws IOException{
		//TOSCA-Metadata/TOSCA.meta
		String fileContent=getFileContent(rootPath+"/TOSCA-Metadata/TOSCA.meta");
		String entyDef = fileContent.substring(fileContent.indexOf("Entry-Definitions: Definitions/")+31 , fileContent.indexOf(".tosca")+6);
		return entyDef;
	}
	
	private String getFileContent(String path) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
	
	public void getFullNodeTypeName(String entryDef) throws ParserConfigurationException, SAXException, IOException {

		File fXmlFile = new File(rootPath+"/Definitions/"+entryDef);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("tosca:Import");
		//nodeTypeFileNameList = new ArrayList<String>(nodeTypeList);
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eImport = (Element) nNode ;
				String fullFileName=eImport.getAttribute("location");
				for(int i=0; i<nodeTemplateList.size();i++){
					if(fullFileName.contains(nodeTemplateList.get(i).getName()) ){
						nodeTemplateList.get(i).setFileName(fullFileName);
					}
				}
			}
		}
	  }
	
	
	private void fillNodeTypes(List<NodeType> nodeTypeList) throws ParserConfigurationException, SAXException, IOException{
		for(int i=0;i<nodeTypeList.size();i++ ){
			NodeType theNodeType = nodeTypeList.get(i);
			String nodeTypeName = theNodeType.getName() ;
			System.out.println("Process " +nodeTypeName+ " ..." );
			String nodeTypeFileName = theNodeType.getFileName() ;
			if (nodeTypeFileName!=null){
				String nodeTypeInterface = getNodeTypeInterface(nodeTypeFileName);
				System.out.println("Interface: "+nodeTypeInterface);
				if(nodeTypeInterface.equals("ssh_lifecycle")){
					theNodeType.setLifecycleInterface(new NodeTypeSSHInterface(theNodeType.getServiceTemplate()));
				} else if (nodeTypeInterface.equals("http_lifecycle")){
					theNodeType.setLifecycleInterface(new NodeTypeHTTPInterface(theNodeType.getServiceTemplate() ));
				}
				getNodeTypeImplementationFileName(theNodeType);
				if (nodeTypeList.get(i).getImplementationFileName() !=null){
					parseNodeTypeInterfaceRef(theNodeType);
				}
			}
		}
	}
	
	private String getNodeTypeInterface(String nodeTypeFileName) throws ParserConfigurationException, SAXException, IOException{
		File fXmlFile = new File(rootPath+"/Definitions/"+nodeTypeFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("tosca:Interface");
		Node nNode = nList.item(0);
		Element eInterface = (Element) nNode ;
		return (eInterface.getAttribute("name") );
	}
	
	private void getNodeTypeImplementationFileName(NodeType theNodeType) throws ParserConfigurationException, SAXException, IOException{
		File fXmlFile = new File(rootPath+"/Definitions/"+theNodeType.getFileName());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("tosca:Import");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eImport = (Element) nNode ;
			if (eImport.getAttribute("location").contains("_"+theNodeType.getName())){
				theNodeType.setImplementationFileName(eImport.getAttribute("location"));
			}
		}
	}
	
	private void parseNodeTypeInterfaceRef(NodeType theNodeType) throws SAXException, IOException, ParserConfigurationException{
		System.out.println(rootPath+"/Definitions/"+theNodeType.getImplementationFileName());
		File fXmlFile = new File(rootPath+"/Definitions/"+theNodeType.getImplementationFileName());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("tosca:ImplementationArtifact");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eImpArtifact = (Element) nNode ;
			String refPath=eImpArtifact.getAttribute("artifactRef");
			refPath=refPath.replace(":", "__").concat(".tosca");
			String interfacePath = parseNodeTypeInterfaceFile(refPath);
			System.out.println(interfacePath);
			if (eImpArtifact.getAttribute("operationName").equals("install")){
				((NodeTypeSSHInterface)theNodeType.getLifecycleInterface())
				.setInstallRefFilePath(refPath);
				((NodeTypeSSHInterface)theNodeType.getLifecycleInterface())
				.setInstallFilePath(interfacePath);
			} else if (eImpArtifact.getAttribute("operationName").equals("configure")){
				((NodeTypeSSHInterface)theNodeType.getLifecycleInterface())
				.setConfigRefFilePath(refPath);
				((NodeTypeSSHInterface)theNodeType.getLifecycleInterface())
				.setConfigFilePath(interfacePath);
			} else if (eImpArtifact.getAttribute("operationName").equals("start")){
				((NodeTypeSSHInterface)theNodeType.getLifecycleInterface())
				.setStartRefFilePath(refPath);
				((NodeTypeSSHInterface)theNodeType.getLifecycleInterface())
				.setStartFilePath(interfacePath); 
			} else if (eImpArtifact.getAttribute("operationName").equals("installRequest")){
				((NodeTypeHTTPInterface)theNodeType.getLifecycleInterface())
				.setJsonBodyRefFilePath(refPath);
				((NodeTypeHTTPInterface)theNodeType.getLifecycleInterface())
				.setJsonBodyFilePath(interfacePath);
			}
		}
	}
	
	private String parseNodeTypeInterfaceFile(String interfaceRefFilePath) throws SAXException, IOException, ParserConfigurationException{
		File fXmlFile = new File(rootPath+"/Definitions/"+interfaceRefFilePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("tosca:ArtifactReference");
		Node nNode = nList.item(0);
		Element eArtifactRef = (Element) nNode ;
		String path=rootPath+"/"+eArtifactRef.getAttribute("reference").replace("25", "");
		return path;
	}
	
	
	
	
}
