
package cashmanager.dialog;

import cashmanager.calendar.JDateChooser;
import cashmanager.database.DayReport;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import cashmanager.database.Transaction;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Kiko
 */
public class TransactionPanel extends JPanel{

    private JDialog dialog;
    private boolean type;

    //Dialog fields
    private JPanel fieldPanel;
    private JLabel causalLabel;
    private JComboBox causalField;
    private JLabel amountLabel;
    private JTextField amountField;
    private JLabel dateLabel;
    private JDateChooser dateField;
    private JLabel descriptionLabel;
    private JTextArea descriptionArea;
    private JScrollPane descriptionPane;

    //Commands fields
    private JPanel commandPanel;
    private JButton insertButton;
    private JButton resetButton;

    public TransactionPanel(){
        this(new JDialog(), true);
    }
    public TransactionPanel(JDialog dialog, boolean type){
        super();
        setLayout(new BorderLayout());
        this.dialog = dialog;
        this.type = type;
        initFieldPanel();
        initCommandPanel();
        add(fieldPanel, BorderLayout.CENTER);
        add(commandPanel, BorderLayout.SOUTH);

//        printInfo();
    }
    public JDialog getDialog(){
        return dialog;
    }//getDialog
    public void printInfo(){
        System.out.println(this.getSize());
        System.out.println(this.getPreferredSize());
        System.out.println(this.getMaximumSize());
        System.out.println(this.getMinimumSize());
    }//printInfo
    private final void initFieldPanel(){
        causalLabel = new JLabel("Causal:");
        causalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        List<String> causalList = Transaction.getAllCausal();
        causalField = new JComboBox(causalList.toArray());
        causalField.setEditable(true);
        amountLabel = new JLabel("Amount:");
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        amountField = new JTextField(15);
        dateLabel = new JLabel("Transaction Date:");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateField = new JDateChooser();
        descriptionLabel = new JLabel("Description:");
        descriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        descriptionArea = new JTextArea(5, 15);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionPane = new JScrollPane(descriptionArea);

        initFieldPanelLayout();
    }//initFieldPanle
    private void initFieldPanelLayout(){
        fieldPanel = new JPanel();
        GroupLayout layout = new GroupLayout(fieldPanel);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        fieldPanel.setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(causalLabel)
                .addComponent(amountLabel)
                .addComponent(dateLabel)
                .addComponent(descriptionLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(causalField)
                .addComponent(amountField)
                .addComponent(dateField)
                .addComponent(descriptionPane)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(causalLabel)
                .addComponent(causalField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(amountLabel)
                .addComponent(amountField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(dateLabel)
                .addComponent(dateField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(descriptionLabel)
                .addComponent(descriptionPane)));
    }//initFieldPanelLayout
    private final void initCommandPanel(){
        commandPanel = new JPanel();

        insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                Transaction trans = new Transaction();
                trans.setCausal((String) causalField.getSelectedItem());
                trans.setAmount(Double.parseDouble(amountField.getText()));
                trans.setTransactionDate(dateField.getCalendar());
                trans.setDescription(descriptionArea.getText());
                if(type){
                    trans.setType(Transaction.IN);
                }else{
                    trans.setType(Transaction.OUT);
                }

                List<Transaction> list = new ArrayList<Transaction>();
                list.add(trans);
                Transaction.insertTransactions(list);
                dialog.dispose();
            }
        });

        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                resetFields();
            }
        });

        commandPanel.add(insertButton);
        commandPanel.add(resetButton);
    }//initCommandPanel
    private void resetFields(){
        List<String> causalList = Transaction.getAllCausal();
        causalField = new JComboBox(causalList.toArray());
        causalField.setEditable(true);
        amountField.setText("");
        descriptionArea.setText("");
    }
    public static void main(String args[]){
        TransactionPanel t = new TransactionPanel();
        t.getDialog().setVisible(true);
        t.getDialog().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        t.getDialog().add(t);
        t.getDialog().pack();
    }//main

}//TransactionPanel
