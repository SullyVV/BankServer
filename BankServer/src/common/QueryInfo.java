package common;

import java.util.ArrayList;

/**
 * Created by JohnDong on 2017/4/1.
 */
public class QueryInfo {
    private ArrayList<XmlUtil.TransferReq> orArray;
    private ArrayList<XmlUtil.TransferReq> andArray;
    private ArrayList<XmlUtil.TransferReq> notArray;
    public void setOrArray(ArrayList<XmlUtil.TransferReq> orArray) {
        this.orArray = orArray;
    }
    public void setAndArray(ArrayList<XmlUtil.TransferReq> andArray) {
        this.andArray = andArray;
    }
    public void setNotArray(ArrayList<XmlUtil.TransferReq> notArray) {
        this.notArray = notArray;
    }

    public ArrayList<XmlUtil.TransferReq> getOrArray() {
        return orArray;
    }

    public ArrayList<XmlUtil.TransferReq> getAndArray() {
        return andArray;
    }

    public ArrayList<XmlUtil.TransferReq> getNotArray() {
        return notArray;
    }
}
