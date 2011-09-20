
package cashmanager.database;

import cashmanager.toolkit.DateToolkit;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Kiko
 */
public class DayBalance {

    private Calendar date;
    private double balance;

    public DayBalance(){}
    public DayBalance(Calendar day){
        this(day, 0);
    }
    public DayBalance(Calendar day, double balance){
        setDate(day);
        setBalance(balance);
    }
    public Calendar getDate(){
        return date;
    }
    public long getTimeInMillis(){
        return date.getTimeInMillis();
    }
    public Date getTime(){
        return date.getTime();
    }
    public void setDate(Calendar day){
        DateToolkit.resetTime(day);
        date = day;
    }
    public double getBalance(){
        return balance;
    }
    public void setBalance(double balance){
        this.balance = balance;
    }
    public static double getMax(List<DayBalance> list){
        double max = Double.NEGATIVE_INFINITY;
        for(DayBalance d : list){
            if(d.getBalance() > max){
                max = d.getBalance();
            }
        }
        return max;
    }
    public static double getMin(List<DayBalance> list){
        double min = Double.POSITIVE_INFINITY;
        for(DayBalance d : list){
            if(d.getBalance() < min){
                min = d.getBalance();
            }
        }
        return min;
    }
    @Override
    public String toString(){
        DateFormat df = DateFormat.getDateInstance();
        String tmpDate = df.format(date.getTime());
        return String.format("DayBalance [date=%s, balance=%.2f]", tmpDate, getBalance());
    }
    public static void printDayReportList(List<DayBalance> list){
        System.out.println("\n--------------------------------------------------");
        System.out.println("Printing DayBalance list...");
        for(DayBalance d : list){
            System.out.println(d);
        }
        System.out.println("Finished printing DayBalance list.");
        System.out.println("--------------------------------------------------");
    }
    public static int isDayBetween(Calendar date, List<DayBalance> list){
        for(int i = 0; i < list.size(); i++){
            DayBalance tmp = list.get(i);
            int compare = tmp.getDate().compareTo(date);
            switch(compare){
                case 0:
                    return i;
                case 1:
                    return -1;
            }
        }
        return -1;
    }
}//DayBalance
