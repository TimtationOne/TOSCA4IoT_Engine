package CSARParsing;

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

import toscaTypes.NodeType;
import toscaTypes.NodeTypeHTTPInterface;
import toscaTypes.NodeTypeSSHInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class CSARParserComplex {
	public static void main(String args[]){
		CSARParserComplex parser = new CSARParserComplex();
		parser.redLine();
	}
	String rootPath="C:/Users/ebnert/OneDrive - Hewlett Packard Enterprise/Documents/shortPath/20170407TOSCA4IoT/TOSCA4IoT_Engine_new/CSARs/TISensorTag2OPCUA/TISensorTag2OPCUA";
	List<NodeType> nodeTypeList = new ArrayList<NodeType>();
	List<List<String>> orderList2Dims = new ArrayList<>();
	//List<String> nodeTypeFileNameList = new ArrayList<String>();
	
	public void redLine(){
	
		try {
			String entryDef=getEntryDef();
			System.out.println("Entry Definition: "+entryDef);
			NodeList relationShipTemplates = getRelationShipTemplates(entryDef);
			connectAppsToList(relationShipTemplates);
			sortList(relationShipTemplates);
			print2DimList(orderList2Dims);
			CSARParser parser = new CSARParser();
			for (int i=0; i< orderList2Dims.size() ;i++){
				
				parser.listToDataModel(orderList2Dims.get(0) );
			}
				

		/*	getFullNodeTypeName(entryDef);
			//printList(nodeTypeFileNameList);
			processNodeTypes();
			*/
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
	
	public void connectAppsToList(NodeList nList) throws ParserConfigurationException, SAXException, IOException {
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode ;
				if (eElement.getAttribute("type").equals("winery:Connects_To")){
					String sourceElement = ((Element)eElement.getElementsByTagName("tosca:SourceElement").item(0)).getAttribute("ref");
					String targetElement = ((Element)eElement.getElementsByTagName("tosca:TargetElement").item(0)).getAttribute("ref");
					sortNodeTypes(targetElement,sourceElement);
				}
			}
		}
	}

	
	public void sortList(NodeList relationshipTemplates){
		for(int outer=0;outer<orderList2Dims.size();outer++ ){
			List<String> innerList = orderList2Dims.get(outer);
			String rootElement = getRootItem(relationshipTemplates, innerList.get(0));
			if(!rootElement.equals(innerList.get(0)) ){
				innerList.add(0, rootElement);
			}
			for (int inner=0; inner<innerList.size();inner++ ){
				String item = innerList.get(inner);
				addSourceElements(relationshipTemplates,innerList,item);
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
	
	
	public void addSourceElements(NodeList nList, List<String> list, String item) {
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
				for(int i=0; i<nodeTypeList.size();i++){
					if(fullFileName.contains(nodeTypeList.get(i).getName()) ){
						nodeTypeList.get(i).setFileName(fullFileName);
					}
				}
			}
		}
	  }
	
	private void sortNodeTypes(String targetNode, String sourceNode){
		//TODO: Connects To Sonderregel
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
			if (targetPos+1==nodeTypeList.size()) {
				orderList2Dims.add(sourceList);
			} else{
				orderList2Dims.add(targetPos+1, sourceList);
			}
		} else if ((targetPos==-1)&&(sourcePos!=-1)){
			orderList2Dims.add(sourcePos,targetList);
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
	
	private <T> void print2DimList(List<List<T>> theList){
		for(int outer=0;outer<theList.size();outer++ ){
			List<T> innerList = theList.get(outer);
			for (int inner=0; inner<innerList.size();inner++ ){
				System.out.print(" | "+innerList.get(inner)+" | " );
			}
			System.out.println();
		}
	}
}
