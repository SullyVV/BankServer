package common;

import java.util.ArrayList;

/**
 * Created by JohnDong on 2017/4/1.
 */
public class TransInfo {
    private int toActNum;
    private int fromActNum;
    private double amt;
    private ArrayList<String> tagArray;
    public TransInfo() {
        this.toActNum = 0;
        this.fromActNum = 0;
        this.amt = 0;
        this.tagArray = new ArrayList<>();
    }

    public int getToActNum() {
        return toActNum;
    }

    public void setToActNum(int toActNum) {
        this.toActNum = toActNum;
    }

    public int getFromActNum() {
        return fromActNum;
    }

    public void setFromActNum(int fromActNum) {
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
    public boolean searchTag(String target) {
        for(String tag : tagArray) {
            if (tag.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
