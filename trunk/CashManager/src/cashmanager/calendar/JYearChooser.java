/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
        setValue(defaultValue, true, true, false);
        textField.addActionListener(this);
        textField.addFocusListener(this);
        textField.addCaretListener(this);
        spinner.setEditor(textField);
        add(spinner, BorderLayout.CENTER);
//        setNewFont(new Font(Font.DIALOG, Font.PLAIN, 10));

    }

    public final void setMinValue(int min){
        if(min > maxValue){
            minValue = maxValue;
        }else{
            minValue = min;
        }
    }
    public int getMinValue(){
        return minValue;
    }
    public final void setMaxValue(int max){
        if(max < minValue){
            maxValue = minValue;
        }else{
            maxValue = max;
        }
    }
    public int getMaxValue(){
        return maxValue;
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
    public int getDefaultValue(){
        return defaultValue;
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
    public int getCurrentValue(){
        return currentValue;
    }
    public void setValue(int value){
        System.out.println("SetValue start");
        setValue(value, true, true, true);
        System.out.println("SetValue end");
    }
    public void setValue(int value, boolean updateTextField, boolean updateSpinner, boolean firePropertyEvent){
        System.out.println("SetValue1 start");
        int oldValue = currentValue;
        setCurrentValue(value);

        if(updateTextField){
            System.out.println("Text start");
            textField.setText(Integer.toString(currentValue));
            textField.setForeground(Color.BLACK);
            System.out.println("Text end");
        }
        if(updateSpinner){
            System.out.println("Spinner start");
            spinner.setValue(currentValue);
            System.out.println("Spinner end");
        }
        if(firePropertyEvent){
            firePropertyChange("year", oldValue, currentValue);
            System.out.println("fireprop");
        }
        System.out.println((Integer)spinner.getValue());
        System.out.println("SetValue1 end");
    }//SetValue

    public JSpinner getSpinner(){
        return spinner;
    }
    public boolean isCorrect(){
        if(textField.getForeground().equals(Color.RED)){
            return false;
        }else{
            return true;
        }
    }


    public void setNewFont(Font font){
//        super.setFont(font);
        textField.setFont(font);
    }

    public void printInfo(){
        System.out.println("Spinner prefSize: " + spinner.getPreferredSize());
        System.out.println("Spinner minSize: " + spinner.getMinimumSize());
        System.out.println("Spinner maxSize: " + spinner.getMaximumSize());
        System.out.println("JYearChooser prefSize: " + getPreferredSize());
        System.out.println("JYearChooser minSize: " + getMinimumSize());
        System.out.println("JYearChooser maxSize: " + getMaximumSize());
    }

    public void focusGained(FocusEvent e){
    }

    public void focusLost(FocusEvent e){
        actionPerformed(null);
    }

    public void caretUpdate(CaretEvent e){
        System.out.println("CaretUpdate start");
        try{
            int testValue = Integer.parseInt(textField.getText());
            if((testValue >= minValue) && (testValue <= maxValue)){
                textField.setForeground(editColor);
//                setValue(testValue, false, true, false);
            }else{
                textField.setForeground(Color.red);
            }
        }catch(Exception ex){
            if(ex instanceof NumberFormatException){
                textField.setForeground(Color.red);
            }
            System.out.println(ex);
        }
        textField.repaint();
        System.out.println("CaretUpdate end");
    }

    public void actionPerformed(ActionEvent e){
        System.out.println("ActionPerformed start");
        if(textField.getForeground().equals(editColor)){
            setValue(Integer.parseInt(textField.getText()));
        }
        System.out.println("ActionPerformed end");
    }

    public void stateChanged(ChangeEvent e) {
        System.out.println("StateChanged start");
        if(spinnerModel instanceof SpinnerNumberModel){
            int value = spinnerModel.getNumber().intValue();
            setValue(value, true, false, true);
        }
        System.out.println("StateChanged end");
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
