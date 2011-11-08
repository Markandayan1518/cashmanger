/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author k1k3t0
 */
public class BackupManager extends CashManagerDB{

    private static final String path = "backup.sql";

    public static void createBackUpFile(){
        String backup = DayReport.createBackUpString();
        backup += "\n\n" + Transaction.createBackUpString();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
            out.write(backup);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//createBackUpFile
    public static void restoreBackUpFile(){
        String sqlQuery = "";
        try{
            BufferedReader in = new BufferedReader(new FileReader(path));
            String tmp = "";
            while((tmp = in.readLine()) != null){
                sqlQuery += tmp + "\n";
            }
            System.out.println(sqlQuery);
        }catch(IOException ex){
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection conn = getConnection();
        try{
           if(Transaction.check4Table(conn, Transaction.checkTab)){

           }
        }catch(SQLException ex){
           Logger.getLogger(BackupManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//restoreBackUpFile
}//BackupManager
