
package cashmanager.dialog;

import cashmanager.calendar.JDateChooser;
import cashmanager.database.DayReport;
import java.awt.Window;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cashmanager.database.Transaction;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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
    private JComboBox causalBox;
    private JLabel amountLabel;
    private JTextField amountField;
    private JLabel dateLabel;
    private JDateChooser dateField;
    private JLabel descriptionLabel;
    private JTextArea descriptionArea;
    private JScrollPane descPane;

    //Commands fields
    private JPanel commandPanel;
    private JButton okButton;
    private JButton cancelButton;

    public TransactionPanel(){
        this(new JDialog(), true);
    }

    public TransactionPanel(JDialog dialog, boolean type){
        super();
        setLayout(new BorderLayout());
        this.dialog = dialog;
        this.type = type;
        initFields();
        initCommands();
        add(fieldPanel, BorderLayout.CENTER);
        add(commandPanel, BorderLayout.SOUTH);

        this.dialog.add(this);
        printInfo();
    }

    public JDialog getDialog(){
        return dialog;
    }

    public void printInfo(){
        System.out.println(this.getSize());
        System.out.println(this.getPreferredSize());
        System.out.println(this.getMaximumSize());
        System.out.println(this.getMinimumSize());
    }

    private final void initFields(){
        fieldPanel = new JPanel();
        GroupLayout layout = new GroupLayout(fieldPanel);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        fieldPanel.setLayout(layout);

        causalLabel = new JLabel("Causal:");
        causalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        List<String> causalList = Transaction.getAllCausal();
        causalBox = new JComboBox(causalList.toArray());
        causalBox.setEditable(true);
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
        descPane = new JScrollPane(descriptionArea);


        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(causalLabel)
                .addComponent(amountLabel)
                .addComponent(dateLabel)
                .addComponent(descriptionLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(causalBox)
                .addComponent(amountField)
                .addComponent(dateField)
                .addComponent(descPane)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(causalLabel)
                .addComponent(causalBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(amountLabel)
                .addComponent(amountField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(dateLabel)
                .addComponent(dateField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(descriptionLabel)
                .addComponent(descPane)));

    }//initFields

    private final void initCommands(){
        commandPanel = new JPanel();

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                Transaction trans = new Transaction();
                trans.setCausal((String) causalBox.getSelectedItem());
                trans.setAmount(Double.parseDouble(amountField.getText()));
                trans.setTransactionDate(dateField.getCalendar());
                trans.setDescription(descriptionArea.getText());

                DayReport dr = new DayReport();
                dr.setDay(dateField.getTimeInMillis());
                if(type){
                    trans.setType("in");
                    dr.setIncome(Double.parseDouble(amountField.getText()));
                }else{
                    trans.setType("out");
                    dr.setOutcome(Double.parseDouble(amountField.getText()));
                }

                Transaction.insertTransaction(trans);
                DayReport.updateOrInsert(dr);
                dialog.dispose();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e){
                dialog.dispose();
            }
        });

        commandPanel.add(okButton);
        commandPanel.add(cancelButton);
    }//initCommands

    public static void main(String args[]){
        TransactionPanel t = new TransactionPanel();
        t.getDialog().pack();
        t.getDialog().setVisible(true);
        t.getDialog().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}//TransactionPanel
