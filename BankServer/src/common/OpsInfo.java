package common;
public class OpsInfo {
    private String ref;
    /*
     * opsType ------- operation
     *    1:              create
     *    2:              transfer
     *    3:              balance
     *    4:              query
     */
    private String opsType;
    private double amt;
    private double bal;
    private int actNum;
    private int fromActNum;
    private int toActNum;
    private String tag;
    private String result;
    public OpsInfo() {
        this.ref = "";
        this.opsType = "";
        this.amt = 0;
        this.actNum = 0;
        this.fromActNum = 0;
        this.toActNum = 0;
        this.tag = "";
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

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
