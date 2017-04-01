package common;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by JohnDong on 2017/3/31.
 */
public class XmlUtil {
    public Document parseXml(String txt) {
        InputSource is = new InputSource(new StringReader(txt));
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        // initialize builder
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        // parse string into XML Document
        try {
            document = builder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
    public void initOpsArray(Document xmlDocument, ArrayList<OpsInfo> opsArray) {
        NodeList nodeList = xmlDocument.getDocumentElement().getChildNodes();
        // traverse through each operation
        for (int i = 0; i < nodeList.getLength(); i++) {
            OpsInfo currOp = new OpsInfo();
            if (nodeList.item(i).getAttributes().getNamedItem("ref") != null) {
                currOp.setRef(nodeList.item(i).getAttributes().getNamedItem("ref").getNodeValue());
            }
            currOp.setOpsType(nodeList.item(i).getNodeName());
            // traverse through detail of each operation
            NodeList subNodeList = nodeList.item(i).getChildNodes();
            // traverse through detail of this op
            for (int j = 0; j < subNodeList.getLength(); j++) {
                Node currNode = subNodeList.item(j);
                if (currNode.getNodeName().equals("account")) {
                    currOp.setActNum(Integer.valueOf(currNode.getChildNodes().item(0).getNodeValue()));
                }
                if (currNode.getNodeName().equals("amount")) {
                    currOp.setAmt(Double.valueOf(currNode.getChildNodes().item(0).getNodeValue()));
                }
                if (currNode.getNodeName().equals("balance")) {
                    currOp.setBal(Double.valueOf(currNode.getChildNodes().item(0).getNodeValue()));
                }
                if (currNode.getNodeName().equals("to")) {
                    currOp.setToActNum(Integer.valueOf(currNode.getChildNodes().item(0).getNodeValue()));
                }
                if (currNode.getNodeName().equals("from")) {
                    currOp.setFromActNum(Integer.valueOf(currNode.getChildNodes().item(0).getNodeValue()));
                }
                if (currNode.getNodeName().equals("tag")) {
                    currOp.setTag(currNode.getChildNodes().item(0).getNodeValue());
                }
            }
            opsArray.add(currOp);
        }
    }
}
