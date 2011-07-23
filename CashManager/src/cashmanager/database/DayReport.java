

package cashmanager.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Kiko
 */
public class DayReport extends CashManagerDB{

    private Date day;
    private double income;
    private double outcome;

    private static final String createTab = "create table dayReport " +
            "(day date not null primary key, " +
            "income double default 0, " +
            "outcome double default 0)";
    private static final String deleteTab = "delete from dayReport";
    private static final String dropTable = "drop table dayReport";
    private static final String checkTab = "select * from dayReport";
    private static final String insertInto = "insert into dayReport" +
                    "(day, income, outcome) values ";
    private static final String updateRow = "update dayReport"
            + "set income = income + ? , outcome = outcome + ? where day = ?";

    public void setDay(Date day){
        this.day = day;
    }
    public void setDay(long timeInMillis){
        day = new Date();
        day.setTime(timeInMillis);
    }
    public Date getDay(){
        return day;
    }
    public long getTimeInMillis(){
        return day.getTime();
    }
    public void setIncome(double in){
        income = in;
    }
    public double getIncome(){
        return income;
    }
    public void setOutcome(double out){
        outcome = out;
    }
    public double getOutcome(){
        return outcome;
    }

    @Override
    public String toString(){
        DateFormat df = DateFormat.getDateInstance();
        String tmpDate = df.format(day);
        return String.format("DayReport [date=%s, income=%.2f, outcome=%.2f]", tmpDate, getIncome(), getOutcome());
    }
    public static void printDayReportList(List<DayReport> list){
        System.out.println("\n--------------------------------------------------");
        System.out.println("Printing DayReport list...");
        for(DayReport d : list){
            System.out.println(d);
        }
        System.out.println("Finished printing DayReport list.");
        System.out.println("--------------------------------------------------");
    }
    public static boolean isDayReportIn(DayReport d){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement("select * from dayReport where day = ?");
            ps.setDate(1, new java.sql.Date(d.getTimeInMillis()));
            rs = ps.executeQuery();
            return rs.next();
        }catch(SQLException ex){
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

        return false;
    }

    public static List<DayReport> getAllDayReport(){
        Connection conn = getConnection();
        Statement s = null;
        ResultSet rs = null;
        ArrayList<DayReport> arr = new ArrayList<DayReport>();
        try{
            s = conn.createStatement();
            rs = s.executeQuery("select * from dayReport");
            while(rs.next()){
                DayReport dr = new DayReport();
                dr.setDay(rs.getDate("day").getTime());
                dr.setIncome(rs.getDouble("income"));
                dr.setOutcome(rs.getDouble("outcome"));
                arr.add(dr);
            }
        }catch(SQLException ex){
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
    }//getAllDayReport
    public static List<DayReport> getDayReportBetween(DayReport d1, DayReport d2){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<DayReport> arr = new ArrayList<DayReport>();
        try{
            ps = conn.prepareStatement("select * from dayReport where day >= ? and day <= ?");
            ps.setDate(1, new java.sql.Date(d1.getTimeInMillis()));
            ps.setDate(2, new java.sql.Date(d2.getTimeInMillis()));
            rs = ps.executeQuery();
            while(rs.next()){
                DayReport dr = new DayReport();
                dr.setDay(rs.getDate("day").getTime());
                dr.setIncome(rs.getDouble("income"));
                dr.setOutcome(rs.getDouble("outcome"));
                arr.add(dr);
            }
        }catch(SQLException ex){
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

        return arr;
    }//getDayReportBetween

    public static void insertDayReport(DayReport dayRep){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        String ins = insertInto;
        ins += "(?, ?, ?)";
        try {
            ps = conn.prepareStatement(ins);
            ps.setDate(1, new java.sql.Date(dayRep.getDay().getTime()));
            ps.setDouble(2, dayRep.getIncome());
            ps.setDouble(3, dayRep.getOutcome());
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

    }//insertDayReport
    public static void insertDayReports(List<DayReport> list){
        String ins = insertInto;
        for(int i = 1; i < list.size(); i++){
            ins += "(?, ?, ?), ";
        }
        ins += "(?, ?, ?, ?)";
        Connection conn = getConnection();
        PreparedStatement ps = null;
        int index = 1;
        try{
            ps = conn.prepareStatement(ins);
            for(DayReport dayRep : list){
                ps.setDate(index, new java.sql.Date(dayRep.getDay().getTime()));
                index++;
                ps.setDouble(index, dayRep.getIncome());
                index++;
                ps.setDouble(index, dayRep.getOutcome());
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

    }//insertDayReports

    public static void updateDayReport(DayReport dayRep){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(updateRow);
            ps.setDouble(1, dayRep.getIncome());
            ps.setDouble(2, dayRep.getOutcome());
            ps.setDate(3, new java.sql.Date(dayRep.getDay().getTime()));
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
    }//updateDayReport
    public static void updateOrInsert(DayReport dr){
        if(isDayReportIn(dr)){
            updateDayReport(dr);
        }else{
            insertDayReport(dr);
        }
    }//updateOrInsert

    public static void main(String args[]){
//        DayReport.deleteTable(checkTab, deleteTab);
//        DayReport.dropTable(checkTab, dropTable);
        DayReport.createTable(checkTab, createTab);
        DayReport dr = new DayReport();
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter 1 to continue or exit to quit.");
        String s = scan.nextLine();
        while(!s.equals("exit")){
            System.out.print("Insert the date(dd-MM-yyyy): ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date day = new Date();
            try {
                day = sdf.parse(scan.nextLine());
            } catch (ParseException ex) {
            }
            dr.setDay(day);
            System.out.print("Insert the income: ");
            dr.setIncome(Double.parseDouble(scan.nextLine()));
            System.out.print("Insert the outcome: ");
            dr.setOutcome(Double.parseDouble(scan.nextLine()));
            DayReport.insertDayReport(dr);
            System.out.println("Enter 1 to continue or exit to quit.");
            s = scan.nextLine();
        }
        DayReport.printDayReportList(DayReport.getAllDayReport());
        DayReport.shutdown();
    }

}//DayReport