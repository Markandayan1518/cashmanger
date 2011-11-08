
package cashmanager.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Kiko
 */
public class Transaction extends CashManagerDB{

    private long idTrans;
    private String causal;
    private double amount;
    private Calendar transactionDate;
    private String description;
    private String type;
    public static final String IN = "in";
    public static final String OUT = "out";
    private static final String createTab = "create table transactions \n\t" +
            "(idtrans bigint not null generated always as identity primary key, \n\t" +
            "causal varchar(150) not null, \n\t" +
            "amount double not null, \n\t" +
            "transaction_date date not null, \n\t" +
            "description varchar(500), \n\t" +
            "type varchar(3) not null)\n";
    private static final String deleteTab = "delete from transactions ";
    private static final String dropTable = "drop table transactions";
    public static final String checkTab = "update transactions " +
            "set description='test' " +
            "where 1=2";
    private static final String insertInto = "insert into transactions \n\t" +
                    "(causal, amount, transaction_date, description, type) " +
                    "values \n\t";
    private static final String insertIntoBackup = "insert into transactions \n\t" +
                    "(idtrans, causal, amount, transaction_date, description, type) \n\t" +
                    "values \n\t";

    public void setIdTrans(long id){
        idTrans = id;
    }
    public long getIdTrans(){
        return idTrans;
    }
    public void setCausal(String caus){
        causal = caus;
    }
    public String getCausal(){
        return causal;
    }
    public void setAmount(double amm){
        amount = amm;
    }
    public double getAmount(){
        return amount;
    }
    public void setTransactionDate(Calendar date){
        transactionDate = date;
    }
    public void setTransactionDate(long timeInMillis){
        Calendar tmp = Calendar.getInstance();
        tmp.setTimeInMillis(timeInMillis);
        tmp.getTimeInMillis();
        setTransactionDate(tmp);
    }
    public Calendar getTransactionDate(){
        return transactionDate;
    }
    public void setDescription(String desc){
        description = desc;
    }
    public String getDescription(){
        return description;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
    @Override
    public String toString(){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String tmpDate = df.format(getTransactionDate().getTime());
        return String.format("Transaction [id=%d, causal=%s, amount=%f, date=%s, description=%s, type=%s] ",
                getIdTrans(), getCausal(), getAmount(), tmpDate, getDescription(), getType());
    }
    public static void printTransactionList(List<Transaction> list){
        System.out.println("\n--------------------------------------------------");
        System.out.println("Printing transaction list...");
        for(Transaction t : list){
            System.out.println(t);
        }
        System.out.println("Finished printing transaction list.");
        System.out.println("--------------------------------------------------");
    }
    public static boolean isTransactionInOrOut(Transaction trans){
        if(trans.getType().equals(Transaction.IN)){
            return true;
        }else{
            return false;
        }
    }
    public static void insertTransaction(Connection conn, Transaction trans){
        PreparedStatement ps = null;
        String ins = insertInto;
        ins += "(?, ?, ?, ?, ?)";
        try{
            ps = conn.prepareStatement(ins);
            ps.setString(1, trans.getCausal());
            ps.setDouble(2, trans.getAmount());
            ps.setDate(3, new Date(trans.getTransactionDate().getTimeInMillis()));
            ps.setString(4, trans.getDescription());
            ps.setString(5, trans.getType());
            ps.executeUpdate();

            DayReport dayRep = new DayReport();
            dayRep.setDay(trans.getTransactionDate().getTimeInMillis());
            if(isTransactionInOrOut(trans)){
                dayRep.setIncome(trans.getAmount());
            }else{
                dayRep.setOutcome(trans.getAmount());
            }
            DayReport.updateOrInsert(conn, dayRep);
        }catch(SQLException ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        finally{
            try{
                if(ps != null){
                    ps.close();
                }
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally


    }//insertTransaction
    public static void insertTransactions(List<Transaction> transactionList){
        if(transactionList.size() > 0){
            Connection conn = getConnection();
            Statement s = null;
            String ins = createInsertIntoString(transactionList, false);
            List<DayReport> reps = createDayReportList(transactionList);
            try{
                s = conn.createStatement();
                s.executeUpdate(ins);
                for(DayReport d : reps){
                    DayReport.updateOrInsert(conn, d);
                }
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
            finally{
                disconnect(conn);
            }//finally
        }
    }
    public static void deleteTransaction(Connection conn, Transaction trans){
        String deleteString = deleteTab;
        deleteString += "where idtrans =" + trans.getIdTrans();
        Transaction t = getTransactionByID(trans.getIdTrans());
        Statement s = null;
        try{
            s = conn.createStatement();
            s.executeUpdate(deleteString);
            DayReport d = new DayReport();
            d.setDay(t.getTransactionDate());
            if(isTransactionInOrOut(t)){
                d.setIncome(-t.getAmount());
            }else{
                d.setOutcome(-t.getAmount());
            }
            DayReport.updateOrInsert(conn, d);
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        finally{
            try {
                s.close();
            } catch (SQLException ex) {
                System.err.println("SQLException thrown in class" + Transaction.class.getName());
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }//deleteTransaction
    public static void deleteTransactions(List<Transaction> transactionList){
        Connection conn = getConnection();
        for(Transaction t : transactionList){
            deleteTransaction(conn, t);
        }
        disconnect(conn);
    }//deleteTransactions
    public static Transaction getTransactionByID(long id){
        Connection conn = getConnection();
        Statement s = null;
        ResultSet r = null;
        Transaction t = new Transaction();
        try{
            s = conn.createStatement();
            r = s.executeQuery("select * from transactions where idtrans = " + id);
            while(r.next()){
                t.setIdTrans(id);
                t.setCausal(r.getString("causal"));
                t.setAmount(r.getDouble("amount"));
                t.setType(r.getString("type"));
                t.setDescription(r.getString("description"));
                t.setTransactionDate(r.getDate("transaction_date").getTime());
            }
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        finally{
            try {
                r.close();
                s.close();
            } catch (SQLException ex) {
                System.err.println("SQLException thrown in class" + Transaction.class.getName());
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        return t;
    }
    private static List<DayReport> createDayReportList(List<Transaction> trans){
        List<DayReport> reps = new ArrayList<DayReport>();
        for(Transaction t : trans){
            DayReport d = new DayReport();
            d.setDay(t.getTransactionDate());
            if(isTransactionInOrOut(t)){
                d.setIncome(t.getAmount());
            }else{
                d.setOutcome(t.getAmount());
            }
            reps.add(d);
        }
        return reps;
    }//createDayReportList
    public static List<Transaction> getAllTransaction(){
        Connection conn = getConnection();
        Statement s = null;
        ResultSet rs = null;
        ArrayList<Transaction> arr = new ArrayList<Transaction>();
        try{
            s = conn.createStatement();
            rs = s.executeQuery("select * from transactions");
            while(rs.next()){
                Calendar c = Calendar.getInstance();
                Transaction temp = new Transaction();
                temp.setIdTrans(rs.getLong("idtrans"));
                temp.setCausal(rs.getString("causal"));
                temp.setAmount(rs.getDouble("amount"));
                c.setTimeInMillis(rs.getDate("transaction_date").getTime());
                temp.setTransactionDate(c);
                temp.setDescription(rs.getString("description"));
                temp.setType(rs.getString("type"));
                arr.add(temp);
            }

        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        finally{
            try{
                if(rs != null){
                    rs.close();
                }
                if(s != null){
                    s.close();
                }
                disconnect(conn);
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally
        return arr;
    }//getAllTransaction
    public static List<String> getAllCausal(){
        Connection conn = getConnection();
        Statement s = null;
        ResultSet rs = null;
        ArrayList<String> arr = new ArrayList<String>();

        try{
            s = conn.createStatement();
            rs = s.executeQuery("select distinct causal from transactions order by causal");
            while(rs.next()){
                arr.add(rs.getString(1));
            }
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        finally{
            try{
                if(rs != null){
                    rs.close();
                }
                if(s != null){
                    s.close();
                }
                disconnect(conn);
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally

        return arr;
    }//getAllCausal
    private static List<CausalAmount> getCausalAmount(Calendar fromDate, Calendar toDate, boolean type){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CausalAmount> list = new ArrayList<CausalAmount>();
        String statement = "select causal, sum(amount) from transactions where "
                + "transaction_date >= ? and transaction_date <= ? and type = ? group by causal";
        String typeInOut = "";
        if(type){
            typeInOut = Transaction.IN;
        }else{
            typeInOut = Transaction.OUT;
        }
        try{
            ps = conn.prepareStatement(statement);
            ps.setDate(1, new Date(fromDate.getTimeInMillis()));
            ps.setDate(2, new Date(toDate.getTimeInMillis()));
            ps.setString(3, typeInOut);
            rs = ps.executeQuery();
            while(rs.next()){
                CausalAmount c = new CausalAmount();
                c.setCausal(rs.getString(1));
                c.setTotalAmount(rs.getDouble(2));
                list.add(c);
            }
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        finally{
            try{
                if(rs != null){
                    rs.close();
                }
                if(ps != null){
                    ps.close();
                }
                disconnect(conn);
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally

        CausalAmount[] t = list.toArray(new CausalAmount[0]);
        Arrays.sort(t);
        list = Arrays.asList(t);
        Collections.reverse(list);
        return list;
    }//getCausalAmount
    public static List<CausalAmount> getCausalTotalIncome(Calendar fromDate, Calendar toDate){
        return getCausalAmount(fromDate, toDate, true);
    }//getCausalTotalIncome
    public static List<CausalAmount> getCausalTotalOutcome(Calendar fromDate, Calendar toDate){
        return getCausalAmount(fromDate, toDate, false);
    }//getCausalTotalOutcome
    private static String createInsertIntoString(List<Transaction> trans, boolean isBackup){
        if(trans.size() > 0){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String ins = isBackup ? insertIntoBackup : insertInto;
            for(int i = 0; i < trans.size(); i++){
                Transaction t = trans.get(i);
                ins += "(";
                ins += isBackup ? "" + t.getIdTrans() + ", " : "";
                ins += "'" + t.getCausal() + "', ";
                ins += t.getAmount() + ", ";
                ins += "DATE('" + df.format(t.getTransactionDate().getTime()) + "'), ";
                ins += "'" + t.getDescription() + "', ";
                ins += "'" + t.getType() + "')";
                if(i < trans.size() - 1){
                    ins += ", ";
                }
            }
            return ins;
        }else{
            return "";
        }
    }
    public static String createBackUpString(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String backUp = createTab;
        List<Transaction> trans = getAllTransaction();
        backUp += createInsertIntoString(trans, false);
        System.out.println(backUp);
        return backUp;
    }//createBackUpString
    public static void displayMenu(){
        Scanner s = new Scanner(System.in);
        while(true){
            System.out.println("Transaction Menu:");
            System.out.println("Press 1 to insert new transactions");
            System.out.println("Press 2 to delete transactions");
            System.out.println("Press 3 to print all transactions");
            System.out.println("Press 4 to delete all transactions");
            System.out.println("Press 5 to drop transaction table");
            System.out.println("Press 6 to create transaction table");
            System.out.println("Press 0 to exit...");
            System.out.print(">");
            int selection = Integer.parseInt(s.next());
            switch(selection){
                case 1:
                    Transaction.insertTransactionsCLI();
                    break;
                case 2:
                    Transaction.deleteTransactionsCLI();
                    break;
                case 3:
                    Transaction.printTransactionList(getAllTransaction());
                    break;
                case 4:
                    Transaction.deleteTransactions(getAllTransaction());
                    break;
                case 5:
                    Transaction.dropTable(checkTab, dropTable);
                    break;
                case 6:
                    Transaction.createTable(checkTab, createTab);
                    break;
                case 0:
                    return;
                default:
                    break;
            }
        }
    }//displayMenu
    public static void deleteTransactionsCLI(){
        Scanner s = new Scanner(System.in);
        List<Transaction> list = new ArrayList<Transaction>();
        System.out.print("Insert a new transaction ID or -1 to exit: ");
        long id = Long.parseLong(s.next());
        if(id != -1){
            Transaction t = new Transaction();
            t.setIdTrans(id);
            list.add(t);
            deleteTransactions(list);
            System.out.println("Deleted transaction with id = " + id);
        }
    }//deleteransactionsCLI
    public static void insertTransactionsCLI(){
        Scanner s = new Scanner(System.in);
        System.out.print("Insert a new transaction causal or exit to exit: ");
        String tmp = s.nextLine();
        List<Transaction> list = new ArrayList<Transaction>();
        while(!tmp.equals("exit")){
            Transaction t = new Transaction();
            t.setCausal(tmp);
            System.out.print("Insert transaction description: ");
            t.setDescription(s.nextLine());
            System.out.print("Insert transaction amount: ");
            t.setAmount(Double.parseDouble(s.nextLine()));
            System.out.print("Insert transaction date(dd-MM-yyyy): ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            try{
                c.setTime(sdf.parse(s.nextLine()));
                c.getTime();
            }catch(ParseException ex){}
            t.setTransactionDate(c);
            System.out.print("Insert transaction type(in or out): ");
            t.setType(s.nextLine());

            list.add(t);

            System.out.print("Insert a new transaction or exit to exit: ");
            tmp = s.nextLine();
        }
        Transaction.insertTransactions(list);
        System.out.println("Done inserting.");
    }//insertTransactionsCLI
    public static void main(String args[]){
        try{
            displayMenu();
//            Transaction.printTransactionList(Transaction.getAllTransaction());
//            Transaction.createBackUpString();
//            System.out.println("Main finisched.");
        }catch(Exception ex){}
        finally{
            Transaction.shutdown();
        }
    }//main

}//Transaction
