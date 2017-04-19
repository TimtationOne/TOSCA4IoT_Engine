package org.tosca4iot.csarparsing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.tosca4iot.toscatypes.NodeType;
import org.tosca4iot.toscatypes.NodeTypeHTTPInterface;
import org.tosca4iot.toscatypes.NodeTypeSSHInterface;
import org.tosca4iot.toscatypes.ServiceTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class CsarToServiceTemplateListParser {
	public static void main(String args[]){
		CsarToServiceTemplateListParser parser = new CsarToServiceTemplateListParser("C:/Users/ebnert/OneDrive - Hewlett Packard Enterprise/Documents/shortPath/20170407TOSCA4IoT/TOSCA4IoT_Engine_new/CSARs/TISensorTag2OPCUA/TISensorTag2OPCUA");
		
	}
	private String rootPath;
	private List<ServiceTemplate> serviceTemplateList;
	
	public CsarToServiceTemplateListParser(String rootPath){
		this.rootPath = rootPath;
		try {
			String entryDef=getEntryDef();
			System.out.println("Entry Definition: "+entryDef);
			String serviceTemplatePath =  rootPath+"/Definitions/"+entryDef;
			NodeList relationShipTemplates = getRelationShipTemplates(entryDef);
			//List<List<String>> orderList2Dims = connectAppsTo2DimList_Legacy(relationShipTemplates);
			serviceTemplateList = getOrderedServiceTemplateList(relationShipTemplates);
			//gut bishier
			//orderList2Dims = parseNodeTypesIntoList_legacy(relationShipTemplates,orderList2Dims);
			parseNodeTypesIntoList(relationShipTemplates,serviceTemplateList);
			//print2DimList(orderList2Dims);
			CsarToNodeTemplateParser parser = new CsarToNodeTemplateParser(rootPath);
			PropertyParser proPar = new PropertyParser(serviceTemplatePath);
			
			for (int i=0; i< serviceTemplateList.size() ;i++){
				//List<NodeType> nodeTypeList = orderListToNodeTypeList_legacy(orderList2Dims.get(i),serviceTemplateList.get(i));
				List<NodeType> nodeTypeList = serviceTemplateList.get(i).getTheNodeTypes();
				parser.parse(nodeTypeList );
				serviceTemplateList.get(i).setTheNodeTypes(nodeTypeList);
				proPar.parse(serviceTemplateList.get(i));
				serviceTemplateList.get(i).connectsToCheck();
				System.out.println(serviceTemplateList.get(i).toString() );
			}
			System.out.println(serviceTemplateList.get(1).getTheNodeTypes().get(2).getName()  ) ;
			System.out.println(serviceTemplateList.get(1).getTheNodeTypes().get(2).getConnectedTo()) ;
			System.out.println(serviceTemplateList.get(2).getTheNodeTypes().get(4).getName()  ) ;
			System.out.println(serviceTemplateList.get(2).getTheNodeTypes().get(4).getConnectedTo()) ;
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
	
	public NodeList getRelationShipTemplates(String entryDef) throws SAXException, IOException, ParserConfigurationException{
		File fXmlFile = new File(rootPath+"/Definitions/"+entryDef);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName("tosca:RelationshipTemplate");
	}
	
	public List<List<String>> connectAppsTo2DimList_legacy(NodeList nList) throws ParserConfigurationException, SAXException, IOException {
		List<List<String>> twoDimList = new ArrayList<>();
		String sourceElement = "";
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode ;
				if (eElement.getAttribute("type").equals("winery:Connects_To")){
					sourceElement = ((Element)eElement.getElementsByTagName("tosca:SourceElement").item(0)).getAttribute("ref");
					String targetElement = ((Element)eElement.getElementsByTagName("tosca:TargetElement").item(0)).getAttribute("ref");
					sortNodeTypes2Dim(targetElement,sourceElement,twoDimList);
				}
			}
		}
		if(twoDimList.size()==-1 ){
			List<String> dummyList = new ArrayList<String>();
			dummyList.add(sourceElement);
			twoDimList.add(dummyList);
		}
		return twoDimList;
	}
	
	//***NOch bnicht feritg
	public List<ServiceTemplate> getOrderedServiceTemplateList(NodeList nList) throws ParserConfigurationException, SAXException, IOException {
		List<ServiceTemplate> serTempList =new ArrayList<ServiceTemplate>();
		String sourceElement = "";
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode ;
				if (eElement.getAttribute("type").equals("winery:Connects_To")){
					sourceElement = ((Element)eElement.getElementsByTagName("tosca:SourceElement").item(0)).getAttribute("ref");
					String targetElement = ((Element)eElement.getElementsByTagName("tosca:TargetElement").item(0)).getAttribute("ref");
					ServiceTemplate targetServiceTemplate = new ServiceTemplate(targetElement);
					ServiceTemplate sourceServiceTemplate = new ServiceTemplate(sourceElement);
					sortServiceTemplates(targetServiceTemplate,sourceServiceTemplate,serTempList);

				}
			}
		}
		if(serTempList.size()==-1 && !sourceElement.equals("") ){
			ServiceTemplate dummyServiceTemplate = new ServiceTemplate(sourceElement);
			serTempList.add(dummyServiceTemplate);
		} else if (sourceElement.equals("")){
			new Exception("There are no RelationShipTemplates within the ServiceTemplate. No Provisioning possible");
		}
		return serTempList;
	}
	
	private void sortNodeTypes2Dim(String targetNode, String sourceNode, List<List<String>> orderList2Dims){
		ArrayList<String> sourceList = new ArrayList<String>();
		sourceList.add(sourceNode);
		ArrayList<String> targetList = new ArrayList<String>();
		targetList.add(targetNode);
		
		int targetPos=getOuterIndexOfInnerListItem(targetNode,orderList2Dims);
		int sourcePos=getOuterIndexOfInnerListItem(sourceNode,orderList2Dims);
		if ((targetPos==-1)&&(sourcePos==-1)){
			orderList2Dims.add(targetList);
			orderList2Dims.add(sourceList);
		} else if ((targetPos!=-1)&&(sourcePos!=-1)){
			if (targetPos>sourcePos){
				orderList2Dims.remove(sourcePos);
				targetPos=getOuterIndexOfInnerListItem(targetNode,orderList2Dims);
				if (targetPos+1==orderList2Dims.size()) {
					orderList2Dims.add(sourceList);
				} else{
					orderList2Dims.add(targetPos+1, sourceList);
				}
			}
		} else if ((targetPos!=-1)&&(sourcePos==-1)){
			if (targetPos+1==orderList2Dims.size()) {
				orderList2Dims.add(sourceList);
			} else{
				orderList2Dims.add(targetPos+1, sourceList);
			}
		} else if ((targetPos==-1)&&(sourcePos!=-1)){
			orderList2Dims.add(sourcePos,targetList);
		}
	}
	
	
	
	private void sortServiceTemplates(ServiceTemplate targetServiceTemplate, ServiceTemplate sourceServiceTemplate, List<ServiceTemplate> serviceTemplateList){
		int targetPos=getIndexOfServiceTemplate(targetServiceTemplate,serviceTemplateList) ;
		int sourcePos=getIndexOfServiceTemplate(sourceServiceTemplate,serviceTemplateList) ;
		if ((targetPos==-1)&&(sourcePos==-1)){
			serviceTemplateList.add(targetServiceTemplate);
			serviceTemplateList.add(sourceServiceTemplate);
			sourceServiceTemplate.getTheNodeTypes().get(0).setConnectedTo(
					targetServiceTemplate.getTheNodeTypes().get(0));
		} else if ((targetPos!=-1)&&(sourcePos!=-1)){
			serviceTemplateList.get(sourcePos).getTheNodeTypes().get(0).setConnectedTo(
					serviceTemplateList.get(targetPos).getTheNodeTypes().get(0));
			if (targetPos>sourcePos){
				serviceTemplateList.remove(sourcePos);
				targetPos=getIndexOfServiceTemplate(targetServiceTemplate,serviceTemplateList);
				if (targetPos+1==serviceTemplateList.size()) {
					serviceTemplateList.add(sourceServiceTemplate);
				} else{
					serviceTemplateList.add(targetPos+1, sourceServiceTemplate);
				}
			}
		} else if ((targetPos!=-1)&&(sourcePos==-1)){//New Source
			sourceServiceTemplate.getTheNodeTypes().get(0).setConnectedTo(
					serviceTemplateList.get(targetPos).getTheNodeTypes().get(0));
			if (targetPos+1==serviceTemplateList.size()) {
				serviceTemplateList.add(sourceServiceTemplate);
			} else{
				serviceTemplateList.add(targetPos+1, sourceServiceTemplate);
			}
		} else if ((targetPos==-1)&&(sourcePos!=-1)){ //New Target
			serviceTemplateList.add(sourcePos,targetServiceTemplate);
			serviceTemplateList.get(sourcePos).getTheNodeTypes().get(0).setConnectedTo(
					targetServiceTemplate.getTheNodeTypes().get(0));
		}
	}
	
	private int getIndexOfServiceTemplate(ServiceTemplate serviceTemplate, List<ServiceTemplate> serviceTemplateList){
		for(int i=0; i<serviceTemplateList.size() ; i++){
			if(serviceTemplateList.get(i).getConnectApp().equals(serviceTemplate.getConnectApp()) ){
				return i;
			}
		}
		return -1;
	}

	
	public List<List<String>> parseNodeTypesIntoList_legacy(NodeList relationshipTemplates,List<List<String>> orderList2Dims){
		for(int outer=0;outer<orderList2Dims.size();outer++ ){
			List<String> innerList = orderList2Dims.get(outer);
			String rootElement = getRootItem(relationshipTemplates, innerList.get(0));
			if(!rootElement.equals(innerList.get(0)) ){
				innerList.add(0, rootElement);
			}
			for (int inner=0; inner<innerList.size();inner++ ){
				String item = innerList.get(inner);
				addSourceElements_legacy(relationshipTemplates,innerList,item);
			}
		}
		return orderList2Dims;
	}
	
	public void parseNodeTypesIntoList(NodeList relationshipTemplates,List<ServiceTemplate> serviceTemplateList){
		for(int outer=0;outer<serviceTemplateList.size();outer++ ){
			List<NodeType> nodeTypeList = serviceTemplateList.get(outer).getTheNodeTypes();
			String rootElement = getRootItem(relationshipTemplates, nodeTypeList.get(0).getName());
			if(!rootElement.equals(nodeTypeList.get(0).getName()) ){
				nodeTypeList.add(0, new NodeType(rootElement,serviceTemplateList.get(outer)));
			}
			for (int inner=0; inner<nodeTypeList.size();inner++ ){
				String item = nodeTypeList.get(inner).getName();
				addSourceElements(relationshipTemplates,nodeTypeList,item,serviceTemplateList.get(outer));
			}
		}
	}
	
	public String getRootItem(NodeList nList, String randomItem) {
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode ;
				if (!eElement.getAttribute("type").equals("winery:Connects_To")){
					String sourceElement = ((Element)eElement.getElementsByTagName("tosca:SourceElement").item(0)).getAttribute("ref");
					String targetElement = ((Element)eElement.getElementsByTagName("tosca:TargetElement").item(0)).getAttribute("ref");
					if(randomItem.equals(sourceElement)){
						//Check new targetItem if its root
						return getRootItem(nList,targetElement);
					}
				}
			}
		}
		return randomItem;
	}
	
	
	public void addSourceElements_legacy(NodeList nList, List<String> list, String item) {
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode ;
				if (!eElement.getAttribute("type").equals("winery:Connects_To")){
					String sourceElement = ((Element)eElement.getElementsByTagName("tosca:SourceElement").item(0)).getAttribute("ref");
					String targetElement = ((Element)eElement.getElementsByTagName("tosca:TargetElement").item(0)).getAttribute("ref");
					if(item.equals(targetElement)){
						int sourcePos = list.indexOf(sourceElement);
						int itemPos = list.indexOf(item);
						if (sourcePos==-1){
							list.add(sourceElement);
						} else {
							if (sourcePos<itemPos){
								list.remove(sourcePos);
								itemPos = list.indexOf(item);
								if (list.size()==itemPos+1){
									list.add(sourceElement);
								} else{
									list.add(itemPos+1,sourceElement);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void addSourceElements(NodeList nList, List<NodeType> nodeTypeList, String item, ServiceTemplate theServiceTemplate) {
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode ;
				if (!eElement.getAttribute("type").equals("winery:Connects_To")){
					String sourceElement = ((Element)eElement.getElementsByTagName("tosca:SourceElement").item(0)).getAttribute("ref");
					String targetElement = ((Element)eElement.getElementsByTagName("tosca:TargetElement").item(0)).getAttribute("ref");
					if(item.equals(targetElement)){
						int sourcePos = getIndexOfNodeType(sourceElement,nodeTypeList);
						int itemPos = getIndexOfNodeType(item,nodeTypeList);
						if (sourcePos==-1){
							nodeTypeList.add(new NodeType(sourceElement,theServiceTemplate));
						} else {
							if (sourcePos<itemPos){
								NodeType tempo = nodeTypeList.get(sourcePos);
								nodeTypeList.remove(sourcePos);
								itemPos = getIndexOfNodeType(item,nodeTypeList);
								if (nodeTypeList.size()==itemPos+1){
									nodeTypeList.add(tempo);
								} else{
									nodeTypeList.add(tempo);
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	private <T> int getOuterIndexOfInnerListItem(Object listItem ,List<List<T>> orderList2Dims ){
		for(int outer=0;outer<orderList2Dims.size();outer++ ){
			List<T> innerList = orderList2Dims.get(outer);
			for (int inner=0; inner<innerList.size();inner++ ){
				if(innerList.get(inner).equals(listItem)){
					return outer;
				}
			}
		}
		return -1;
	}
	
	private int getIndexOfNodeType(String nodeTypeName ,List<NodeType> nodeTypeList ){
		for(int outer=0;outer<nodeTypeList.size();outer++ ){
			if(nodeTypeList.get(outer).getName().equals(nodeTypeName)){
				return outer;
			}
		}
		return -1;
	}
	
	private <T> void print2DimList(List<List<T>> theList){
		for(int outer=0;outer<theList.size();outer++ ){
			List<T> innerList = theList.get(outer);
			for (int inner=0; inner<innerList.size();inner++ ){
				System.out.print(" | "+innerList.get(inner)+" | " );
			}
			System.out.println();
		}
	}
	
	private List<ServiceTemplate> string2DimListToServiceTemplateList(List<List<String>> string2DimList){
		List<ServiceTemplate> serviceTemplateList = new ArrayList<ServiceTemplate>();
		for(int i=0;i<string2DimList.size();i++){
			ServiceTemplate theServiceTemplate = new ServiceTemplate(string2DimList.get(i).get(0));
			serviceTemplateList.add(theServiceTemplate);
		}
		return serviceTemplateList;
	}
	private List<NodeType> orderListToNodeTypeList_legacy(List<String> orderList, ServiceTemplate nodeTypeSeriveTemplate){
		List<NodeType> nodeTypeList = new ArrayList<NodeType>();
		for(int i=0;i<orderList.size();i++ ){
			nodeTypeList.add(new NodeType(orderList.get(i),nodeTypeSeriveTemplate));
		}
		return nodeTypeList;
	}


	public List<ServiceTemplate> getServiceTemplateList() {
		return serviceTemplateList;
	}

	private void propertiesParsing(){
		//getNodeTemplates
		//forEveryNodeTemplate
			//Check IF Properties
					//GetChildsOfChild
					//for everyGrandChild
						//add PropertyKey
						//ifAvailable addPropertyGrandChild
	}
}
