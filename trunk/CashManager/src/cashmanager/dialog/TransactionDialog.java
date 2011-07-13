/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.dialog;

import java.awt.Window;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cashmanager.database.Transaction;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;
import javax.swing.Box;
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
    private JTextField dateField;
    private JLabel descriptionLabel;
    private JTextArea descriptionArea;

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
        setSize(400,300);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private final void initFields(){
        fieldPanel = new JPanel();

        causalLabel = new JLabel("Causal:");
        causalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        List<String> causalList = Transaction.getAllCausal();
        causalBox = new JComboBox(causalList.toArray());
        causalBox.setEditable(true);
        ammountLabel = new JLabel("Ammount:");
        ammountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ammountField = new JTextField(15);
        dateLabel = new JLabel("Transaction date:");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateField = new JTextField(15);
        descriptionLabel = new JLabel("Description:");
        descriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        descriptionArea = new JTextArea(10, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);


        Box cBox = Box.createHorizontalBox();
        cBox.add(causalLabel);
        cBox.add(Box.createHorizontalStrut(10));
        cBox.add(causalBox);
        //cBox.add(Box.createHorizontalStrut(50));

        Box aBox = Box.createHorizontalBox();
        aBox.add(ammountLabel);
        aBox.add(Box.createHorizontalStrut(10));
        aBox.add(ammountField);
        //aBox.add(Box.createHorizontalStrut(50));

        Box dBox = Box.createHorizontalBox();
        dBox.add(dateLabel);
        dBox.add(Box.createHorizontalStrut(10));
        dBox.add(dateField);
        //dBox.add(Box.createHorizontalStrut(50));

        Box descBox = Box.createHorizontalBox();
        descBox.add(descriptionLabel);
        descBox.add(Box.createHorizontalStrut(10));
        descBox.add(descriptionArea);
        //descBox.add(Box.createHorizontalStrut(300));

        fieldPanel.add(cBox);
        fieldPanel.add(aBox);
        fieldPanel.add(dBox);
        fieldPanel.add(descBox);

        /*
        fieldPanel.add(causalBox);
        fieldPanel.add(ammountLabel);
        fieldPanel.add(ammountField);
        fieldPanel.add(dateLabel);
        fieldPanel.add(dateField);
        fieldPanel.add(descriptionLabel);
        fieldPanel.add(new JScrollPane(descriptionArea));
         */

    }//initFields

    private final void initCommands(){
        commandPanel = new JPanel();

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Transaction trans = new Transaction();
                trans.setCausal((String)causalBox.getSelectedItem());
                trans.setAmmount(Integer.parseInt(ammountField.getText()));
                Calendar c = Calendar.getInstance();
                trans.setTransactionDate(c);
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
        final JFrame frame = new JFrame();
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                TransactionDialog t = new TransactionDialog(frame);
            }
        });
        frame.add(button);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocation(500, 500);
        frame.setVisible(true);
    }
}//TransactionDialog
