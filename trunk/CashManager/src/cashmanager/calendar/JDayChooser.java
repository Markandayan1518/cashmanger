/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Kiko
 */
public class JDayChooser extends JPanel implements ActionListener{

    private JPanel dayPanel;
    private JPanel weekPanel;
    private JButton[] dayButtons;
    private JButton[] weekNumbers;
    private boolean weekVisible;
    private Locale locale;
    private Calendar today;
    private Calendar calendar;
    private JButton selectedDay;
    private int day;
    private int month;
    private int year;
    private int numberOfWeeks;

    public JDayChooser(){
        this(true);
    }
    public JDayChooser(boolean weekVisible){
        super();
        setName("JDayChooser");
        setLayout(new BorderLayout());
        locale = Locale.getDefault();
        today = Calendar.getInstance(locale);
        calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        this.weekVisible = weekVisible;
        dayButtons = new JButton[49];
        dayPanel = new JPanel();
        dayPanel.setLayout(new GridLayout(7, 7));
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                int index = (i * 7) + j;
                if(i == 0){
                    dayButtons[index] = createDecorationButton();
                }else{
                    dayButtons[index] = new JButton(Integer.toString(index));
                    dayButtons[index].addActionListener(this);
                }
                dayButtons[index].setFocusPainted(false);
                dayPanel.add(dayButtons[index]);
            }
        }
        weekNumbers = new JButton[7];
        weekPanel = new JPanel();
        weekPanel.setLayout(new GridLayout(7, 1));
        for(int i = 0; i < 7; i++){
            weekNumbers[i] = createDecorationButton();
            weekPanel.add(weekNumbers[i]);
        }
        init();

        add(dayPanel, BorderLayout.CENTER);
        add(weekPanel, BorderLayout.LINE_START);

    }
    private JButton createDecorationButton(){
        JButton button = new JButton();
        button.setFocusPainted(false);
        button.setBackground(Color.CYAN);
        button.setBorderPainted(false);
        MouseListener[] mList = button.getMouseListeners();
        for(MouseListener m : mList){
            button.removeMouseListener(m);
        }
        MouseMotionListener[] mmList = button.getMouseMotionListeners();
        for(MouseMotionListener m : mmList){
            button.removeMouseMotionListener(m);
        }
        return button;
    }

    public void init(){
        initDayPanel();
        initWeekPanel();
//        setNewFont(new Font(Font.DIALOG, Font.PLAIN, 9));
    }

    public void setNumberOfWeeks(){
        Calendar tmp = (Calendar)calendar.clone();
        tmp.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tmp.getFirstDayOfWeek();
        int dayOfWeek = tmp.get(Calendar.DAY_OF_WEEK);
        int firstDay = dayOfWeek - firstDayOfWeek;
        if(firstDay < 0){
            firstDay += 7;
        }
        int daysInFirstWeek = 7 - firstDay;
        tmp.add(Calendar.MONTH, 1);
        tmp.add(Calendar.DAY_OF_MONTH, -1);
        int daysInMonth = tmp.get(Calendar.DAY_OF_MONTH);
        int remDays = daysInMonth - daysInFirstWeek;

        numberOfWeeks = (remDays / 7) + 1;
        if((remDays % 7) != 0){
            numberOfWeeks++;
        }
    }//setNumberOfWeeks

    public void initDayPanel(){
        initDayNames();
        initDayNumbers();
    }//initDayPanel

    public void initDayNames(){
        int firstDayOfWeek = Calendar.getInstance(locale).getFirstDayOfWeek();
        int dayOfWeek = firstDayOfWeek;
        DateFormatSymbols df = new DateFormatSymbols(locale);
        String[] dayNames = df.getShortWeekdays();
        for(int i = 0; i < 7; i++){
            dayButtons[i].setText(dayNames[dayOfWeek]);
            if(dayOfWeek < 7){
                dayOfWeek++;
            }else{
                dayOfWeek = 1;
            }
        }
    }//initDayNames

    public void initDayNumbers(){
        Calendar tmp = (Calendar)calendar.clone();
        tmp.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tmp.getFirstDayOfWeek();
        int dayOfWeek = tmp.get(Calendar.DAY_OF_WEEK);
        int firstDay = dayOfWeek - firstDayOfWeek;
        if(firstDay < 0){
            firstDay += 7;
        }
        int i = 7;
        int j;
        for(j = 0; j < firstDay; j++){
            dayButtons[i].setText("");
            dayButtons[i].setVisible(false);
            i++;
        }
        tmp.add(Calendar.MONTH, 1);
        tmp.add(Calendar.DAY_OF_MONTH, -1);
        int daysInMonth = tmp.get(Calendar.DAY_OF_MONTH);
        for(j = 1; j <= daysInMonth; j++){
            dayButtons[i].setText(Integer.toString(j));
            dayButtons[i].setVisible(true);
            i++;
        }
        for(; i < dayButtons.length; i++){
            dayButtons[i].setText("");
            dayButtons[i].setVisible(false);
        }
    }

    public void initWeekPanel(){
        setNumberOfWeeks();
        Calendar tmp = (Calendar)calendar.clone();
        tmp.set(Calendar.DAY_OF_MONTH, 1);
        weekNumbers[0].setText("");
        weekNumbers[0].setVisible(true);
        int i;
        for(i = 1; i < weekNumbers.length; i++){
            if(i <= numberOfWeeks){
                int week = tmp.get(Calendar.WEEK_OF_YEAR);
                weekNumbers[i].setText(Integer.toString(week));
                weekNumbers[i].setForeground(Color.BLUE);
                weekNumbers[i].setVisible(true);
                tmp.add(Calendar.DAY_OF_MONTH, 7);
            }else{
                weekNumbers[i].setText("");
                weekNumbers[i].setVisible(false);
            }
        }
        if(weekVisible){
            add(weekPanel, BorderLayout.LINE_START);
        }
    }//initWeekPanel

    public void updatePane(){
        initDayNumbers();
        initWeekPanel();
    }

    public void setDay(int newDay){
        setDay(newDay, true);
    }
    public void setDay(int newDay, boolean firePropertyEvent){
        int oldDay = day;
        day = newDay;
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.getTime();

        if(firePropertyEvent){
            firePropertyChange("day", oldDay, day);
        }
    }
    public void setMonth(int newMonth){
        int oldMonth = month;
        month = newMonth;
        calendar.set(Calendar.MONTH, month);
        calendar.getTime();
        updatePane();
    }
    public void setYear(int newYear){
        int oldYear = year;
        year = newYear;
        calendar.set(Calendar.YEAR, year);
        calendar.getTime();
        updatePane();
    }
    public void setNewFont(Font font){
        super.setFont(font);
        for(JButton b : dayButtons){
            b.setFont(font);
        }
        for(JButton b : weekNumbers){
            b.setFont(font);
        }
    }

    public void actionPerformed(ActionEvent e){
        selectedDay = (JButton)e.getSource();
        int day = Integer.parseInt(selectedDay.getText());
        setDay(day);
        System.out.println(day);
    }

    public static void main(String args[]){
        JFrame frame = new JFrame("JDayChooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JDayChooser());
        frame.pack();
        frame.setVisible(true);
    }
}//JDayChooser
