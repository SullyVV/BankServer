package common;

/**
 * Created by JohnDong on 2017/4/1.
 */
public class QueryOps extends OpsType {
    QueryInfo queryInfo;
    public QueryOps() {
        queryInfo = new QueryInfo();
    }
    public QueryInfo getQueryInfo() {
        return queryInfo;
    }
}
