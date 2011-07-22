
package cashmanager.calendar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Kiko
 */
public class JDateChooser extends JPanel implements PropertyChangeListener, ActionListener, FocusListener{

    private JCalendar jCalendar;
    private DateFormat df;
    private JTextField textField;
    private JButton button;
    private JPopupMenu popMenu;
    private Locale locale;
    private Calendar calendar;
    private boolean dateSelected;
    private boolean isInitialized;

    public JDateChooser(){
        super();
        setName("JDateChooser");
        setLayout(new BorderLayout());
        locale = Locale.getDefault();
        calendar = Calendar.getInstance(locale);
        df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        String textDate = df.format(calendar.getTime());
        textField = new JTextField(10);
        textField.setEditable(false);
        textField.setHorizontalAlignment(SwingConstants.TRAILING);
        textField.setText(textDate);
        jCalendar = new JCalendar();
        jCalendar.addPropertyChangeListener(this);
        popMenu = new JPopupMenu() {
            public void setVisible(boolean b) {
                Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
                if (b || (!b && dateSelected) || ((isCanceled != null) && !b && isCanceled.booleanValue())) {
                    super.setVisible(b);
                }
            }
        };
        popMenu.add(jCalendar);
        popMenu.addFocusListener(this);
        button = new JButton("ok");
        button.addActionListener(this);
        button.setFocusable(false);
        Box box = Box.createHorizontalBox();
        box.add(textField);
        box.add(button);

        add(box, BorderLayout.CENTER);

        isInitialized = true;
    }//Constructor

    public Calendar getCalendar(){
        return calendar;
    }
    public long getTimeInMillis(){
        return calendar.getTimeInMillis();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("calendar") && jCalendar.getYearChooser().isCorrect()){
            Date tmpDate = jCalendar.getCalendar().getTime();
            String newDate = df.format(tmpDate);
            calendar.setTime(tmpDate);
            textField.setText(newDate);
            dateSelected = true;
            popMenu.setVisible(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
            dateSelected = false;
            JButton b = (JButton)e.getSource();
            Point p = b.getLocation();
            Dimension d = b.getSize();
            popMenu.show(this, p.x + d.width, p.y);
            popMenu.requestFocus();
    }

    public void focusGained(FocusEvent e) {
        System.out.println("Focus gained");
    }

    public void focusLost(FocusEvent e) {
        dateSelected = false;
        System.out.println("FocusLost end");
    }

    public static void main(String args[]){
        JFrame frame = new JFrame("JDateChooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(400, 400);
        frame.add(new JDateChooser());
        frame.pack();
        frame.setVisible(true);
    }

}//JDateChooser
