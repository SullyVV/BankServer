package common;

import java.util.ArrayList;

public class OpsInfo {
    private String ref;
    private String opsType;
    private double amt;
    private double bal;
    private int actNum;
    private int fromActNum;
    private int toActNum;
    private ArrayList<String> tagArray;
    private QueryInfo queryInfo;
    private String result;
    public OpsInfo() {
        this.ref = "";
        this.opsType = "";
        this.amt = 0;
        this.actNum = 0;
        this.fromActNum = 0;
        this.toActNum = 0;
        this.tagArray = new ArrayList<>();
        this.queryInfo = new QueryInfo();
        this.result = "";
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return ref;
    }

    public void setOpsType(String opsType) {
        this.opsType = opsType;
    }

    public String getOpsType() {
        return opsType;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public double getAmt() {
        return amt;
    }

    public void setBal(double bal) {
        this.bal = bal;
    }

    public double getBal() {
        return bal;
    }

    public void setActNum(int actNum) {
        this.actNum = actNum;
    }

    public int getActNum() {
        return actNum;
    }

    public void setFromActNum(int fromActNum) {
        this.fromActNum = fromActNum;
    }

    public int getFromActNum() {
        return fromActNum;
    }

    public void setToActNum(int toActNum) {
        this.toActNum = toActNum;
    }

    public int getToActNum() {
        return toActNum;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void addTag(String tag) {
        tagArray.add(tag);
    }
    public ArrayList<String>getTagArray() {
        return this.tagArray;
    }

    public QueryInfo getQueryInfo() {
        return queryInfo;
    }

    public boolean searchTag(String target) {
        for(String tag : tagArray) {
            if (tag.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
