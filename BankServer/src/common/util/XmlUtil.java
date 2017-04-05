package common.util;


import common.database.DatabaseManager;
import common.operationType.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by JohnDong on 2017/3/31.
 */
public class XmlUtil {
    private DocumentBuilderFactory builderFactory;
    private DocumentBuilder builder;
    public XmlUtil() {
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = null;
    }
    public void reset(Document xmlDocument, DatabaseManager databaseManager) {
        // reset if needed
        if (xmlDocument.getDocumentElement().hasAttribute("reset") && xmlDocument.getDocumentElement().getAttribute("reset").equals("true")) {
            databaseManager.clearDatabase();
        }
    }

    public void generateXmlFile(Document newXmlDoc, String s) throws TransformerException {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(newXmlDoc);
        s += ".xml";
        Result dest = new StreamResult(new File(s));
        aTransformer.transform(src, dest);
    }

    public String generateString(Document newXmlDoc) {
        try {
            DOMSource domSource = new DOMSource(newXmlDoc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public class TransferReq {
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

    public boolean initOpsArray(Document xmlDocument, ArrayList<OpsType> opsArray) {
        NodeList nodeList = xmlDocument.getDocumentElement().getChildNodes();
        // traverse through each operation
        for (int i = 0; i < nodeList.getLength(); i++) {
            String opsType = nodeList.item(i).getNodeName();
            String opsRef = "";
            if (nodeList.item(i).getAttributes().getNamedItem("ref") != null) {
                opsRef = nodeList.item(i).getAttributes().getNamedItem("ref").getNodeValue();
            }
            OpsType currOp;
            switch (opsType) {
                case "create":
                    currOp = new CreateOps();
                    buildCreateOps(nodeList.item(i), (CreateOps) currOp);
                    break;
                case "transfer":
                    currOp = new TransferOps();
                    buildTransferOps(nodeList.item(i), (TransferOps) currOp);
                    break;
                case "balance":
                    currOp = new BalanceOps();
                    buildBalanceOps(nodeList.item(i), (BalanceOps) currOp);
                    break;
                case "query":
                    currOp = new QueryOps();
                    queryHandler(nodeList.item(i), (QueryOps) currOp);
                    break;
                default:
                    return false;
            }
            currOp.setOpsType(opsType);
            currOp.setRef(opsRef);
            opsArray.add(currOp);
        }
        return true;
    }

    public void processOps(ArrayList<OpsType> opsArray, DatabaseManager databaseManager) {
        for (OpsType op : opsArray) {
            switch (op.getOpsType()) {
                case "create":
                    processCreate((CreateOps)op, databaseManager);
                    break;
                case "transfer":
                    processTransfer((TransferOps)op, databaseManager);
                    break;
                case "balance":
                    processBalance((BalanceOps)op, databaseManager);
                    break;
                case "query":
                    processQuery((QueryOps)op, databaseManager);
                    break;
                default:
                    System.out.print("This case is not possible to happen");
            }
        }
    }

    public void processCreate(CreateOps op, DatabaseManager databaseManager) {
        op.setResType("success");
        op.setResMsg("created");
        databaseManager.createAct(op, op.getBal());
    }

    public Document constructXml(ArrayList<OpsType> opsArray) {
        Document doc = builder.newDocument();
        // root element
        Element rootElement = doc.createElement("results");
        doc.appendChild(rootElement);
        for (OpsType currOp : opsArray) {
            // result element
            String s;
            Element currRes;
            if (!currOp.getOpsType().equals("query")) {
                s = currOp.getResType();
                currRes = doc.createElement(s);
                rootElement.appendChild(currRes);
                // attribute
                if (!currOp.getRef().equals("")) {
                    Attr attr = doc.createAttribute("ref");
                    attr.setValue(currOp.getRef());
                    currRes.setAttributeNode(attr);
                }
                currRes.appendChild(doc.createTextNode(currOp.getResMsg()));
            } else {
                // construct query result
                if (currOp.getResArray().size() > 0) {
                    s = "results";
                    currRes = doc.createElement(s);
                    rootElement.appendChild(currRes);
                    // attribute
                    if (!currOp.getRef().equals("")) {
                        Attr attr = doc.createAttribute("ref");
                        attr.setValue(currOp.getRef());
                        currRes.setAttributeNode(attr);
                    }
                    // traverse through each transfer
                    for (TransferOps currTrans : currOp.getResArray()) {
                        Element transfer = doc.createElement("transfer");
                        currRes.appendChild(transfer);
                        Element from = doc.createElement("from");
                        from.appendChild(doc.createTextNode(String.valueOf(currTrans.getFromActNum())));
                        transfer.appendChild(from);
                        Element to = doc.createElement("to");
                        to.appendChild(doc.createTextNode(String.valueOf(currTrans.getToActNum())));
                        transfer.appendChild(to);
                        Element amt = doc.createElement("amount");
                        amt.appendChild(doc.createTextNode(String.valueOf(currTrans.getAmt())));
                        transfer.appendChild(amt);
                        if (currTrans.getTagArray().size() > 0) {
                            Element tags = doc.createElement("tags");
                            transfer.appendChild(tags);
                            for (String tag : currTrans.getTagArray()) {
                                Element currTag = doc.createElement("tag");
                                currTag.appendChild(doc.createTextNode(tag));
                                tags.appendChild(currTag);
                            }
                        }
                    }
                }
            }
        }
        return doc;
    }

    private void processTransfer(TransferOps op, DatabaseManager databaseManager) {
        if (!databaseManager.checkActNum(op.getToActNum())) {
            op.setResType("error");
            op.setResMsg("To account doesn't exist");
            return;
        }
        if (!databaseManager.checkActNum(op.getFromActNum())) {
            op.setResType("error");
            op.setResMsg("From account doesn't exist");
            return;
        }
        if (databaseManager.checkBal(op.getFromActNum()) < op.getAmt()) {
            op.setResType("error");
            op.setResMsg("Insufficient fund");
            return;
        }
        databaseManager.transfer(op.getFromActNum(), op.getToActNum(), op.getAmt());
        databaseManager.recordTransfers(op);
        op.setResType("success");
        op.setResMsg("transferred");

    }

    private void processBalance(BalanceOps op, DatabaseManager databaseManager) {
        if (!databaseManager.checkActNum(op.getActNum())) {
            op.setResType("error");
            op.setResMsg("Account doesn't exist");
        } else {
            op.setResType("success");
            op.setResMsg(Double.toString(databaseManager.checkBal(op.getActNum())));
        }
    }

    private void processQuery(QueryOps op, DatabaseManager databaseManager) {
        ArrayList<TransferOps> resArray = new ArrayList<>();
        ArrayList<TransferOps> transArray = databaseManager.buildTransArray();
        for (TransferOps currTrans : transArray) {
            boolean flag = false;
            // check reqs in orArray ---> requrie meet any of them
            for (TransferReq currReq: op.getQueryInfo().getOrArray()) {
                if (checkReq(currReq, currTrans)) {
                    flag = true;
                }
            }
            // check reqs in notArray ---> require meet none of them
            for (TransferReq currReq: op.getQueryInfo().getNotArray()) {
                if (checkReq(currReq, currTrans)) {
                    flag = false;
                }
            }
            // check reqs in andArray ---> require meet all of them
            for (TransferReq currReq: op.getQueryInfo().getAndArray()) {
                if (!checkReq(currReq, currTrans)) {
                    flag = false;
                }
            }
            if (flag) {
                resArray.add(currTrans);
            }
        }
        op.setResArray(new ArrayList<>(resArray));
    }

    private boolean checkReq(TransferReq currReq, TransferOps currTrans) {
        // check if a transfer meets current requirement
        boolean flag = false;
        switch (currReq.queryType) {
            case "to":
                flag = checkHelper(currTrans.getToActNum(), Long.valueOf(currReq.value), currReq);
                break;
            case "from":
                flag = checkHelper(currTrans.getFromActNum(), Long.valueOf(currReq.value), currReq);
                break;
            case "amount":
                flag = checkHelper(currTrans.getAmt(), Double.valueOf(currReq.value), currReq);
                break;
            case "tag":
                for (String currTag:currTrans.getTagArray()) {
                    if (currTag.equals(currReq.value)) {
                        flag = true;
                    }
                }
                break;
            default:
                System.out.print("This line should be non-reachable");
        }
        return flag;
    }

    private boolean checkHelper(Long trans, Long req, TransferReq currReq) {
        boolean flag = false;
        switch (currReq.req) {
            case "equals":
                flag = (trans.longValue() == req.longValue());
                break;
            case "greater":
                flag = (trans.longValue() > req.longValue());
                break;
            case "less":
                flag = (trans.longValue() < req.longValue());
                break;
        }
        return flag;
    }

    private boolean checkHelper(Double trans, Double req, TransferReq currReq) {
        boolean flag = false;
        switch (currReq.req) {
            case "equal":
                flag = (trans.doubleValue() == req.doubleValue());
                break;
            case "greater":
                flag = (trans.doubleValue() > req.doubleValue());
                break;
            case "less":
                flag = (trans.doubleValue() < req.doubleValue());
                break;
        }
        return flag;
    }

    private void buildCreateOps(Node currNode, CreateOps currOp) {
        // traverse through detail of create operation
        NodeList subNodeList = currNode.getChildNodes();
        for (int j = 0; j < subNodeList.getLength(); j++) {
            Node currSubNode = subNodeList.item(j);
            if (currSubNode.getNodeName().equals("account")) {
                currOp.setActNum(Long.valueOf(currSubNode.getChildNodes().item(0).getNodeValue()));
            }
            if (currSubNode.getNodeName().equals("balance")) {
                currOp.setBal(Double.valueOf(currSubNode.getChildNodes().item(0).getNodeValue()));
            }
        }
    }

    private void buildTransferOps(Node currNode, TransferOps currOp) {
        // traverse through detail of create operation
        NodeList subNodeList = currNode.getChildNodes();
        for (int j = 0; j < subNodeList.getLength(); j++) {
            Node currSubNode = subNodeList.item(j);
            if (currSubNode.getNodeName().equals("to")) {
                currOp.setToActNum(Long.valueOf(currSubNode.getChildNodes().item(0).getNodeValue()));
            }
            if (currSubNode.getNodeName().equals("from")) {
                currOp.setFromActNum(Long.valueOf(currSubNode.getChildNodes().item(0).getNodeValue()));
            }
            if (currSubNode.getNodeName().equals("amount")) {
                currOp.setAmt(Double.valueOf(currSubNode.getChildNodes().item(0).getNodeValue()));
            }
            if (currSubNode.getNodeName().equals("tag")) {
                currOp.addTag(currSubNode.getChildNodes().item(0).getNodeValue());
            }
        }
    }

    private void buildBalanceOps(Node currNode, BalanceOps currOp) {
        // traverse through detail of create operation
        NodeList subNodeList = currNode.getChildNodes();
        for (int j = 0; j < subNodeList.getLength(); j++) {
            Node currSubNode = subNodeList.item(j);
            if (currSubNode.getNodeName().equals("account")) {
                currOp.setActNum(Long.valueOf(currSubNode.getChildNodes().item(0).getNodeValue()));
            }
        }
    }

    private void queryHandler(Node currNode, QueryOps currOp) {
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
            array.add(new TransferReq("tag", currNode.getAttributes().getNamedItem("info").getNodeValue(), "equal"));
        }
    }
}
