
package cashmanager.graph;

import cashmanager.calendar.JDateChooser;
import cashmanager.database.DayReport;
import cashmanager.database.Transaction;
import cashmanager.database.CausalAmount;
import cashmanager.toolkit.DateToolkit;
import cashmanager.database.IncomeOutcome;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Kiko
 */
public class PieChart extends JPanel{

    private JDialog dialog;
    private boolean modal;
    private Calendar fromDate;
    private Calendar toDate;
    private List<InnerPane> innerPanes;
    private CommandPane commandPane;
    private JPanel centerPane;

    public PieChart(){
        this(new JDialog(), true);
    }
    public PieChart(JDialog dialog, boolean modal){
        super();
        setLayout(new BorderLayout());
        innitDates();
        initInnerPanes();
        commandPane = new CommandPane();
        add(commandPane, BorderLayout.PAGE_START);
        add(centerPane, BorderLayout.CENTER);
        this.dialog = dialog;
        this.modal = modal;
    }
    private void innitDates(){
        fromDate = Calendar.getInstance();
        fromDate.set(Calendar.DAY_OF_MONTH, 8);
        fromDate.set(Calendar.MONTH, Calendar.JULY);
        fromDate.getTime();
        toDate = Calendar.getInstance();
        toDate.set(Calendar.DAY_OF_MONTH, 20);
        toDate.getTime();
    }//initDates
    private void initInnerPanes(){
        centerPane = new JPanel();
        centerPane.setLayout(new GridLayout(2, 2));
        innerPanes = new ArrayList<InnerPane>();
        InnerPane tmp = new InnerPane(fromDate, toDate, PieChart.InnerPane.IN_OUT_DIFF);
        innerPanes.add(tmp);
        centerPane.add(tmp);
        tmp = new InnerPane(fromDate, toDate, PieChart.InnerPane.IN_OUT_RATIO);
        innerPanes.add(tmp);
        centerPane.add(tmp);
        tmp = new InnerPane(fromDate, toDate, PieChart.InnerPane.INCOME);
        innerPanes.add(tmp);
        centerPane.add(tmp);
        tmp = new InnerPane(fromDate, toDate, PieChart.InnerPane.OUTCOME);
        innerPanes.add(tmp);
        centerPane.add(tmp);
    }//initInnerPanes
    private void updateInnerPanes(){
        for(InnerPane inner: innerPanes){
            inner.updatePeriod(fromDate, toDate);
        }
    }//updateInnerPanes
    public static void main(String args[]){
        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        PieChart p = new PieChart(dialog, true);
        dialog.add(p, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
    }//main
    private class InnerPane extends JPanel{

        private static final int OFFSET_WIDTH = 50;
        private static final int OFFSET_HEIGHT = 50;
        private static final int GRAPH_WIDTH = 200;
        private static final int GRAPH_HEIGHT = 200;
        private static final int PANEL_WIDTH = GRAPH_WIDTH + 2 * OFFSET_WIDTH;
        private static final int PANEL_HEIGHT = GRAPH_HEIGHT + 2 * OFFSET_HEIGHT;
        private static final String INCOME = "income";
        private static final String OUTCOME = "outcome";
        private static final String IN_OUT_RATIO = "ratio";
        private static final String IN_OUT_DIFF = "diff";
        private List<CausalAmount> causalList;
        private IncomeOutcome inOutRatio;
        private String type;
        private Graphics2D g2;
        private Calendar from;
        private Calendar to;

        public InnerPane(Calendar fromDate, Calendar toDate, String type){
            super();
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setType(type);
            setFromDate(fromDate);
            setToDate(toDate);
            updateFields();
        }
        public void setType(String type){
            this.type = type;
        }
        public void setFromDate(Calendar fromDate){
            this.from = fromDate;
        }
        public void setToDate(Calendar toDate){
            this.to = toDate;
        }
        private void updateFields(){
            if(type.equals(IN_OUT_RATIO)){
                updateRatio();
            }else if(type.equals(IN_OUT_DIFF)){
                updateRatio();
            }else if(type.equals(INCOME)){
                updateIncomeList();
            }else if(type.equals(OUTCOME)){
                updateOutcomeList();
            }
        }
        public void updatePeriod(Calendar fromDate, Calendar toDate){
            setFromDate(fromDate);
            setToDate(toDate);
            updateFields();
            repaint();
        }
        private void updatePane(){
            if(type.equals(IN_OUT_RATIO)){
                drawRatioPie();
            }else if(type.equals(IN_OUT_DIFF)){
                drawDiffPie();
            }else if(type.equals(INCOME) || type.equals(OUTCOME)){
                drawInOrOutPie();
            }
        }//updatePane
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            setBackground(Color.WHITE);
            g2 = (Graphics2D) g;
            Rectangle2D r = new Rectangle2D.Double(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
            g2.draw(r);
            updatePane();
        }
        private void drawRatioPie(){
            g2.setStroke(new BasicStroke(2.0f));
            double in = inOutRatio.getIncome();
            double out = inOutRatio.getOutcome();
            boolean notValid = (in == 0.0 || out == 0.0);
            if(notValid){
                if(in == 0.0){
                    g2.setColor(GraphColors.outColors[1]);
                }else if(out == 0.0){
                    g2.setColor(GraphColors.inColors[1]);
                }
                Ellipse2D pie = new Ellipse2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT);
                g2.fill(pie);
                g2.setColor(Color.BLACK);
                g2.draw(pie);
            }else{
                double inPercent = (in * 100) / (in + out);
                double inArcPercent = (inPercent * 360) / 100;
                double outArcPercent = 360 - inArcPercent;
                double initAngle = 90;
                Arc2D inArc = new Arc2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT, initAngle, inArcPercent, Arc2D.PIE);
                initAngle += inArcPercent;
                Arc2D outArc = new Arc2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT, initAngle, outArcPercent, Arc2D.PIE);
                g2.setColor(GraphColors.inColors[1]);
                g2.fill(inArc);
                g2.setColor(GraphColors.outColors[1]);
                g2.fill(outArc);
                g2.setColor(Color.BLACK);
                g2.draw(inArc);
                g2.draw(outArc);
                System.out.println("Ratio inpercent: " + in + "(" + inPercent + ") outpercent: " + out + "(" + (100 - inPercent) + ")");
            }
        }//drawRatioPie
        private void drawDiffPie(){
            g2.setStroke(new BasicStroke(2.0f));
            double in = inOutRatio.getIncome();
            double out = inOutRatio.getOutcome();
            double outPercent = (out * 100) / in;
            double outArcPercent = (outPercent * 360) / 100;
            double inArcPercent = 360 - outArcPercent;
            double initAngle = 90;
            if(in > out){
                Arc2D inArc = new Arc2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT, initAngle, inArcPercent, Arc2D.PIE);
                initAngle += inArcPercent;
                Arc2D outArc = new Arc2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT, initAngle, outArcPercent, Arc2D.PIE);
                g2.setColor(GraphColors.inColors[1]);
                g2.fill(inArc);
                g2.setColor(GraphColors.outColors[1]);
                g2.fill(outArc);
                g2.setColor(Color.BLACK);
                g2.draw(inArc);
                g2.draw(outArc);
            }else{
                Ellipse2D pie = new Ellipse2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT);
                g2.setColor(GraphColors.outColors[1]);
                g2.fill(pie);
                g2.setColor(Color.BLACK);
                g2.draw(pie);
            }
            
            System.out.println("Diff inpercent: "+ in + "(" + (100 - outPercent) + ") outpercent: " + out + "(" + outPercent + ")");
        }//drawDiffPie
        private void drawInOrOutPie(){
            g2.setStroke(new BasicStroke(2.0f));
            double total = 0;
            for(CausalAmount c : causalList){
                total += c.getTotalAmount();
            }
            if(causalList.size() == 1){
                Color c;
                if(type.equals(INCOME)){
                    c = GraphColors.inColors[1];
                } else{
                    c = GraphColors.outColors[1];
                }
                g2.setColor(c);
                Ellipse2D pie = new Ellipse2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT);
                g2.fill(pie);
                g2.setColor(Color.BLACK);
                g2.draw(pie);
            }else{
                double initAngle = 90;
                for(int i = 0; i < causalList.size(); i++){
                    double amount = causalList.get(i).getTotalAmount();
                    double percent = (amount * 100) / total;
                    double arc = (percent * 360) / 100;
                    Color c;
                    if(type.equals(INCOME)){
                        c = GraphColors.inColors[i % GraphColors.COLORS_LENGTH];
                    }else{
                        c = GraphColors.outColors[i % GraphColors.COLORS_LENGTH];
                    }
                    if(i == (causalList.size() - 1) && ((causalList.size() % GraphColors.COLORS_LENGTH) == 1)){
                        if(type.equals(INCOME)){
                            c = GraphColors.inColors[GraphColors.COLORS_LENGTH - 2];
                        }else{
                            c = GraphColors.outColors[GraphColors.COLORS_LENGTH - 2];
                        }
                    }
                    g2.setColor(c);
                    Arc2D tmp = new Arc2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, GRAPH_WIDTH, GRAPH_HEIGHT, initAngle, arc, Arc2D.PIE);
                    g2.fill(tmp);
                    g2.setColor(Color.BLACK);
                    g2.draw(tmp);
                    initAngle += arc;
                    if(type.equals(INCOME)){
                        System.out.print("Income: ");
                    } else{
                        System.out.print("Outcome: ");
                    }
                    System.out.println(causalList.get(i).getCausal() + ": " + amount + "(" + percent + ")");
                }
            }
        }//drawInOrOutPie
        private void updateRatio(){
            inOutRatio = DayReport.getTotalIncomeOutcome(from, to);
        }//updateRatio
        private void updateIncomeList(){
            causalList = Transaction.getCausalTotalIncome(from, to);
        }//updateIncomeList
        private void updateOutcomeList(){
            causalList = Transaction.getCausalTotalOutcome(from, to);
        }//updateOutcomeList
    }//InnerPane
    private class CommandPane extends JPanel implements PropertyChangeListener{
        private JLabel fromLabel;
        private JLabel toLabel;
        private JDateChooser fromDateChooser;
        private JDateChooser toDateChooser;

        public CommandPane(){
            fromLabel = new JLabel("From date:");
            fromDateChooser = new JDateChooser();
            fromDateChooser.setCalendar(fromDate);
            fromDateChooser.addPropertyChangeListener(this);
            toLabel = new JLabel("To date:");
            toDateChooser = new JDateChooser();
            toDateChooser.setCalendar(toDate);
            toDateChooser.addPropertyChangeListener(this);
            initLayout();
        }
        private void initLayout(){
            GroupLayout layout = new GroupLayout(this);
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            setLayout(layout);
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addComponent(fromLabel)
                    .addComponent(fromDateChooser)
                    .addComponent(toLabel)
                    .addComponent(toDateChooser));
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(fromLabel)
                    .addComponent(fromDateChooser)
                    .addComponent(toLabel)
                    .addComponent(toDateChooser)));
        }
        public void propertyChange(PropertyChangeEvent evt){
            if(evt.getPropertyName().equals("JDateChooser")){
                Calendar newDate = (Calendar) evt.getNewValue();
                DateToolkit.resetTime(newDate);
                if(evt.getSource() == fromDateChooser){
                    fromDate.setTime(newDate.getTime());
                }
                if(evt.getSource() == toDateChooser){
                    toDate.setTime(newDate.getTime());
                }
                PieChart.this.updateInnerPanes();
            }
        }
    }//CommandPane
}//PieChart
