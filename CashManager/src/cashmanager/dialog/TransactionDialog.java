
package cashmanager.dialog;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Admin
 */
public class TransactionDialog extends JDialog{

    private String title = "Transaction Dialog";
    private boolean type;
    private JTabbedPane tabPane;
    private TransactionPanel transactionPane;
    private String transactionTitle = "Transaction Panel";
    private CausalPanel causalPane;
    private String causalTitle = "Causal Panel";
    private DatePanel datePane;
    private String dateTitle = "Date Panel";
    private JLabel statusLabel;

    public TransactionDialog(){
        this(new JFrame(), true);
    }
    public TransactionDialog(JFrame parent, boolean type){
        super(parent, true);
        super.setTitle(title);
        this.type = type;
        tabPane = new JTabbedPane();
        transactionPane = new TransactionPanel(this, type);
        causalPane = new CausalPanel(this, type);
        datePane = new DatePanel(this, type);
        tabPane.add(transactionTitle, transactionPane);
        tabPane.add(causalTitle, causalPane);
        tabPane.add(dateTitle, datePane);
        statusLabel = new JLabel("Ready");

        initLayout();
    }
    private void initLayout(){
        add(tabPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.PAGE_END);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//initLayout
    public static void main(String args[]){
        TransactionDialog t = new TransactionDialog();
    }

}//TransactionDialog