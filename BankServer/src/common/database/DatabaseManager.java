package common.database;
import common.operationType.CreateOps;
import common.operationType.QueryOps;
import common.operationType.TransferOps;
import common.util.XmlUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by JohnDong on 2017/4/3.
 */
public class DatabaseManager {
    static final String DATABASE = "bankInfo";
    static final String USER = "admin";
    static final String PASSWORD = "ece590";
    private Connection conn;
    private HashMap<String, String> queryMap = createMap();
    private String url = null;

    public DatabaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.url =  "jdbc:postgresql://localhost/" + DATABASE + "?user=" + USER + "&password=" + PASSWORD;
    }


    private HashMap<String,String> createMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("equals", "=");
        map.put("greater", ">");
        map.put("less", "<");
        map.put("to", "TOACTNUM");
        map.put("from", "FROMACTNUM");
        map.put("amount", "AMOUNT");
        return map;
    }

    public void initActMap() {
         try (Connection conn = DriverManager.getConnection(url);
              Statement stmt = conn.createStatement()) {
             String dropAct = "DROP TABLE IF EXISTS ACTMAP";
             stmt.executeUpdate(dropAct);
             String actMap = "CREATE TABLE ACTMAP" +
                             "(ACTNUM    BIGINT PRIMARY KEY     NOT NULL DEFAULT 0," +
                             " BALANCE   DOUBLE PRECISION       NOT NULL DEFAULT 0);";
             stmt.executeUpdate(actMap);
         } catch (SQLException e) {
             e.printStackTrace();
         }
    }

    public void initTransfers() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String droptrs = "DROP TABLE IF EXISTS TRANSFERS;";
            stmt.executeUpdate(droptrs);
            String trs = "CREATE TABLE TRANSFERS" +
                         "(TOACTNUM    BIGINT               NOT NULL DEFAULT 0," +
                         " FROMACTNUM  BIGINT               NOT NULL DEFAULT 0," +
                         " AMOUNT     DOUBLE PRECISION     NOT NULL DEFAULT 0," +
                         " TAGS        TEXT[]         NOT NULL DEFAULT '{}');";
            stmt.executeUpdate(trs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void buildIndex() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String dropToIndex = "DROP INDEX IF EXISTS TO_IDX;";
            String dropFromIndex = "DROP INDEX IF EXISTS FROM_IDX;";
            String dropAmountIdx = "DROP INDEX IF EXISTS AMOUNT_IDX;";
            stmt.executeUpdate(dropToIndex);
            stmt.executeUpdate(dropFromIndex);
            stmt.executeUpdate(dropAmountIdx);
            String toIdx = "CREATE INDEX IF NOT EXISTS TO_IDX ON TRANSFERS(TOACTNUM);";
            String fromIdx = "CREATE INDEX IF NOT EXISTS FROM_IDX ON TRANSFERS(FROMACTNUM);";
            String amountIdx = "CREATE INDEX IF NOT EXISTS AMOUNT_IDX ON TRANSFERS(AMOUNT);";
            stmt.executeUpdate(toIdx);
            stmt.executeUpdate(fromIdx);
            stmt.executeUpdate(amountIdx);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String sql1 = "TRUNCATE TABLE ACTMAP;";
            String sql2 = "TRUNCATE TABLE TRANSFERS";
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            String dropToIndex = "DROP INDEX IF EXISTS TO_IDX;";
            String dropFromIndex = "DROP INDEX IF EXISTS FROM_IDX;";
            String dropAmountIdx = "DROP INDEX IF EXISTS AMOUNT_IDX;";
            stmt.executeUpdate(dropToIndex);
            stmt.executeUpdate(dropFromIndex);
            stmt.executeUpdate(dropAmountIdx);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkActNum(long actNum) {
        boolean flag = false;
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT * FROM ACTMAP WHERE ACTNUM = %d;", actNum);
            ResultSet rs = stmt.executeQuery(sql);
            flag = rs.next();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void createAct(CreateOps op, double bal) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String sql = String.format("INSERT INTO ACTMAP (ACTNUM, BALANCE) VALUES (%d, %f);", op.getActNum(), bal);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
                        op.setResType("error");
            op.setResMsg("Already exists");

        }
    }

    public double checkBal(long actNum) {
        double bal = 0;
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT BALANCE FROM ACTMAP WHERE ACTNUM = (%d);", actNum);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                bal = rs.getDouble("BALANCE");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bal;
    }

    public void transfer(long fromActNum, long toActNum, double amt) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String updateFrom = String.format("UPDATE ACTMAP set BALANCE = BALANCE - %f where ACTNUM = %d", amt, fromActNum);
            String updateTo = String.format("UPDATE ACTMAP set BALANCE = BALANCE + %f where ACTNUM = %d", amt, toActNum);
            stmt.executeUpdate(updateFrom);
            stmt.executeUpdate(updateTo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void recordTransfers(TransferOps op) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            String str = "";
            if (op.getTagArray().size() > 0) {
                str = "{";
                for (int i = 0; i < op.getTagArray().size() - 1; i++) {
                    str += op.getTagArray().get(i);
                    str += ", ";
                }
                str += op.getTagArray().get(op.getTagArray().size() - 1);
                str += "}";
            } else {
                str = "{}";
            }
            String sql = String.format("INSERT INTO TRANSFERS (TOACTNUM, FROMACTNUM, AMOUNT, TAGS) VALUES (%d, %d, %f, '%s');", op.getToActNum(), op.getFromActNum(), op.getAmt(), str);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TransferOps> queryRes(QueryOps op) {
        ArrayList<TransferOps> resArray = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
                        StringBuilder sql = new StringBuilder("SELECT * FROM TRANSFERS WHERE ");
            buildQuerySql(sql, op.getQueryInfo().getOrArray(), " OR ");   // deal with orArray
            if (op.getQueryInfo().getNotArray().size() > 0) {
                sql.append(" AND ");
            }
            buildQuerySql(sql, op.getQueryInfo().getNotArray(), " NOT ");  // deal with notArray
            if (op.getQueryInfo().getAndArray().size() > 0) {
                sql.append(" AND ");
            }
            buildQuerySql(sql, op.getQueryInfo().getAndArray(), " AND ");  // deal with andArray
            sql.append(";");
            ResultSet rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                TransferOps currOp = new TransferOps();
                currOp.setToActNum(rs.getLong("TOACTNUM"));
                currOp.setFromActNum(rs.getLong("FROMACTNUM"));
                currOp.setAmt(rs.getDouble("AMOUNT"));
                String[] tagsArray = (String[]) rs.getArray("TAGS").getArray();
                currOp.setTagArray(new ArrayList<String>(Arrays.asList(tagsArray)));
                resArray.add(currOp);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resArray;
    }

    private void buildQuerySql(StringBuilder sql, ArrayList<XmlUtil.TransferReq> array, String s) {
        String str = " AND ";
        if (s.equals(" OR ")) {
            sql.append("(");
            str = " OR ";
        }
        for (int i = 0; i < array.size() - 1; i++) {
            if (s.equals(" NOT ")) {
                sql.append(" NOT ");
            }
            if (array.get(i).queryType != "tag") {
                sql.append(queryMap.get(array.get(i).queryType));
                sql.append(queryMap.get(array.get(i).req));
                sql.append(array.get(i).value);
            } else {
                handleTag(sql, array.get(i));
            }
            sql.append(str);
        }
        if (array.size() > 0) {
            if (s.equals(" NOT ")) {
                sql.append(" NOT ");
            }
            if (array.get(array.size() - 1).queryType != "tag") {
                sql.append(queryMap.get(array.get(array.size() - 1).queryType));
                sql.append(queryMap.get(array.get(array.size() - 1).req));
                sql.append(array.get(array.size() - 1).value);
            } else {
                handleTag(sql, array.get(array.size() - 1));
            }
        }
        if (s.equals(" OR ")) {
            sql.append(")");
        }
    }

    private void handleTag(StringBuilder sql, XmlUtil.TransferReq transferReq) {
        sql.append("'");
        sql.append(transferReq.value);
        sql.append("' = ANY(TAGS::TEXT[]) ");
    }
}
