/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.dialog;

import cashmanager.calendar.JDateChooser;
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
public class TransactionDialog extends JDialog{

    private JPanel fieldPanel;
    private JLabel causalLabel;
    private JComboBox causalBox;
    private JLabel ammountLabel;
    private JTextField ammountField;
    private JLabel dateLabel;
    private JDateChooser dateField;
    private JLabel descriptionLabel;
    private JTextArea descriptionArea;
    private JScrollPane descPane;

    //Commands fields
    private JPanel commandPanel;
    private JButton okButton;
    private JButton cancelButton;

    public TransactionDialog(Window owner){
        super(owner, "Transaction Dialog");
        initFields();
        initCommands();
        add(fieldPanel, BorderLayout.CENTER);
        add(commandPanel, BorderLayout.SOUTH);
        setSize(getPreferredSize());
        System.out.println(this.getSize());
        System.out.println(this.getPreferredSize());
        System.out.println(this.getMaximumSize());
        System.out.println(this.getMinimumSize());
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
        ammountLabel = new JLabel("Ammount:");
        ammountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ammountField = new JTextField(15);
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
                    .addComponent(ammountLabel)
                    .addComponent(dateLabel)
                    .addComponent(descriptionLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(causalBox)
                    .addComponent(ammountField)
                    .addComponent(dateField)
                    .addComponent(descPane))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(causalLabel)
                    .addComponent(causalBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(ammountLabel)
                    .addComponent(ammountField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dateLabel)
                    .addComponent(dateField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLabel)
                    .addComponent(descPane))
        );

    }//initFields

    private final void initCommands(){
        commandPanel = new JPanel();

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Transaction trans = new Transaction();
                trans.setCausal((String)causalBox.getSelectedItem());
                trans.setAmmount(Double.parseDouble(ammountField.getText()));
                trans.setTransactionDate(dateField.getCalendar());
                trans.setDescription(descriptionArea.getText());

                Transaction.insertTransaction(trans);
                dispose();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });

        commandPanel.add(okButton);
        commandPanel.add(cancelButton);
    }//initCommands

    public static void main(String args[]){
        final JFrame frame = null;
//        final JFrame frame = new JFrame();
//        JButton button = new JButton("OK");
//        button.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e){
                TransactionDialog t = new TransactionDialog(frame);
//            }
//        });
//        frame.add(button);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setLocation(500, 500);
//        frame.setVisible(true);
    }
}//TransactionDialog
