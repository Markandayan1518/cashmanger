
package cashmanager.dialog;

import cashmanager.calendar.JDateChooser;
import cashmanager.database.Transaction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
    private List<Transaction> transactionList;

    private JPanel datePanel;
    private JLabel dateLabel;
    private JDateChooser dateField;
    private JButton addButton;
    private JButton deleteButton;

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
        transactionList = new ArrayList<Transaction>();
        initDatePanel();
        initFieldPanel();
        initCommandPanel();
        setLayout(new BorderLayout());
        add(datePanel, BorderLayout.PAGE_START);
        add(fieldScroll, BorderLayout.CENTER);
        add(commandPanel, BorderLayout.PAGE_END);
    }
    private void initDatePanel(){
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
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener(){

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
        datePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        datePanel.add(deleteButton);
    }//initDatePanel
    private void initFieldPanel(){
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
        fieldScroll = new JScrollPane(fieldPanel);
        fieldList = new ArrayList<RecordPanel>();
        fieldList.add(new RecordPanel());

        for(RecordPanel rp : fieldList){
            fieldPanel.add(rp);
        }
    }//initFieldPanel
    private void initCommandPanel(){
        commandPanel = new JPanel();
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.LINE_AXIS));
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e){
                for(RecordPanel r : fieldList){
                    r.addRecord();
                }
                Transaction.insertTransactions(transactionList);
                dialog.dispose();
            }
        });
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
    }//initCommandPanel
    private void updateFieldPanel(){
        fieldPanel.removeAll();
        for(RecordPanel rp : fieldList){
            fieldPanel.add(rp);
        }
        fieldScroll.validate();
    }//updateFieldPanel
    public static void main(String args[]){
        JDialog dialog = new JDialog();
        DatePanel dp = new DatePanel(dialog, true);
        dialog.add(dp, BorderLayout.CENTER);
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
    }//main

    class RecordPanel extends JPanel{

        private JLabel causalLabel;
        private JComboBox causalField;
        private boolean causalValid;
        private JLabel amountLabel;
        private JTextField amountField;
        private boolean amountValid;
        private JLabel descriptionLabel;
        private JTextArea descriptionArea;
        private boolean descriptionValid;
        private JScrollPane descScroll;
        private static final int BORDER_WIDTH = 1;

        private RecordPanel(){
            setBorder(new LineBorder(Color.BLACK, BORDER_WIDTH));
            causalLabel = new JLabel("Causal: ");
            List<String> causalList = Transaction.getAllCausal();
            causalField = new JComboBox(causalList.toArray());
            causalField.setEditable(true);
            causalValid = true;
            /*
            causalField.addFocusListener(new FocusListener(){

                public void focusLost(FocusEvent e){
                    if(causalField.getText().length() > 50){
                        causalField.setForeground(Color.RED);
                        causalValid = false;
                    }else{
                        causalField.setForeground(Color.BLACK);
                        causalValid = true;
                    }
                }
            });
            */
            amountLabel = new JLabel("Amount: ");
            amountField = new JTextField(10);
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
            amountValid = false;
            descriptionLabel = new JLabel("Description: ");
            descriptionArea = new JTextArea(5,10);
            descriptionArea.setWrapStyleWord(true);
            descriptionArea.setLineWrap(true);
            descriptionArea.addCaretListener(new CaretListener(){

                public void caretUpdate(CaretEvent e){
                    if(descriptionArea.getText().length() > 500){
                        descriptionArea.setForeground(Color.RED);
                        descriptionValid = false;
                    }else{
                        descriptionArea.setForeground(Color.BLACK);
                        descriptionValid = true;
                    }
                }
            });
            descScroll = new JScrollPane(descriptionArea);
            descriptionValid = true;
            initLayout();
        }
        private void initLayout(){
            GroupLayout gp = new GroupLayout(this);
            gp.setAutoCreateContainerGaps(true);
            gp.setAutoCreateGaps(true);
            setLayout(gp);
            gp.setHorizontalGroup(gp.createSequentialGroup()
                    .addGroup(gp.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(causalLabel)
                        .addComponent(descriptionLabel))
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
                         .addComponent(descriptionLabel)
                         .addComponent(descScroll)));
        }//initLayout
        private boolean isRecordValid(){
            return causalValid && amountValid && descriptionValid;
        }//isRecordValid
        private void addRecord(){
            if(isRecordValid()){
                Transaction tmp = new Transaction();
                tmp.setTransactionDate(dateField.getTimeInMillis());
                tmp.setCausal((String) causalField.getSelectedItem());
                tmp.setAmount(Double.parseDouble(amountField.getText()));
                tmp.setDescription(descriptionArea.getText());
                if(type){
                    tmp.setType(Transaction.IN);
                }else{
                    tmp.setType(Transaction.OUT);
                }
                transactionList.add(tmp);
            }
        }//addRecord
    }//RecordPanel
}//DatePanel
