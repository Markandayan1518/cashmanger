/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.screen;

import cashmanager.calendar.JCalendar;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author Admin
 */
public class SummaryScreen extends JPanel{
    private JCalendar summaryCalendar;

    public SummaryScreen(){
        super();
        setLayout(new BorderLayout());
        summaryCalendar = new JCalendar(false, true);
        add(summaryCalendar, BorderLayout.CENTER);
    }
}//SummaryScreen
