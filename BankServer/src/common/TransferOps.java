package common;

import java.util.ArrayList;

/**
 * Created by JohnDong on 2017/4/1.
 */
public class TransferOps extends OpsType {
    long toActNum;
    long fromActNum;
    double amt;
    ArrayList<String> tagArray;
    public TransferOps() {
        toActNum = 0;
        fromActNum = 0;
        amt = 0;
        tagArray = new ArrayList<>();
    }

    public long getToActNum() {
        return toActNum;
    }

    public void setToActNum(long toActNum) {
        this.toActNum = toActNum;
    }

    public long getFromActNum() {
        return fromActNum;
    }

    public void setFromActNum(long fromActNum) {
        this.fromActNum = fromActNum;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }
    public void addTag(String tag) {
        tagArray.add(tag);
    }
    public ArrayList<String> getTagArray() {
        return tagArray;
    }
}
