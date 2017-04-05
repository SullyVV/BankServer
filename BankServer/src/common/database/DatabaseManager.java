package common.database;

import common.operationType.CreateOps;
import common.operationType.TransferOps;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by JohnDong on 2017/4/3.
 */
public class DatabaseManager {
    static final String DATABASE = "bankInfo";
    static final String USER = "admin";
    static final String PASSWORD = "ece590";
    private Connection conn;

    public void initActMap() {
        try {
            connectDatabase();
            Statement stmt = null;
            stmt = conn.createStatement();
            String dropAct = "DROP TABLE IF EXISTS ACTMAP";
            stmt.executeUpdate(dropAct);
            String actMap = "CREATE TABLE ACTMAP" +
                    "(ACTNUM    BIGINT PRIMARY KEY     NOT NULL DEFAULT 0," +
                    " BALANCE   DOUBLE PRECISION       NOT NULL DEFAULT 0);";
            stmt.executeUpdate(actMap);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initTransfers() {
        try {
            connectDatabase();
            Statement stmt = null;
            stmt = conn.createStatement();
            String droptrs = "DROP TABLE IF EXISTS TRANSFERS";
            stmt.executeUpdate(droptrs);
            String trs = "CREATE TABLE TRANSFERS" +
                    "(TOACTNUM    BIGINT               NOT NULL DEFAULT 0," +
                    " FROMACTNUM  BIGINT               NOT NULL DEFAULT 0," +
                    " AMOUNT     DOUBLE PRECISION     NOT NULL DEFAULT 0," +
                    " TAGS        TEXT[]         NOT NULL DEFAULT '{}');";
            stmt.executeUpdate(trs);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearDatabase() {
        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
            String sql1 = "TRUNCATE TABLE ACTMAP;";
            String sql2 = "TRUNCATE TABLE TRANSFERS";
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void connectDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost/" + DATABASE + "?user=" + USER + "&password=" + PASSWORD;
            this.conn = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkActNum(long actNum) {
        boolean flag = false;
        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
            String sql = String.format("SELECT * FROM ACTMAP WHERE ACTNUM = %d;", actNum);
            ResultSet rs = stmt.executeQuery(sql);
            flag = rs.next();
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void createAct(CreateOps op, double bal) {
        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
            String sql = String.format("INSERT INTO ACTMAP (ACTNUM, BALANCE) VALUES (%d, %f);", op.getActNum(), bal);
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("In create account op: account " + op.getActNum() + " already exists");
            op.setResType("error");
            op.setResMsg("Already exists");
        }
    }

    public double checkBal(long actNum) {
        double bal = 0;
        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
            String sql = String.format("SELECT BALANCE FROM ACTMAP WHERE ACTNUM = (%d);", actNum);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                bal = rs.getDouble("BALANCE");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bal;
    }

    public void transfer(long fromActNum, long toActNum, double amt) {
        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
            String getFrom = String.format("SELECT BALANCE FROM ACTMAP WHERE ACTNUM = (%d);", fromActNum);
            String getTo = String.format("SELECT BALANCE FROM ACTMAP WHERE ACTNUM = (%d);", toActNum);
            double fromBal = 0;
            double toBal = 0;
            ResultSet rs1 = stmt.executeQuery(getFrom);
            if (rs1.next()) {
                fromBal = rs1.getDouble("BALANCE");
            }
            rs1.close();
            ResultSet rs2 = stmt.executeQuery(getTo);
            if (rs2.next()) {
                toBal = rs2.getDouble("BALANCE");
            }
            rs2.close();
            fromBal -= amt;
            toBal += amt;
            String updateFrom = String.format("UPDATE ACTMAP set BALANCE = %f where ACTNUM = %d", fromBal, fromActNum);
            String updateTo = String.format("UPDATE ACTMAP set BALANCE = %f where ACTNUM = %d", toBal, toActNum);
            stmt.executeUpdate(updateFrom);
            stmt.executeUpdate(updateTo);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recordTransfers(TransferOps op) {
        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
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
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TransferOps> buildTransArray() {
        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TRANSFERS;");
            ArrayList<TransferOps> transferArray = new ArrayList<>();
            while (rs.next()) {
                TransferOps currOp = new TransferOps();
                currOp.setToActNum(rs.getLong("TOACTNUM"));
                currOp.setFromActNum(rs.getLong("FROMACTNUM"));
                currOp.setAmt(rs.getDouble("AMOUNT"));
                String[] tagsArray = (String[]) rs.getArray("TAGS").getArray();
                currOp.setTagArray(new ArrayList<String>(Arrays.asList(tagsArray)));
                transferArray.add(currOp);
            }
            return new ArrayList<>(transferArray);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
