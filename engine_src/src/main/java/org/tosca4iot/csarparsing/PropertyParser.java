package org.tosca4iot.csarparsing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.tosca4iot.toscatypes.NodeType;
import org.tosca4iot.toscatypes.ServiceTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PropertyParser {
	String stFilePath;
	public PropertyParser(String stFilePath){
		this.stFilePath = stFilePath;
	}
	
	public void parse(ServiceTemplate theServiceTemplate) throws ParserConfigurationException, SAXException, IOException{
		List<NodeType> nodeTypeList = theServiceTemplate.getTheNodeTypes();
		NodeList nodeTemplates = getNodeTemplates(stFilePath);
		for(int nodeTypeID=0;nodeTypeID<nodeTypeList.size();nodeTypeID++){
			for (int temp = 0; temp < nodeTemplates.getLength(); temp++) {
				Node nNode = nodeTemplates.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode ;
					if (eElement.getAttribute("id").equals(nodeTypeList.get(nodeTypeID).getName() )){
						addProperties(eElement,nodeTypeList.get(nodeTypeID));
					}
				}
			}
		}
	}
	
	private NodeList getNodeTemplates(String fileName) throws ParserConfigurationException, SAXException, IOException{
		File fXmlFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName("tosca:NodeTemplate");
	}
	
	private void addProperties(Element eElement, NodeType theNodeTemplate){
		if (eElement.hasChildNodes()){
			NodeList toscaProperties = eElement.getElementsByTagName("tosca:Properties");
			NodeList toscaPropertiesChilds = toscaProperties.item(0).getChildNodes();
			
			int counter = 0;
			while(toscaPropertiesChilds.item(counter).getNodeType() != Node.ELEMENT_NODE){
				counter ++;
			}
			NodeList unnecessaryWineryPropElement=toscaPropertiesChilds.item(counter).getChildNodes();
			for(int i = 0; i<unnecessaryWineryPropElement.getLength();i++ ){
				Node nProperty = unnecessaryWineryPropElement.item(i);
				if (nProperty.getNodeType() == Node.ELEMENT_NODE) {
					Element eProperty = (Element) nProperty ;
					theNodeTemplate.addProperty(nProperty.getNodeName(),eProperty.getTextContent());
				}
			}
		}
	}

}
