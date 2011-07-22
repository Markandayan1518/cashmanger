

package cashmanager.calendar;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Kiko
 */
public class JCalendar extends JPanel implements ItemListener, ChangeListener, PropertyChangeListener{

    private JDayChooser dayChooser;
    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private Calendar calendar;

    public JCalendar(){
        super();
        setName("JCalendar");
        setLayout(new BorderLayout());
        calendar = Calendar.getInstance();
        dayChooser = new JDayChooser();
        dayChooser.addPropertyChangeListener(this);
        monthChooser = new JMonthChooser();
        monthChooser.addPropertyChangeListener(this);
        yearChooser = new JYearChooser(0, 9999);
        yearChooser.addPropertyChangeListener(this);
        Box monthYearBox = Box.createHorizontalBox();
        monthYearBox.add(monthChooser);
        monthYearBox.add(yearChooser);
        add(monthYearBox, BorderLayout.PAGE_START);
        add(dayChooser, BorderLayout.CENTER);
        setNewFont(new Font(Font.DIALOG, Font.PLAIN, 10));
    }

    public int getYear(){
        return calendar.get(Calendar.YEAR);
    }
    public int getMonth(){
        return calendar.get(Calendar.MONTH);
    }
    public int getDay(){
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    public Calendar getCalendar(){
        return calendar;
    }
    public JDayChooser getDayChooser(){
        return dayChooser;
    }
    public JMonthChooser getMonthChooser(){
        return monthChooser;
    }
    public JYearChooser getYearChooser(){
        return yearChooser;
    }
    public void setNewFont(Font font){
        super.setFont(font);
        yearChooser.setNewFont(font);
        monthChooser.setNewFont(font);
        dayChooser.setNewFont(font);
    }

    public void itemStateChanged(ItemEvent e) {
//        if(e.getStateChange() == ItemEvent.SELECTED){
//            int month = monthChooser.getMonthBox().getSelectedIndex();
//            dayChooser.setMonth(month);
//            dayChooser.setMonth(monthChooser.getMonth());
//        }
    }

    public void stateChanged(ChangeEvent e) {
//        dayChooser.setYear(yearChooser.getCurrentValue());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("year")){
            int newYear = (Integer)evt.getNewValue();
            calendar.set(Calendar.YEAR, newYear);
            dayChooser.setYear(newYear);
            System.out.println("calendar" + newYear);
        }else if(evt.getPropertyName().equals("month")){
            int newMonth = (Integer)evt.getNewValue();
            calendar.set(Calendar.MONTH, newMonth);
            dayChooser.setMonth(newMonth);
        }else if(evt.getPropertyName().equals("day")){
            int newDay = (Integer)evt.getNewValue();
            int oldDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, newDay);
            firePropertyChange("calendar", oldDay, newDay);
        }
    }

    public static void main(String args[]){
        JFrame frame = new JFrame("JCalendar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JCalendar());
        frame.pack();
        frame.setVisible(true);
    }
}//JCalendar
