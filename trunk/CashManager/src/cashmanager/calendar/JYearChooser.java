/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Kiko
 */
public class JYearChooser extends JPanel implements FocusListener, CaretListener, ActionListener, ChangeListener{

    private JSpinner spinner;
    private SpinnerNumberModel spinnerModel;
    private JTextField textField;
    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;
    private int defaultValue;
    private int currentValue;
    private Color editColor = Color.GREEN;

    public JYearChooser(){
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    public JYearChooser(int min, int max){
        super();
        setName("JYearChooser");
        setLayout(new BorderLayout());
        setMinValue(min);
        setMaxValue(max);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        setDefaultValue(year);
        setCurrentValue(defaultValue);
        int step = 1;
        spinnerModel = new SpinnerNumberModel(defaultValue, minValue, maxValue, step);
        spinner = new JSpinner(spinnerModel);
        spinner.addChangeListener(this);

        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.TRAILING);
        setValue(defaultValue);
        textField.addActionListener(this);
        textField.addFocusListener(this);
        textField.addCaretListener(this);
        spinner.setEditor(textField);
        add(spinner, BorderLayout.CENTER);

        printInfo();
    }

    public final void setMinValue(int min){
        if(min > maxValue){
            minValue = maxValue;
        }else{
            minValue = min;
        }
    }
    public final void setMaxValue(int max){
        if(max < minValue){
            maxValue = minValue;
        }else{
            maxValue = max;
        }
    }
    public final void setDefaultValue(int defValue){
        if(defValue < minValue){
            defaultValue = minValue;
        }else if(defValue > maxValue){
            defaultValue = maxValue;
        }else{
            defaultValue = defValue;
        }
    }
    public final void setCurrentValue(int currValue){
        if(currValue < minValue){
            currentValue = minValue;
        }else if(currValue > maxValue){
            currentValue = maxValue;
        }else{
            currentValue = currValue;
        }
    }

    public void setValue(int value){
        setValue(value, true);
        spinner.setValue(value);
        System.out.println((Integer)spinner.getValue());
    }
    public void setValue(int value, boolean updateTextField){
        int oldValue = currentValue;
        setCurrentValue(value);

        if(updateTextField){
            textField.setText(Integer.toString(value));
            textField.setForeground(Color.BLACK);
        }
    }

    public JSpinner getSpinner(){
        return spinner;
    }
    public int getCurrentValue(){
        return currentValue;
    }

    public void printInfo(){
        System.out.println(spinner.getPreferredSize());
        System.out.println(spinner.getMinimumSize());
        System.out.println(spinner.getMaximumSize());
        System.out.println(getPreferredSize());
        System.out.println(getMinimumSize());
        System.out.println(getMaximumSize());
    }

    public void focusGained(FocusEvent e){
    }

    public void focusLost(FocusEvent e){
//        actionPerformed(null);
    }

    public void caretUpdate(CaretEvent e){
        try{
            int testValue = Integer.valueOf(textField.getText()).intValue();
            if((testValue >= minValue) && (testValue <= maxValue)){
                textField.setForeground(editColor);
//                setValue(testValue);
            }else{
                textField.setForeground(Color.red);
            }
        }catch(Exception ex){
            if(ex instanceof NumberFormatException){
                textField.setForeground(Color.red);
            }
            // Ignore all other exceptions, e.g. illegal state exception
        }
        textField.repaint();
    }

    public void actionPerformed(ActionEvent e){
        if(textField.getForeground().equals(editColor)){
            setValue(Integer.parseInt(textField.getText()));
            //Spara un evento per il listener
        }
    }

    public void stateChanged(ChangeEvent e) {
        if(spinnerModel instanceof SpinnerNumberModel){
            int value = spinnerModel.getNumber().intValue();
            setValue(value);
        }
    }

    public static void main(String args[]){
        JFrame frame = new JFrame("JYearChooser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JYearChooser(0, 9999));
        frame.pack();
        frame.setLocation(300, 300);
        frame.setVisible(true);
        System.out.println(frame);
        System.out.println(frame.getPreferredSize());
        System.out.println(frame.getMinimumSize());
        System.out.println(frame.getMaximumSize());
    }


}//JYearChooser
