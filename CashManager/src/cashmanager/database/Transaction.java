
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
    private double amount;
    private Calendar transactionDate;
    private String description;
    private String type;

    private static final String createTab = "create table transactions " +
            "(idtrans bigint not null generated always as identity primary key, " +
            "causal varchar(150) not null , " +
            "amount double not null, " +
            "transaction_date date not null, " +
            "description varchar(500), " +
            "type varchar(3) not null)";
    private static final String deleteTab = "delete from transactions";
    private static final String dropTable = "drop table transactions";
    private static final String checkTab = "update transactions " +
            "set description='test' " +
            "where 1=2";
    private static final String insertInto = "insert into transactions" +
                    "(causal, amount, transaction_date, description, type) " +
                    "values ";

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
        DateFormat df = DateFormat.getDateInstance();
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

    public static void insertTransaction(Transaction trans){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        String ins = insertInto;
        ins += "(?, ?, ?, ?, ?)";
        try {
            ps = conn.prepareStatement(ins);
            ps.setString(1, trans.getCausal());
            ps.setDouble(2, trans.getAmount());
            ps.setDate(3, new Date(trans.getTransactionDate().getTimeInMillis()));
            ps.setString(4, trans.getDescription());
            ps.setString(5, trans.getType());
            ps.executeUpdate();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        finally{
            try{
                if(ps != null){
                    ps.close();
                }
                disconnect(conn);
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally

    }//insertTransaction
    public static void insertTransactions(List<Transaction> list){
        String ins = insertInto;
        for(int i = 1; i < list.size(); i++){
            ins += "(?, ?, ?, ?, ?), ";
        }
        ins += "(?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        PreparedStatement ps = null;
        int index = 1;
        try{
            ps = conn.prepareStatement(ins);
            for(Transaction trans : list){
                ps.setString(index, trans.getCausal());
                index++;
                ps.setDouble(index, trans.getAmount());
                index++;
                ps.setDate(index, new Date(trans.getTransactionDate().getTimeInMillis()));
                index++;
                ps.setString(index, trans.getDescription());
                index++;
                ps.setString(index, trans.getType());
                index++;
            }
            ps.executeUpdate();
            
        }catch(SQLException ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        finally{
            try{
                if(ps != null){
                    ps.close();
                }
                disconnect(conn);
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally

    }//insertTransactions

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
            rs.close();
            s.close();
            disconnect(conn);
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

    public static void main(String args[]){

//        Transaction.deleteTable(checkTab, deleteTab);
//        Transaction.dropTable(checkTab, dropTable);
        Transaction.createTable(checkTab, createTab);
        System.out.print("Insert a new transaction causal or exit to quit: ");
        Scanner s = new Scanner(System.in);
        String tmp = s.nextLine();
        Transaction t = new Transaction();
        while(!tmp.equals("exit")){
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

            Transaction.insertTransaction(t);

            System.out.print("Insert a new transaction or exit to quit: ");
            tmp = s.nextLine();
        }
        System.out.println("Done inserting.");
        Transaction.printTransactionList(Transaction.getAllTransaction());
        Transaction.shutdown();
        System.out.println("Main finisched.");

    }

}//Transaction
