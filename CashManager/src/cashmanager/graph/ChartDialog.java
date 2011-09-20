/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cashmanager.graph;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Kiko
 */
public class ChartDialog extends JDialog{

    private String title = "Charts Dialog";
    private boolean type;
    private JTabbedPane tabPane;
    private BarChart barChartPane;
    private String barChartTitle = "BarChart Panel";
    private BalanceChart balanceChartPane;
    private String balanceChartTitle = "BalanceChart Panel";
    private PieChart pieChartPane;
    private String pieChartTitle = "PieChart Panel";
    private JLabel statusLabel;

    public ChartDialog(){
        this(new JFrame(), true);
    }
    public ChartDialog(JFrame parent, boolean type){
        super(parent, true);
        super.setTitle(title);
        this.type = type;
        tabPane = new JTabbedPane();
        barChartPane = new BarChart(this, type);
        balanceChartPane = new BalanceChart(this, type);
        pieChartPane = new PieChart(this, type);
        tabPane.add(barChartPane, barChartTitle);
        tabPane.add(balanceChartPane, balanceChartTitle);
        tabPane.add(pieChartPane, pieChartTitle);
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
        ChartDialog c = new ChartDialog();
    }
}
