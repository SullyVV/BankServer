package common.operationType;

/**
 * Created by JohnDong on 2017/4/1.
 */
public class CreateOps extends OpsType {
    long actNum;
    double bal;
    public CreateOps() {
        actNum = -1;
        bal = 0;
    }

    public long getActNum() {
        return actNum;
    }

    public void setActNum(long actNum) {
        this.actNum = actNum;
    }

    public double getBal() {
        return bal;
    }

    public void setBal(double bal) {
        this.bal = bal;
    }
}
