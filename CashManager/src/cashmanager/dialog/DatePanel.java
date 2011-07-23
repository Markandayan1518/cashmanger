/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.dialog;

import cashmanager.calendar.JDateChooser;
import cashmanager.database.DayReport;
import cashmanager.database.Transaction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author Admin
 */
public class DatePanel extends JPanel{
    private JDialog dialog;
    private boolean type;

    private JPanel datePanel;
    private JLabel dateLabel;
    private JDateChooser dateField;
    private JButton addButton;
    private JButton delButton;

    private JScrollPane fieldScroll;
    private JPanel fieldPanel;
    private List<RecordPanel> fieldList;

    private JPanel commandPanel;
    private JButton okButton;
    private JButton cancelButton;

    public DatePanel(){
        this(new JDialog(), true);
    }
    public DatePanel(JDialog dialog, boolean type){
        this.dialog = dialog;
        this.type = type;

        initDatePanel();
        initFieldScroll();
        initCommandPanel();

        setLayout(new BorderLayout());
        add(datePanel, BorderLayout.PAGE_START);
        add(fieldScroll, BorderLayout.CENTER);
        add(commandPanel, BorderLayout.PAGE_END);
    }
    private final void initDatePanel(){
        datePanel = new JPanel();
        dateLabel = new JLabel("Transaction date: ");
        dateField = new JDateChooser();
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                fieldList.add(new RecordPanel());
                updateFieldPanel();
            }
        });
        delButton = new JButton("Delete");
        delButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                if(fieldList.size() < 2){
                    return;
                }else{
                    int index = fieldList.size() - 1;
                    fieldList.remove(index);
                    updateFieldPanel();
                }
            }
        });

        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.LINE_AXIS));
        datePanel.add(dateLabel);
        datePanel.add(dateField);
        datePanel.add(Box.createRigidArea(new Dimension(50, 0)));
        datePanel.add(addButton);
        datePanel.add(Box.createRigidArea(new Dimension(50, 0)));
        datePanel.add(delButton);
    }//initDatePanel
    private final void initFieldScroll(){
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
        fieldScroll = new JScrollPane(fieldPanel);
        fieldList = new ArrayList<RecordPanel>();
        fieldList.add(new RecordPanel());

        for(RecordPanel rp : fieldList){
            fieldPanel.add(rp);
        }
    }//initFieldScroll
    private final void initCommandPanel(){
        commandPanel = new JPanel();
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.LINE_AXIS));
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                dialog.dispose();
            }
        });

        commandPanel.add(Box.createHorizontalGlue());
        commandPanel.add(okButton);
        commandPanel.add(Box.createHorizontalGlue());
        commandPanel.add(cancelButton);
        commandPanel.add(Box.createHorizontalGlue());
    }

    private void updateFieldPanel(){
        fieldPanel.removeAll();
        for(RecordPanel rp : fieldList){
            fieldPanel.add(rp);
        }
        fieldPanel.validate();
        fieldScroll.validate();
    }

    public static void main(String args[]){
        JDialog dialog = new JDialog();
        DatePanel dp = new DatePanel(dialog, true);
        dialog.add(dp, BorderLayout.CENTER);
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
    }

    class RecordPanel extends JPanel{

        private JLabel causalLabel;
        private JTextField causalField;
        private boolean causalValid;
        private JLabel amountLabel;
        private JTextField amountField;
        private boolean amountValid;
        private JLabel descLabel;
        private JTextArea descArea;
        private boolean descValid;
        private JScrollPane descScroll;
        private int borderWidth = 1;

        private RecordPanel(){
            setBorder(new LineBorder(Color.BLACK, borderWidth));
            causalLabel = new JLabel("Causal: ");
            causalField = new JTextField(10);
            causalField.addCaretListener(new CaretListener(){

                public void caretUpdate(CaretEvent e){
                    if(causalField.getText().length() > 50){
                        causalField.setForeground(Color.RED);
                        causalValid = false;
                    }else{
                        causalField.setForeground(Color.BLACK);
                        causalValid = true;
                    }
                }
            });
            amountLabel = new JLabel("Amount: ");
            amountField = new JFormattedTextField(NumberFormat.getInstance());
            amountField.addCaretListener(new CaretListener() {

                public void caretUpdate(CaretEvent e){
                    try{
                        Double.parseDouble(amountField.getText());
                        amountField.setForeground(Color.BLACK);
                        amountValid = true;
                    }catch(NumberFormatException ex){
                        amountField.setForeground(Color.RED);
                        amountValid = false;
                    }
                }
            });
            descLabel = new JLabel("Description: ");
            descArea = new JTextArea(5,10);
            descArea.setWrapStyleWord(true);
            descArea.setLineWrap(true);
            descArea.addCaretListener(new CaretListener(){

                public void caretUpdate(CaretEvent e){
                    if(descArea.getText().length() > 500){
                        descArea.setForeground(Color.RED);
                        descValid = false;
                    }else{
                        descArea.setForeground(Color.BLACK);
                        descValid = true;
                    }
                }
            });
            descScroll = new JScrollPane(descArea);
            GroupLayout gp = new GroupLayout(this);
            gp.setAutoCreateContainerGaps(true);
            gp.setAutoCreateGaps(true);
            setLayout(gp);
            gp.setHorizontalGroup(gp.createSequentialGroup()
                    .addGroup(gp.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(causalLabel)
                        .addComponent(descLabel))
                    .addGroup(gp.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gp.createSequentialGroup()
                            .addComponent(causalField)
                            .addComponent(amountLabel)
                            .addComponent(amountField))
                        .addComponent(descScroll)));

            gp.setVerticalGroup(gp.createSequentialGroup()
                    .addGroup(gp.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gp.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(causalLabel)
                            .addComponent(causalField))
                        .addGroup(gp.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(amountLabel)
                            .addComponent(amountField)))
                    .addGroup(gp.createParallelGroup(GroupLayout.Alignment.BASELINE)
                         .addComponent(descLabel)
                         .addComponent(descScroll)));
        }
        private boolean isRecordValid(){
            return causalValid && amountValid && descValid;
        }//isRecordValid
        private void writeRecord(){
            if(isRecordValid()){
                Transaction tmp = new Transaction();
                tmp.setTransactionDate(dateField.getTimeInMillis());
                tmp.setCausal(causalField.getText());
                tmp.setAmount(Double.parseDouble(amountField.getText()));
                tmp.setDescription(descArea.getText());

                DayReport dr = new DayReport();
                dr.setDay(dateField.getTimeInMillis());
                if(type){
                    tmp.setType(Transaction.IN);
                    dr.setIncome(Double.parseDouble(amountField.getText()));
                }else{
                    tmp.setType(Transaction.OUT);
                    dr.setOutcome(Double.parseDouble(amountField.getText()));
                }

                Transaction.insertTransaction(tmp);
                DayReport.updateOrInsert(dr);
            }
        }//writeRecord
    }//RecordPanel
}//DatePanel
