/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Kiko
 */
public class Transaction extends CashManagerDB{

    private long idTrans;
    private String causal;
    private int ammount;
    private Calendar transactionDate;
    private String description;

    private static final String createTab = "create table transactions " +
            "(idtrans bigint not null generated always as identity primary key, " +
            "causal varchar(150) not null , " +
            "ammount int not null, " +
            "transaction_date date not null, " +
            "description varchar(500))";
    private static final String checkTab = "update transactions " +
            "set description='test' " +
            "where 1=2";
    private static final String insertInto = "insert into transactions" +
                    "(causal, ammount, transaction_date, description) " +
                    "values ";

    //Set Methods
    public void setIdTrans(long id){
        idTrans = id;
    }
    public void setCausal(String caus){
        causal = caus;
    }
    public void setAmmount(int amm){
        ammount = amm;
    }
    public void setTransactionDate(Calendar date){
        transactionDate = date;
    }
    public void setDescription(String desc){
        description = desc;
    }
    //Set Methods

    //Get methods
    public long getIdTrans(){
        return idTrans;
    }
    public String getCausal(){
        return causal;
    }
    public int getAmmount(){
        return ammount;
    }
    public Calendar getTransactionDate(){
        return transactionDate;
    }
    public String getDescription(){
        return description;
    }
    //Get Methods

    @Override
    public String toString(){
        return String.format("Transaction [id=%d, causal=%s, ammount=%d, date=%s, description=%s] ",
                getIdTrans(), getCausal(), getAmmount(), "date", getDescription());
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

    public static void insertTransaction(Transaction trans){
        Connection conn = getConnection();
        String ins = insertInto;
        ins += "(?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(ins);
            ps.setString(1, trans.getCausal());
            ps.setInt(2, trans.getAmmount());
            ps.setDate(3, new Date(trans.getTransactionDate().getTimeInMillis()));
            ps.setString(4, trans.getDescription());
            ps.executeUpdate();

            ps.close();
            disconnect(conn);
        } catch (SQLException ex) {
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void insertTransactions(List<Transaction> list){
        String ins = insertInto;
        for(int i = 1; i < list.size(); i++){
            ins += "(?, ?, ?, ?), ";
        }
        ins += "(?, ?, ?, ?)";
        Connection conn = getConnection();
        int index = 1;
        try{
            PreparedStatement ps = conn.prepareStatement(ins);
            for(Transaction trans : list){
                ps.setString(index, trans.getCausal());
                index++;
                ps.setInt(index, trans.getAmmount());
                index++;
                ps.setDate(index, new Date(trans.getTransactionDate().getTimeInMillis()));
                index++;
                ps.setString(index, trans.getDescription());
                index++;
            }
            ps.executeUpdate();

            ps.close();
            disconnect(conn);
            
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }//insertTransactions

    public static List<Transaction> getAllTransaction(){
        Connection conn = getConnection();
        Statement s;
        ResultSet rs;
        ArrayList<Transaction> arr = new ArrayList<Transaction>();
        try{
            s = conn.createStatement();
            rs = s.executeQuery("select * from transactions");
            while(rs.next()){
                Calendar c = Calendar.getInstance();
                Transaction temp = new Transaction();
                temp.setIdTrans(rs.getLong("idtrans"));
                temp.setCausal(rs.getString("causal"));
                temp.setAmmount(rs.getInt("ammount"));
                c.setTimeInMillis(rs.getDate("transaction_date").getTime());
                temp.setTransactionDate(c);
                temp.setDescription(rs.getString("description"));
                arr.add(temp);
            }

            rs.close();
            s.close();
            disconnect(conn);

        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return arr;
    }//getAllTransaction

    public static List<String> getAllCausal(){
        Connection conn = getConnection();
        ArrayList<String> arr = new ArrayList<String>();

        try{
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("select distinct causal from transactions order by causal");
            while(rs.next()){
                arr.add(rs.getString(1));
            }
            rs.close();
            s.close();
            disconnect(conn);
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + Transaction.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return arr;
    }//getAllCausal


    public static void main(String args[]){
        Transaction.createTable(checkTab, createTab);
        System.out.println("Insert a new transaction causal or exit to quit.");
        Scanner s = new Scanner(System.in);
        Transaction t = new Transaction();
        while(!s.nextLine().equals("exit")){
            System.out.print("Insert transaction causal: ");
            t.setCausal(s.nextLine());
            System.out.print("Insert transaction description: ");
            t.setDescription(s.nextLine());
            System.out.print("Insert transaction ammount: ");
            t.setAmmount(Integer.parseInt(s.nextLine()));
            t.setTransactionDate(Calendar.getInstance());

            Transaction.insertTransaction(t);

            System.out.println("Insert a new transaction or exit to quit.");
        }
        System.out.println("Done inserting.");
        Transaction.printTransactionList(Transaction.getAllTransaction());
        Transaction.shutdown();
        System.out.println("Main finisched.");

    }

}//Transaction
