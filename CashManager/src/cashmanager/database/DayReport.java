
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Kiko
 */
public class DayReport extends CashManagerDB{

    private Calendar day;
    private double income;
    private double outcome;

    private static final String createTab = "create table dayReport " +
            "(day date not null primary key, " +
            "income double default 0, " +
            "outcome double default 0)";
    private static final String deleteTab = "delete from dayReport";
    private static final String dropTable = "drop table dayReport";
    private static final String checkTab = "select * from dayReport";
    private static final String insertInto = "insert into dayReport " +
                    "(day, income, outcome) values ";
    private static String updateRow = "update dayReport "
            + "set income = income + ?, outcome = outcome + ? where day = ?";

    public DayReport(){
        this(null, 0, 0);
    }
    public DayReport(Calendar day){
        this(day, 0, 0);
    }
    public DayReport(Calendar day, double income, double outcome){
        setDay(day);
        setIncome(income);
        setOutcome(outcome);
    }

    public void setDay(Calendar day){
        this.day = day;
    }
    public void setDay(long timeInMillis){
        day = Calendar.getInstance();
        day.setTimeInMillis(timeInMillis);
    }
    public Calendar getDay(){
        return day;
    }
    public Date getTime(){
        return day.getTime();
    }
    public long getTimeInMillis(){
        return day.getTimeInMillis();
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
    public int isPositive(){
        return Double.compare(income, outcome);
    }
    @Override
    public String toString(){
        DateFormat df = DateFormat.getDateInstance();
        String tmpDate = df.format(day.getTime());
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
    public static int isDayBetween(Calendar date, List<DayReport> list){
        for(int i = 0; i < list.size(); i++){
            DayReport tmp = list.get(i);
            int compare = tmp.getDay().compareTo(date);
            switch(compare){
                case 0:
                    return i;
                case 1:
                    return -1;
            }
        }
        return -1;
    }
    public static boolean isDayReportIn(Connection conn, DayReport d){
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
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally

        return false;
    }
    public static double maximumValue(List<DayReport> list, boolean type){
        double max = 0;
        for(DayReport tmp : list){
            if(type){
                if(tmp.getIncome() > max){
                    max = tmp.getIncome();
                }
            }else{
                if(tmp.getOutcome() > max){
                    max = tmp.getOutcome();
                }
            }
        }
        return max;
    }
    public static double maximumIncome(List<DayReport> list){
        return maximumValue(list, true);
    }
    public static double maximumOutcome(List<DayReport> list){
        return maximumValue(list, false);
    }
    public static List<DayReport> getAllDayReport(){
        Connection conn = getConnection();
        Statement s = null;
        ResultSet rs = null;
        ArrayList<DayReport> arr = new ArrayList<DayReport>();
        try{
            s = conn.createStatement();
            rs = s.executeQuery("select * from dayReport order by day");
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
    public static List<DayReport> getDayReportBetween(Calendar fromDate, Calendar toDate){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DayReport> arr = new ArrayList<DayReport>();
        try{
            ps = conn.prepareStatement("select * from dayReport where day >= ? and day <= ? order by day");
            ps.setDate(1, new java.sql.Date(fromDate.getTimeInMillis()));
            ps.setDate(2, new java.sql.Date(toDate.getTimeInMillis()));
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
    public static IncomeOutcome getTotalIncomeOutcome(Calendar fromDate, Calendar toDate){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        IncomeOutcome tmp = new IncomeOutcome();
        try{
            ps = conn.prepareStatement("select sum(income), sum(outcome) from dayReport where day >= ? and day <= ?");
            ps.setDate(1, new java.sql.Date(fromDate.getTimeInMillis()));
            ps.setDate(2, new java.sql.Date(toDate.getTimeInMillis()));
            rs = ps.executeQuery();
            while(rs.next()){
                tmp.setIncome(rs.getDouble(1));
                tmp.setOutcome(rs.getDouble(2));
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

        return tmp;
    }//getDayReportBetween
    public static DayBalance getBalanceToDate(Calendar day){
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String statement = "select sum(income), sum(outcome) from dayReport where day < ?";
        DayBalance dayBal = new DayBalance(day);
        try{
            ps = conn.prepareStatement(statement);
            ps.setDate(1, new java.sql.Date(day.getTimeInMillis()));
            rs = ps.executeQuery();
            if(rs.next()){
                double balance = rs.getDouble(1) - rs.getDouble(2);
                dayBal.setBalance(balance);
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

        return dayBal;
    }//getBalanceToDate
    public static List<DayBalance> getDayBalanceBetween(Calendar fromDate, Calendar toDate){
        List<DayBalance> list = new ArrayList<DayBalance>();
        DayBalance balanceBefore = DayReport.getBalanceToDate(fromDate);
        List<DayReport> arr = DayReport.getDayReportBetween(fromDate, toDate);

        double tmpBalance = balanceBefore.getBalance();
        for(DayReport d : arr){
            double dayBal = d.getIncome() - d.getOutcome();
            tmpBalance += dayBal;
            DayBalance b = new DayBalance(d.getDay(), tmpBalance);
            list.add(b);
        }
        return list;
    }//getDayBalanceBetween
    public static void insertDayReport(Connection conn, DayReport dayRep){
        PreparedStatement ps = null;
        String ins = insertInto;
        ins += "(?, ?, ?)";
        try {
            ps = conn.prepareStatement(ins);
            ps.setDate(1, new java.sql.Date(dayRep.getTimeInMillis()));
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
                ps.setDate(index, new java.sql.Date(dayRep.getTimeInMillis()));
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
    public static void updateDayReport(Connection conn, DayReport dayRep){
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(updateRow);
            ps.setDouble(1, dayRep.getIncome());
            ps.setDouble(2, dayRep.getOutcome());
            ps.setDate(3, new java.sql.Date(dayRep.getTimeInMillis()));
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
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }//finally
    }//updateDayReport
    public static void updateOrInsert(Connection conn, DayReport dr){
        if(isDayReportIn(conn, dr)){
            updateDayReport(conn, dr);
        }else{
            insertDayReport(conn, dr);
        }
    }//updateOrInsert
    public static String createBackUpString(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<DayReport> reps = getAllDayReport();
        String backUp = createTab + "\n" + insertInto;
        for(int i = 0; i < reps.size(); i++){
            DayReport d = reps.get(i);
            backUp += "(";
            backUp += "DATE('" + df.format(d.getTime()) + "'), ";
            backUp += d.getIncome() + ", ";
            backUp += d.getOutcome() + ")";
            if(i < reps.size() - 1){
                backUp += ", ";
            }
        }
        System.out.println(backUp);
        return backUp;
    }//createBackUpString
    public static void main(String args[]){
        Scanner scan = new Scanner(System.in);
        System.out.print("Delete table? y/n ");
        if(scan.nextLine().equals("y")){
            DayReport.deleteTable(checkTab, deleteTab);
        }
//        DayReport.dropTable(checkTab, dropTable);
        DayReport.createTable(checkTab, createTab);
        Connection conn = getConnection();
        try{
            conn.setAutoCommit(false);
            System.out.println("Enter 1 to continue or exit to exit.");
            String s = scan.nextLine();
            while(!s.equals("exit")){
                DayReport dr = new DayReport();
                System.out.print("Insert the date(dd-MM-yyyy): ");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Calendar day = Calendar.getInstance();
                try{
                    day.setTime(sdf.parse(scan.nextLine()));
                }catch(ParseException ex){}
                dr.setDay(day);
                System.out.print("Insert the income: ");
                dr.setIncome(Double.parseDouble(scan.nextLine()));
                System.out.print("Insert the outcome: ");
                dr.setOutcome(Double.parseDouble(scan.nextLine()));
                DayReport.insertDayReport(conn, dr);
                System.out.println("Enter 1 to continue or exit to exit.");
                s = scan.nextLine();
            }
            conn.commit();
        }catch(SQLException ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        DayReport.printDayReportList(DayReport.getAllDayReport());
        DayReport.shutdown();
    }//main

}//DayReport
