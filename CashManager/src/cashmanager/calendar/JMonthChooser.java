

package cashmanager.calendar;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Kiko
 */
public class JMonthChooser extends JPanel implements ItemListener{
    private JComboBox monthBox;
    private Locale locale;
    private int currentMonth;
    private boolean initialized;
    private boolean localeInitialize;

    public JMonthChooser(){
        super();
        setName("JMonthChooser");
        setLayout(new BorderLayout());
        monthBox = new JComboBox();
        monthBox.addItemListener(this);
        locale = Locale.getDefault();
        initMonth();
        add(monthBox, BorderLayout.CENTER);
        initialized = true;
        Calendar c = Calendar.getInstance(locale);
        setMonth(c.get(Calendar.MONTH), true, false);
//        setNewFont(new Font(Font.DIALOG, Font.PLAIN, 10));
    }

    private final void initMonth(){
        localeInitialize = true;
        DateFormatSymbols date = new DateFormatSymbols(locale);
        String[] months = date.getMonths();

        if(monthBox.getItemCount() == 12){
            monthBox.removeAllItems();
        }
        for(int i = 0; i < 12; i++){
            monthBox.addItem(months[i]);
        }
        localeInitialize = false;
        monthBox.setSelectedIndex(currentMonth);
    }

    public void setMonth(int newMonth){
        setMonth(newMonth, true, true);
    }
    private void setMonth(int newMonth, boolean updateSelection, boolean firePropertyEvent){
        if(!initialized || localeInitialize){
            return;
        }

        int oldMonth = currentMonth;

        if(newMonth < 0){
            currentMonth = 0;
        }else if(newMonth > 11){
            currentMonth = 11;
        }else{
            currentMonth = newMonth;
        }

        if(updateSelection){
            monthBox.setSelectedIndex(currentMonth);
        }
        if(firePropertyEvent){
            firePropertyChange("month", oldMonth, currentMonth);
        }
    }
    public int getMonth(){
        return currentMonth;
    }
    public void setLocale(Locale l){
        super.setLocale(l);
        locale = l;
        initMonth();
    }
    public Locale getLocale(){
        return locale;
    }
    public JComboBox getMonthBox(){
        return monthBox;
    }
    public void setNewFont(Font font){
        super.setFont(font);
        monthBox.setFont(font);
    }

    public void itemStateChanged(ItemEvent e){
        if(e.getStateChange() == ItemEvent.SELECTED){
            int index = monthBox.getSelectedIndex();
            if((index >= 0) && (index != currentMonth)){
                setMonth(index, false, true);
            }
        }
    }
    
    public static void main(String args[]){
        JFrame frame = new JFrame("JMonthChooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JMonthChooser mc = new JMonthChooser();
        Locale[] list = Locale.getAvailableLocales();
        String[] a = new String[list.length];
        int i = 0;
        for(Locale l : list){
            a[i] = l.getDisplayName();
            i++;
        }
        Arrays.sort(a);
        final JComboBox b = new JComboBox();
        for(String s : a){
            b.addItem(s);
        }
        b.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e){
                if(e.getStateChange() == ItemEvent.SELECTED){
                    String sel = (String)b.getSelectedItem();
                    Locale[] list = Locale.getAvailableLocales();
                    for(Locale l : list){
                        if(l.getDisplayName().equals(sel)){
                            mc.setLocale(l);
                            break;
                        }
                    }
                }
            }
        });
        frame.add(mc, BorderLayout.CENTER);
        frame.add(b, BorderLayout.PAGE_END);
        frame.pack();
        frame.setVisible(true);
    }

}//JMonthChooser
