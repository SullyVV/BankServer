package common.operationType;

/**
 * Created by JohnDong on 2017/4/1.
 */
public class BalanceOps extends OpsType {
    long actNum;
    public BalanceOps() {
        actNum = 0;
    }

    public long getActNum() {
        return actNum;
    }

    public void setActNum(long actNum) {
        this.actNum = actNum;
    }
}
