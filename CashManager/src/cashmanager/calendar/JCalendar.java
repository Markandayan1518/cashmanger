/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.calendar;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    public JCalendar(){
        super();
        setLayout(new BorderLayout());
        dayChooser = new JDayChooser();
        monthChooser = new JMonthChooser();
        monthChooser.getMonthBox().addItemListener(this);
        monthChooser.addPropertyChangeListener(this);
        yearChooser = new JYearChooser(0, 9999);
        yearChooser.getSpinner().addChangeListener(this);
        Box monthYearBox = Box.createHorizontalBox();
        monthYearBox.add(monthChooser);
        monthYearBox.add(yearChooser);
        add(monthYearBox, BorderLayout.PAGE_START);
        add(dayChooser, BorderLayout.CENTER);
    }

    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED){
            int month = monthChooser.getMonthBox().getSelectedIndex();
            dayChooser.setMonth(month);
//            dayChooser.setMonth(monthChooser.getMonth());
        }
    }

    public void stateChanged(ChangeEvent e) {
        dayChooser.setYear(yearChooser.getCurrentValue());
    }

    public void propertyChange(PropertyChangeEvent evt) {

    }

    public static void main(String args[]){
        JFrame frame = new JFrame("JCalendar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JCalendar());
        frame.pack();
        frame.setVisible(true);
    }

}//JCalendar
