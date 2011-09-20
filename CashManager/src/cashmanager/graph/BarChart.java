
package cashmanager.graph;

import cashmanager.calendar.JDateChooser;
import cashmanager.database.DayReport;
import cashmanager.database.Transaction;
import cashmanager.toolkit.DateToolkit;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Kiko
 */
public class BarChart extends JPanel{

    private JDialog dialog;
    private boolean modal;
    private JScrollPane scroll;
    private InnerPane innerPane;
    private CommandPane commandPane;

    public BarChart(){
        this(new JDialog(), true);
    }
    public BarChart(JDialog dialog, boolean modal){
        super();
        setLayout(new BorderLayout());
        innerPane = new InnerPane();
        scroll = new JScrollPane(innerPane);
        scroll.setPreferredSize(new Dimension(400, 450));
        commandPane = new CommandPane();
        add(scroll, BorderLayout.CENTER);
        add(commandPane, BorderLayout.PAGE_START);
        this.dialog = dialog;
        this.modal = modal;
    }
    public static void main(String args[]){
            JDialog dialog = new JDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            BarChart b = new BarChart(dialog, true);
            dialog.add(b, BorderLayout.CENTER);
            dialog.pack();
            dialog.setVisible(true);
        }
    private class InnerPane extends JPanel implements MouseListener{

        private static final int BAR_WIDTH = 25;
        private static final int DAY_WIDTH = 2 * BAR_WIDTH;
        private static final int OFFSET_WIDTH = 50;
        private static final int OFFSET_HEIGHT = 50;
        private static final int GRAPH_HEIGHT = 300;
        private static final int GRAPH_WIDTH = 400;
        private static final int Y_AXIS = OFFSET_HEIGHT + GRAPH_HEIGHT;
        private static final int PANEL_WIDTH = GRAPH_WIDTH + 2 * OFFSET_WIDTH;
        private static final int PANEL_HEIGHT = GRAPH_HEIGHT + 2 * OFFSET_HEIGHT;
        private static final int DAY_STRING_HEIGHT = 15;
        private static final int DETAIL_WIDTH = 2 * BAR_WIDTH;
        private static final int DETAIL_HEIGHT = 20;
        private int numberOfDays;
        private List<DayReport> dayRep;
        private boolean inVisible;
        private boolean outVisible;
        private Calendar fromDate;
        private Calendar toDate;
        private int panelWidth;
        private int dayWidth;
        private int barWidth;
        private double maxValue;
        private Dimension panelSize;
        private boolean dayRepValid;
        private Map<Rectangle2D, DayReport> incomeMap;
        private Map<Rectangle2D, DayReport> outcomeMap;
        private List<Rectangle2D> incomeList;
        private List<Rectangle2D> outcomeList;
        private Graphics2D g2;

        public InnerPane(){
            super();
            inVisible = true;
            outVisible = true;
            initDates();
            updateFields();
            addMouseListener(this);
        }
        private void initDates(){
            fromDate = Calendar.getInstance();
            fromDate.set(Calendar.DAY_OF_MONTH, 1);
            DateToolkit.resetTime(fromDate);
            toDate = Calendar.getInstance();
            toDate.set(Calendar.DAY_OF_MONTH, 1);
            toDate.add(Calendar.MONTH, 1);
            toDate.add(Calendar.DAY_OF_MONTH, -1);
            DateToolkit.resetTime(toDate);
        }
        private void updatePane(){
            updateFields();
            revalidate();
            repaint();
        }
        private void updateFields(){
            initDayReportList();
            updateMaxValue();
            updateDimension();
            updateIncomes();
            updateOutcomes();
        }
        private void initDayReportList(){
            numberOfDays = DateToolkit.daysBetween(fromDate, toDate);
            dayRepValid = false;
            if(numberOfDays > 0){
                dayRep = DayReport.getDayReportBetween(fromDate, toDate);
                dayRepValid = true;
                fillDayReportList();
            }
        }
        private void fillDayReportList(){
            if(dayRepValid){
                List<DayReport> tmpList = new ArrayList<DayReport>();
                Calendar fromClone = (Calendar) fromDate.clone();
                while(fromClone.compareTo(toDate) != 1){
                    int index = DayReport.isDayBetween(fromClone, dayRep);
                    DayReport d;
                    if(index != -1){
                        d = dayRep.get(index);
                    }else{
                        d = new DayReport((Calendar)fromClone.clone());
                    }
                    tmpList.add(d);
                    fromClone.add(Calendar.DAY_OF_MONTH, 1);
                }
                dayRep = tmpList;
            }
        }
        private void updateMaxValue(){
            if(dayRepValid){
                maxValue = 0;
                if(inVisible){
                    maxValue = DayReport.maximumIncome(dayRep);
                }
                if(outVisible){
                    if(maxValue < DayReport.maximumOutcome(dayRep)){
                        maxValue = DayReport.maximumOutcome(dayRep);
                    }
                }
            }
        }
        private void updateDimension(){
            if(dayRepValid){
                panelWidth = 0;
                barWidth = 0;
                if(inVisible ^ outVisible){
                    barWidth = 2 * BAR_WIDTH;
                }else{
                    barWidth = BAR_WIDTH;
                }
                panelWidth = DAY_WIDTH * numberOfDays;
                if(panelWidth == 0){
                    panelWidth += GRAPH_WIDTH;
                }
                panelSize = new Dimension(panelWidth + 2 * OFFSET_WIDTH, PANEL_HEIGHT);
                setPreferredSize(panelSize);
            }
        }
        private void updateIncomes(){
            if(dayRepValid){
                incomeMap = new HashMap<Rectangle2D, DayReport>();
                incomeList = new ArrayList<Rectangle2D>();
                double x = OFFSET_WIDTH;
                double w = barWidth;
                double y;
                double h;
                Rectangle2D r;
                for(DayReport d : dayRep){
                    h = (d.getIncome() * GRAPH_HEIGHT) / maxValue;
                    y = Y_AXIS - h;
                    r = new Rectangle2D.Double(x, y, w, h);
                    incomeMap.put(r, d);
                    incomeList.add(r);
                    x += DAY_WIDTH;
                }
            }
        }
        private void updateOutcomes(){
            if(dayRepValid){
                outcomeMap = new HashMap<Rectangle2D, DayReport>();
                outcomeList = new ArrayList<Rectangle2D>();
                double x = OFFSET_WIDTH;
                if(inVisible){
                    x += barWidth;
                }
                double w = barWidth;
                double y;
                double h;
                Rectangle2D r;
                for(DayReport d : dayRep){
                    h = (d.getOutcome() * GRAPH_HEIGHT) / maxValue;
                    y = Y_AXIS - h;
                    r = new Rectangle2D.Double(x, y, w, h);
                    outcomeMap.put(r, d);
                    outcomeList.add(r);
                    x += DAY_WIDTH;
                }
            }
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g2 = (Graphics2D) g;
            setBackground(Color.WHITE);
            drawChart();
        }
        private void initGraph(){
            drawGuideLines();
            drawDays();
            drawRectangles();
            g2.draw(new Rectangle2D.Double(OFFSET_WIDTH, OFFSET_HEIGHT, panelWidth, GRAPH_HEIGHT));
        }
        private void drawGuideLines(){
            if(dayRepValid && (inVisible || outVisible)){
                int step = GRAPH_HEIGHT / 10;
                double percent = 100;
                for(int i = OFFSET_HEIGHT; i < Y_AXIS; i += step){
                    double percentValueOfMax = (maxValue * percent) / 100;
                    Line2D l = new Line2D.Double(OFFSET_WIDTH, i, OFFSET_WIDTH + panelWidth, i);
                    float[] dash = {5.0f};
                    g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0.0f));
                    g2.draw(l);
                    String stringValueOfPercent = String.format("%.2f", percentValueOfMax);
                    g2.drawString(stringValueOfPercent, 10, i + 5);
                    percent -= 10;
                }
                g2.setStroke(new BasicStroke(1.0f));
            }
        }
        private void drawRectangles(){
            int x = OFFSET_WIDTH;
            int y = OFFSET_HEIGHT;
            int w = DAY_WIDTH;
            int h = GRAPH_HEIGHT;
            Rectangle2D r;
            for(int i = 0; i < numberOfDays; i++){
                r = new Rectangle2D.Double(x, y, w, h);
                g2.setColor(Color.BLACK);
                g2.draw(r);
                x += DAY_WIDTH;
            }
        }
        private void drawChart(){
            initGraph();
            drawIncomes();
            drawOutcomes();
        }
        private void drawIncomes(){
            if(dayRepValid && inVisible){
                for(Rectangle2D r : incomeList){
                    g2.setColor(GraphColors.inColors[1]);
                    g2.fill(r);
                    g2.setColor(Color.BLACK);
                    g2.draw(r);
                }
            }
        }
        private void drawOutcomes(){
            if(dayRepValid && outVisible){
                for(Rectangle2D r : outcomeList){
                    g2.setColor(GraphColors.outColors[1]);
                    g2.fill(r);
                    g2.setColor(Color.BLACK);
                    g2.draw(r);
                }
            }
        }
        private void drawDays(){
            if(dayRepValid){
                boolean stringUp = false;
                int xString = OFFSET_WIDTH;
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                for(DayReport d : dayRep){
                    int yString = Y_AXIS + DAY_STRING_HEIGHT;
                    String stringDate = df.format(d.getTime());
                    if(stringUp){
                        yString += DAY_STRING_HEIGHT;
                    }
                    g2.drawString(stringDate, xString, yString);
                    stringUp = !stringUp;
                    xString += DAY_WIDTH;
                }
            }
        }
        public void mouseClicked(MouseEvent e){
        }
        public void mousePressed(MouseEvent e){
            repaint();
        }
        public void mouseReleased(MouseEvent e){
            boolean found = false;
            String type = "";
            Point mouseClick = e.getPoint();
            Rectangle2D detail = new Rectangle2D.Double(e.getX(), e.getY(), DETAIL_WIDTH, DETAIL_HEIGHT);
            Graphics2D g = (Graphics2D) getGraphics();
            String value = "";
            Set<Rectangle2D> incomeSet = incomeMap.keySet();
            for(Rectangle2D r : incomeSet){
                if(r.contains(mouseClick)){
                    found = true;
                    DayReport tmp = incomeMap.get(r);
                    value = String.format("%.2f", tmp.getIncome());
                    type = Transaction.IN;
                    g.setColor(GraphColors.IN_COLOR);
                    g.fill(r);
                    g.setColor(Color.BLACK);
                    g.setStroke(new BasicStroke(2.0f));
                    g.draw(r);
                }
            }
            Set<Rectangle2D> outcomeSet = outcomeMap.keySet();
            for(Rectangle2D r : outcomeSet){
                if(r.contains(mouseClick)){
                    found = true;
                    DayReport tmp = outcomeMap.get(r);
                    value = String.format("%.2f€", tmp.getOutcome());
                    type = Transaction.OUT;
                    g.setColor(GraphColors.OUT_COLOR);
                    g.fill(r);
                    g.setColor(Color.BLACK);
                    g.setStroke(new BasicStroke(2.0f));
                    g.draw(r);
                }
            }
            if(found){
                g.setColor(Color.WHITE);
                g.fill(detail);
                g.setColor(Color.BLACK);
                g.draw(detail);
                if(type.equals(Transaction.IN)){
                    g.setColor(GraphColors.IN_COLOR);
                }
                if(type.equals(Transaction.OUT)){
                    g.setColor(GraphColors.OUT_COLOR);
                }
                g.drawString(value, e.getX() + 5, e.getY() + DETAIL_HEIGHT - 5);
                g.setColor(Color.BLACK);
            }else{
                repaint();
            }
        }
        public void mouseEntered(MouseEvent e){
        }
        public void mouseExited(MouseEvent e){
        }

    }
    private class CommandPane extends JPanel implements PropertyChangeListener, ItemListener{
        private JLabel fromLabel;
        private JLabel toLabel;
        private JDateChooser fromDateChooser;
        private JDateChooser toDateChooser;
        private JCheckBox incomeVisible;
        private JCheckBox outcomeVisible;

        public CommandPane(){
            fromLabel = new JLabel("From date:");
            fromDateChooser = new JDateChooser();
            fromDateChooser.setCalendar(innerPane.fromDate);
            fromDateChooser.addPropertyChangeListener(this);
            toLabel = new JLabel("To date:");
            toDateChooser = new JDateChooser();
            toDateChooser.setCalendar(innerPane.toDate);
            toDateChooser.addPropertyChangeListener(this);
            incomeVisible = new JCheckBox("Income", innerPane.inVisible);
            incomeVisible.addItemListener(this);
            outcomeVisible = new JCheckBox("OutCome", innerPane.outVisible);
            outcomeVisible.addItemListener(this);
            initLayout();
        }
        private void initLayout(){
            GroupLayout layout = new GroupLayout(this);
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            setLayout(layout);
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(fromLabel)
                    .addComponent(toLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(fromDateChooser)
                    .addComponent(toDateChooser))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(incomeVisible)
                    .addComponent(outcomeVisible)));
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(fromLabel)
                    .addComponent(fromDateChooser)
                    .addComponent(incomeVisible))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(toLabel)
                    .addComponent(toDateChooser)
                    .addComponent(outcomeVisible)));
        }
        public void propertyChange(PropertyChangeEvent evt){
            if(evt.getPropertyName().equals("JDateChooser")){
                Calendar newDate = (Calendar) evt.getNewValue();
                DateToolkit.resetTime(newDate);
                if(evt.getSource() == fromDateChooser){
                    innerPane.fromDate.setTime(newDate.getTime());
                }
                if(evt.getSource() == toDateChooser){
                    innerPane.toDate.setTime(newDate.getTime());
                }
                innerPane.updatePane();
            }
        }
        public void itemStateChanged(ItemEvent e){
            if(e.getItemSelectable() == incomeVisible){
                if(e.getStateChange() == ItemEvent.DESELECTED){
                    innerPane.inVisible = false;
                }else{
                    innerPane.inVisible = true;
                }
            }
            if(e.getItemSelectable() == outcomeVisible){
                if(e.getStateChange() == ItemEvent.DESELECTED){
                    innerPane.outVisible = false;
                }else{
                    innerPane.outVisible = true;
                }
            }
            innerPane.updatePane();
        }
    }//CommandPane
}//BarChart
