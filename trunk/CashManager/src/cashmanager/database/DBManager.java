/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.database;

import java.util.Scanner;

/**
 *
 * @author k1k3t0
 */
public class DBManager {

    public static void displayMenu(){
        Scanner s = new Scanner(System.in);
        while(true){
            System.out.println("DBManager Menu:");
            System.out.println("Press 1 to enter Transaction Menu");
            System.out.println("Press 2 to enter DayReport Menu");
            System.out.println("Press 3 to create a Backup file");
            System.out.println("Press 0 to exit");
            System.out.print(">");
            int selection = Integer.parseInt(s.next());
            switch(selection){
                case 1:
                    Transaction.displayMenu();
                    break;
                case 2:
                    DayReport.displayMenu();
                    break;
                case 3:
                    BackupManager.createBackUpFile();
                    break;
                case 0:
                    return;
                default:
                    break;
            }
        }
    }//displayMenu
    public static void main(String args[]){
        displayMenu();
    }//main
}//DBManager
