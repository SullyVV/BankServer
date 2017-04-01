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
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by JohnDong on 2017/3/31.
 */
public class XmlUtil {
    class TransferReq {
        String queryType;
        String value;
        String req;
        public TransferReq(String queryType, String value, String req) {
            this.queryType = queryType;
            this.value = value;
            this.req = req;
        }
    }
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
    public void initOpsArray(Document xmlDocument, ArrayList<OpsInfo> opsArray, CopyOnWriteArrayList<TransInfo> transArray) {
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
            // handle nodes in query and other operations separately
            if (nodeList.item(i).getNodeName().equals("query")) {
                queryHandler(nodeList.item(i), currOp);             // for query
            } else {
                // for create, transfer, balance.......
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
                        currOp.addTag(currNode.getChildNodes().item(0).getNodeValue());
                    }
                }
            }
            if (currOp.getOpsType().equals("transfer")) {
                transArray.add(initTrans(currOp));
            }
            opsArray.add(currOp);

        }
    }

    private TransInfo initTrans(OpsInfo currOp) {
        TransInfo currTrans = new TransInfo();
        currTrans.setToActNum(currOp.getToActNum());
        currTrans.setFromActNum(currOp.getFromActNum());
        currTrans.setAmt(currOp.getAmt());
        for (String tag : currOp.getTagArray()) {
            currTrans.addTag(tag);
        }
        return currTrans;
    }

    private void queryHandler(Node currNode, OpsInfo currOp) {
        System.out.println("In Query Handler");
        NodeList nodeList = currNode.getChildNodes();
        ArrayList<TransferReq> orArray = new ArrayList<>();
        ArrayList<TransferReq> andArray = new ArrayList<>();
        ArrayList<TransferReq> notArray = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currQueryNode = nodeList.item(i);
            if (currQueryNode.getNodeName().equals("or")) {
                orHandler(orArray, currQueryNode);
            } else if (currQueryNode.getNodeName().equals("not")) {
                notHandler(notArray, currQueryNode);
            } else {
                andHandler(andArray, currQueryNode);
            }
        }
        currOp.getQueryInfo().setOrArray(orArray);
        currOp.getQueryInfo().setAndArray(andArray);
        currOp.getQueryInfo().setNotArray(notArray);
    }

    private void orHandler(ArrayList<TransferReq> orArray, Node currQueryNode) {
        NodeList nodeList = currQueryNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currNode = nodeList.item(i);
            addToArray(currNode, orArray);
        }
    }
    private void notHandler(ArrayList<TransferReq> notArray, Node currQueryNode) {
        NodeList nodeList = currQueryNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currNode = nodeList.item(i);
            addToArray(currNode, notArray);
        }
    }
    private void andHandler(ArrayList<TransferReq> andArray, Node currQueryNode) {
        if (currQueryNode.getNodeName().equals("and")) {
            NodeList nodeList = currQueryNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node currNode = nodeList.item(i);
                addToArray(currNode, andArray);
            }
        } else {
            addToArray(currQueryNode, andArray);
        }
    }

    private void addToArray(Node currNode, ArrayList<TransferReq> array) {
        if (currNode.getAttributes().getNamedItem("from") != null) {
            array.add(new TransferReq("from", currNode.getAttributes().getNamedItem("from").getNodeValue(), currNode.getNodeName()));
        }
        if (currNode.getAttributes().getNamedItem("to") != null) {
            array.add(new TransferReq("to", currNode.getAttributes().getNamedItem("to").getNodeValue(), currNode.getNodeName()));
        }
        if (currNode.getAttributes().getNamedItem("amount") != null) {
            array.add(new TransferReq("amount", currNode.getAttributes().getNamedItem("amount").getNodeValue(), currNode.getNodeName()));
        }
        if (currNode.getAttributes().getNamedItem("info") != null) {
            array.add(new TransferReq("tag","info",currNode.getAttributes().getNamedItem("info").getNodeValue()));
        }
    }
}
