
package cashmanager.screen;

import cashmanager.dialog.TransactionDialog;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author Admin
 */
public class MainFrame extends JFrame{
    private JToolBar toolBar;
    private JPanel centerPane;

    public MainFrame(){
        super("CashManager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initToolBar();
        centerPane = new SummaryScreen();
        add(toolBar, BorderLayout.PAGE_START);
        add(centerPane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }
    private void initToolBar(){
        toolBar = new JToolBar("ToolBar");
        JButton b = new JButton("IN Transaction");
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e){
                TransactionDialog t = new TransactionDialog(MainFrame.this, true);
            }
        });
        toolBar.add(b);
        JButton c = new JButton("OUT Transaction");
        c.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e){
                TransactionDialog t = new TransactionDialog(MainFrame.this, false);
            }
        });
        toolBar.add(c);
    }//initToolBar
    public static void main(String args[]){
        MainFrame m = new MainFrame();
    }//main
}//MainFrame
