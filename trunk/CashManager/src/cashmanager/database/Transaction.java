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
import java.util.logging.Level;
import java.util.logging.Logger;

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
            "causal varchar(150) not null unique, " +
            "ammount int not null, " +
            "transaction_date date not null, " +
            "description varchar(500))";
    private static final String checkTab = "update transactions " +
            "set description='test' " +
            "where 1=2";
    private static final String insertInto = "insert into transactions" +
                    "(causal, ammount, transaction_date, description) " +
                    "values (?, ?, ?, ?)";

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
        return String.format("Transaction [id=%d, causal=%s, ammount=%d, date=%s, description=%s]\n",
                getIdTrans(), getCausal(), getAmmount(), getTransactionDate(), getDescription());
    }

    public static void printTransactionList(List<Transaction> list){
        System.out.println("Printing transaction list...");
        for(Transaction t : list){
            System.out.println(t);
        }
        System.out.println("Transaction list printed.");
    }

    public static void insertTransaction(Transaction trans){
        Connection conn = getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(insertInto);
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


    public static void main(String args[]){
        Transaction.createTable(checkTab, createTab);
        System.out.println("Insert a new transaction causal or exit to quit.");
        Scanner s = new Scanner(System.in);
        Transaction t = new Transaction();
        while(!s.nextLine().equals("exit")){
            System.out.print("Insert transaction causal: ");
            t.setCausal(s.nextLine());
            System.out.print("Insert transaction ammount: ");
            t.setAmmount(s.nextInt());
            System.out.print("Insert transaction description: ");
            t.setTransactionDate(Calendar.getInstance());
            t.setDescription(s.nextLine());
            
            Transaction.insertTransaction(t);

            //System.out.println("Insert a new transaction or exit to quit.");
        }
        System.out.println("Done inserting.");
        Transaction.printTransactionList(Transaction.getAllTransaction());
        Transaction.shutdown();
        System.out.println("Main finisched.");

    }

}//Transaction
