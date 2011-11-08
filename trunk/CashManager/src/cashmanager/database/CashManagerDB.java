
package cashmanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Alan Bertoni
 */
public abstract class CashManagerDB {

    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String database = "cashManagerDB";
    private static final String connectionURL = "jdbc:derby:" + database + ";create=true";
    private static final String user = "admin";
    private static final String password = "admin";

    protected static Connection getConnection(){
        Connection c = null;
        try{
            Class.forName(driver);
        }catch(ClassNotFoundException classNotFoundEx){
            System.err.print("ClassNotFoundException ");
            System.err.println(classNotFoundEx.getMessage());
            classNotFoundEx.printStackTrace();
            System.err.println("\n    >>> Please check your CLASSPATH variable   <<<\n");
        }
        try{
            c = DriverManager.getConnection(connectionURL);
            //System.out.println("Connected to database " + database);
        }catch(SQLException ex){
           System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
           System.err.println(ex.getMessage());
           ex.printStackTrace();
        }
        return c;
    }//getConnection
    protected static void disconnect(Connection conn){
        try{
            conn.close();
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }//disconnect
    protected static void shutdown(){
        //   ## DATABASE SHUTDOWN SECTION ##
        /*** In embedded mode, an application should shut down Derby.
        Shutdown throws the XJ015 exception to confirm success. ***/
        if(driver.equals("org.apache.derby.jdbc.EmbeddedDriver")){
            boolean gotSQLExc = false;
            try{
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            }catch (SQLException se){
                if(se.getSQLState().equals("XJ015")){
                    gotSQLExc = true;
                }
            }
            if(!gotSQLExc){
                System.out.println("Database did not shut down normally");
            }else{
                System.out.println("Database shut down normally");
            }
        }
    }//shutdown
    protected static boolean check4Table(Connection conn, String update) throws SQLException {
      Statement s = null;
      try{
         s = conn.createStatement();
         s.execute(update);
      }catch(SQLException sqle){
         String theError = (sqle).getSQLState();
         //   System.out.println("  Utils GOT:  " + theError);
         /** If table exists will get -  WARNING 02000: No row was found **/
         if(theError.equals("42X05")){  // Table does not exist
             return false;
          } else if(theError.equals("42X14") || theError.equals("42821")){
             System.out.println("check4Table: Incorrect table definition.");
             throw sqle;
          } else {
             System.out.println("check4Table: Unhandled SQLException" );
             throw sqle;
          }
      }

      finally{
          if(s != null)
              s.close();
      }
      //  System.out.println("Just got the warning - table exists OK ");
      return true;
   }//check4Table
    protected static void createTable(String checkTab, String createTab){
        Connection conn = getConnection();
        Statement s = null;
        try{
            if(!check4Table(conn, checkTab)){
                s = conn.createStatement();
                s.executeUpdate(createTab);
            }
        }catch(SQLException ex){
            System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        finally{
            try{
                if(s != null){
                    s.close();
                }
                disconnect(conn);
                System.out.println("Disconnected in createTable.");
            }catch(SQLException ex){
                System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }//createTable
    protected static void deleteTable(String checkTab, String deleteTab){
        Connection conn = getConnection();
        Statement s = null;
        try {
            if(check4Table(conn, checkTab)){
                s = conn.createStatement();
                s.executeUpdate(deleteTab);
            }
        } catch (SQLException ex) {
            System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }finally{
            try{
                if(s != null){
                    s.close();
                }
                disconnect(conn);
                System.out.println("Disconnected in deleteTable.");
            }catch(SQLException ex){
                System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }//deleteTable
    protected static void dropTable(String checkTab, String dropTab){
        Connection conn = getConnection();
        Statement s = null;
        try {
            if(check4Table(conn, checkTab)){
                s = conn.createStatement();
                s.executeUpdate(dropTab);
            }
        } catch (SQLException ex) {
            System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }finally{
            try{
                if(s != null){
                    s.close();
                }
                disconnect(conn);
                System.out.println("Disconnected in dropTable.");
            }catch(SQLException ex){
                System.err.println("SQLException thrown in class" + CashManagerDB.class.getName());
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }//dropTable
    
}//CashManagerDB
