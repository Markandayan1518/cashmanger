
package cashmanager.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kiko
 */
public class Account extends CashManagerDB{

    private int id;
    private String password;

    private static final String createTab = "create table account " +
            "(id int not null generated always as identity primary key, " +
            "password varchar(50) not null)";
    private static final String deleteTab = "delete from account";
    private static final String dropTable = "drop table account";
    private static final String checkTab = "select * from account";
    private static final String insertInto = "insert into account" +
                    "(password) values (?)";
    private static final String updateRow = "update account"
            + "set password = ? where password = ?";

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
    public void setPassword(String pwd){
        password = pwd;
    }
    public String getPassword(){
        return password;
    }
    
    @Override
    public String toString(){
        return String.format("Account [id=%d, password=%s]", getId(), getPassword());
    }

    public static void insertAccount(Account a){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try{
           ps = conn.prepareStatement(insertInto);
           ps.setString(1, a.getPassword());
           ps.executeUpdate();
        }catch(SQLException ex){
           System.err.println(ex.getMessage());
           ex.printStackTrace();
        }finally{
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if(conn != null){
                disconnect(conn);
            }
        }//finally
    }//insertAccount

    public static List<Account> getAllAccount(){
        Connection conn = getConnection();
        Statement s = null;
        ResultSet rs = null;
        ArrayList<Account> list = new ArrayList<Account>();
        try{
            s = conn.createStatement();
            rs = s.executeQuery(checkTab);
            while(rs.next()){
                Account tmp = new Account();
                tmp.setId(rs.getInt("id"));
                tmp.setPassword(rs.getString("password"));
                list.add(tmp);
            }
        }catch(SQLException ex){
           System.err.println(ex.getMessage());
           ex.printStackTrace();
        }finally{
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if(s != null){
                try {
                    s.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if(conn != null){
                disconnect(conn);
            }
        }//finally

        return list;
    }//getAllAccount

}//Account
