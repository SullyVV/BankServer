package common.operationType;

import java.util.ArrayList;

public class OpsType {
    String ref;
    String opsType;
    String resType;
    String resMsg;
    ArrayList<TransferOps> resArray;
    public ArrayList<TransferOps> getResArray() {
        return resArray;
    }
    public void setResArray(ArrayList<TransferOps> resArray) {
        this.resArray = resArray;
    }
    public OpsType() {
        ref = "";
        opsType = "";
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getOpsType() {
        return opsType;
    }

    public void setOpsType(String opsType) {
        this.opsType = opsType;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getResMsg() {
        return resMsg;
    }
}
