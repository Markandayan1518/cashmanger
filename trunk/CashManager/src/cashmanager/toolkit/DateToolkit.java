
package cashmanager.toolkit;

import java.util.Calendar;

/**
 *
 * @author Kiko
 */
public class DateToolkit {

    public static int daysBetween(Calendar from, Calendar to){
        int compare = from.compareTo(to);
        switch(compare){
            case 1:
                return 0;
            default:
                return daysBetweenAux(from, to);
        }
    }
    private static int daysBetweenAux(Calendar from, Calendar to){
        int numberOfDays = 0;
        Calendar fromClone = (Calendar) from.clone();
        if(fromClone.get(Calendar.YEAR) == to.get(Calendar.YEAR)){
            int dayOfYear = fromClone.get(Calendar.DAY_OF_YEAR);
            int dayOfYear2 = to.get(Calendar.DAY_OF_YEAR);
            return numberOfDays = dayOfYear2 - dayOfYear + 1;
        }else{
            int dayOfYear = fromClone.get(Calendar.DAY_OF_YEAR);
            fromClone.set(Calendar.MONTH, Calendar.DECEMBER);
            fromClone.set(Calendar.DAY_OF_MONTH, 31);
            int dayOfYear2 = fromClone.get(Calendar.DAY_OF_YEAR);
            numberOfDays = dayOfYear2 - dayOfYear + 1;
            fromClone.add(Calendar.DAY_OF_MONTH, 1);
            while(fromClone.get(Calendar.YEAR) < to.get(Calendar.YEAR)){
                fromClone.add(Calendar.YEAR, 1);
                numberOfDays += fromClone.get(Calendar.DAY_OF_YEAR);
            }
            numberOfDays += to.get(Calendar.DAY_OF_YEAR);
            return numberOfDays;
        }
    }//daysBetweenAux
    public static void resetTime(Calendar date){
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }
}//DateToolkit
