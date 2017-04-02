package common;

import java.util.ArrayList;

/**
 * Created by JohnDong on 2017/4/1.
 */
public class QueryOps extends OpsType {
    QueryInfo queryInfo;
    ArrayList<TransferOps> resArray;
    public QueryOps() {
        queryInfo = new QueryInfo();
        resArray = new ArrayList<>();
    }
    public QueryInfo getQueryInfo() {
        return queryInfo;
    }
    public ArrayList<TransferOps> getResArray() {
        return resArray;
    }
    public void setResArray(ArrayList<TransferOps> resArray) {
        this.resArray = resArray;
    }
}
