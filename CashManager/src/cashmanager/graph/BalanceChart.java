
package cashmanager.graph;

import cashmanager.calendar.JDateChooser;
import cashmanager.database.DayBalance;
import cashmanager.database.DayReport;
import cashmanager.toolkit.DateToolkit;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Kiko
 */
public class BalanceChart extends JPanel{

    private JDialog dialog;
    private boolean modal;
    private JScrollPane scrollPane;
    private InnerPane innerPane;
    private CommandPane commandPane;
    private DetailPane detailPane;

    public BalanceChart(){
        this(new JDialog(), true);
    }
    public BalanceChart(JDialog dialog, boolean modal){
        super();
        setLayout(new BorderLayout());
        innerPane = new InnerPane();
        scrollPane = new JScrollPane(innerPane);
        scrollPane.setPreferredSize(new Dimension(400,450));
        commandPane = new CommandPane();
        detailPane = new DetailPane();
        add(scrollPane, BorderLayout.CENTER);
        add(commandPane, BorderLayout.PAGE_START);
        add(detailPane, BorderLayout.PAGE_END);
        this.dialog = dialog;
        this.modal = modal;
    }
    public static void main(String args[]){
        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        BalanceChart b = new BalanceChart(dialog, true);
        dialog.add(b);
        dialog.setPreferredSize(new Dimension(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth(), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight()));
        dialog.pack();
        dialog.setVisible(true);
    }
    private class InnerPane extends JPanel implements MouseListener, KeyListener{

        private static final int GRAPH_WIDTH = 300;
        private static final int GRAPH_HEIGHT = 300;
        private static final int OFFSET_WIDTH = 100;
        private static final int OFFSET_HEIGHT = 50;
        private static final int PANEL_WIDTH = GRAPH_WIDTH + 2 * OFFSET_WIDTH;
        private static final int PANEL_HEIGHT = GRAPH_HEIGHT + 2 * OFFSET_HEIGHT;
        private static final int Y_AXIS = GRAPH_HEIGHT + OFFSET_HEIGHT;
        private static final int DAY_WIDTH = 50;
        private static final int DAY_STRING_HEIGHT = 15;
        private static final int RADIUS = 5;
        private Calendar fromDate;
        private Calendar toDate;
        private int numberOfDays;
        private List<DayBalance> dayBalanceList;
        private GeneralPath balancePath;
        private int panelWidth;
        private double maxValue;
        private double minValue;
        private double extent;
        private double increment;
        private double min;
        private double max;
        private double extentMaxMin;
        private double incMaxMin;
        boolean positive;
        boolean negative;
        private double xAxis;
        private Dimension panelSize;
        private int offsetWidth;
        private boolean dayBalValid;
        private Graphics2D g2;
        private Rectangle2D graphRect;
        private boolean graphicsReady;

        private List<String> dayList;
        private List<Point2D> pointList;
        private List<Ellipse2D> circleList;
        private List<Rectangle2D> dayRectangleList;
        private Map<String, Integer> guideLines;
        private List<DayReport> dayReportList;

        private int dayBalanceIndex;

        private InnerPane(){
            super();
            setBorder(new TitledBorder("BalanceChart"));
            initDates();
            updateFields();
            addMouseListener(this);
            addKeyListener(this);
            setFocusable(true);
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
//            fromDate.set(Calendar.MONTH, Calendar.JUNE);
//            toDate.set(Calendar.MONTH, Calendar.JULY);
        }
        private Calendar getFromDate(){
            return fromDate;
        }
        private void setFromDate(Calendar from){
            DateToolkit.resetTime(from);
            fromDate = from;
        }
        private Calendar getToDate(){
            return toDate;
        }
        private void setToDate(Calendar to){
            DateToolkit.resetTime(to);
            toDate = to;
        }
        private void setDayBalanceIndex(int index){
            dayBalanceIndex = index;
        }
        private void incDayBalanceIndex(){
            dayBalanceIndex = (dayBalanceIndex + 1) % numberOfDays;
        }
        private void decDayBalanceIndex(){
            if(dayBalanceIndex <= 0){
                dayBalanceIndex = numberOfDays -1;
            }else{
                dayBalanceIndex = (dayBalanceIndex - 1) % numberOfDays;
            }
        }
        private void updatePane(){
            updateFields();
            revalidate();
            repaint();
        }
        private void updateFields(){
            setDayBalanceIndex(-1);
            updateDayBalanceList();
            updateMaxMinExtent();
            updateGuideLines();
            updateDayList();
            updateDayReportList();
            updatePath();
            updateCircleList();
            updateDayRectangleList();
            updateDimension();
        }
        private void updateDayBalanceList(){
            dayBalValid = false;
            numberOfDays = DateToolkit.daysBetween(fromDate, toDate);
            if(numberOfDays > 0){
                dayBalanceList = DayReport.getDayBalanceBetween(fromDate, toDate);
                dayBalValid = true;
                fillDayBalanceList();
            }
            DayBalance.printDayReportList(dayBalanceList);
        }
        private void fillDayBalanceList(){
            if(dayBalValid){
                List<DayBalance> tmpList = new ArrayList<DayBalance>();
                DayBalance lastBalance = DayReport.getBalanceToDate(fromDate);
                Calendar fromClone = (Calendar) fromDate.clone();
                while(fromClone.compareTo(toDate) != 1){
                    int index = DayBalance.isDayBetween(fromClone, dayBalanceList);
                    DayBalance d;
                    if(index != -1){
                        d = dayBalanceList.get(index);
                        lastBalance = dayBalanceList.get(index);
                    }else{
                        d = new DayBalance((Calendar)fromClone.clone(), lastBalance.getBalance());
                    }
                    tmpList.add(d);
                    fromClone.add(Calendar.DAY_OF_MONTH, 1);
                }
                dayBalanceList = tmpList;
            }
        }
        private void updateMaxMinExtent(){
            maxValue = DayBalance.getMax(dayBalanceList);
            minValue = DayBalance.getMin(dayBalanceList);
            extent = maxValue - minValue;
            max = maxValue;
            min = minValue;
            if(extent == 0){
                extent = 2;
                max = maxValue + 1;
                min = minValue - 1;
            }
            increment = extent / 10;
            max += increment;
            min -= increment;
            extentMaxMin = max - min;
            incMaxMin = extentMaxMin / 10;
            positive = (Math.signum(max) > 0) && (Math.signum(min) >= 0);
            negative = (Math.signum(max) <= 0) && (Math.signum(min) < 0);
        }
        private void updateDayList(){
            if(dayBalValid){
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                dayList = new ArrayList<String>();
                for(DayBalance d : dayBalanceList){
                    String stringDate = df.format(d.getTime());
                    dayList.add(stringDate);
                }
            }
        }
        private void updateGuideLines(){
            if(dayBalValid){
                offsetWidth = 0;
                guideLines = new TreeMap<String, Integer>();
                double value = max;
                int steps = 12;
                int step = GRAPH_HEIGHT / steps;
                int y = OFFSET_HEIGHT;
                String valueString;
                String currency = Currency.getInstance(Locale.getDefault()).getSymbol();
                for(int i = 0; i <= steps; i++){
                    valueString = String.format("%.2f %s", value, currency);
                    guideLines.put(valueString, y);
                    value -= increment;
                    y += step;
                }
                if(graphicsReady){
                    FontMetrics fontMetric = g2.getFontMetrics();
                    Set<String> guideString = guideLines.keySet();
                    for(String s : guideString){
                        int stringWidth = fontMetric.stringWidth(s);
                        if(stringWidth > offsetWidth){
                            offsetWidth = stringWidth;
                        }
                    }
                    offsetWidth += 20;
                }else{
                    offsetWidth = OFFSET_WIDTH;
                }
            }
        }
        private void updatePath(){
            if(dayBalValid){
                double value;
                double shiftToZero;
                if(negative){
                    value = min - max;
                    shiftToZero = max;
                }else{
                    value = max - min;
                    shiftToZero = min;
                }
                
                balancePath = new GeneralPath();
                pointList = new ArrayList<Point2D>();
                double x = offsetWidth + DAY_WIDTH / 2;
                for(int i = 0; i < numberOfDays; i++){
                    DayBalance tmp = dayBalanceList.get(i);
                    double tmpY = (tmp.getBalance() - shiftToZero) / value;
                    if(negative){
                        tmpY = 1 - tmpY;
                    }
                    double y = Y_AXIS - (tmpY * GRAPH_HEIGHT);
                    if(i == 0){
                        balancePath.moveTo(x, y);
                    }else{
                        balancePath.lineTo(x, y);
                    }
                    //adding point to poinList
                    Point2D p = new Point2D.Double(x, y);
                    pointList.add(p);
                    //prepare for next iteration
                    x += DAY_WIDTH;
                }
                if(positive || negative){
                    xAxis = 0;
                }else{
                    xAxis = Y_AXIS + ((shiftToZero / value) * GRAPH_HEIGHT);
                }
            }
        }
        private void updateCircleList(){
            circleList = new ArrayList<Ellipse2D>();
            double diameter = 2 * RADIUS;
            for(Point2D p : pointList){
                Ellipse2D e = new Ellipse2D.Double(p.getX() - RADIUS, p.getY() - RADIUS, diameter, diameter);
                circleList.add(e);
            }
        }
        private void updateDayRectangleList(){
            if(dayBalValid){
                dayRectangleList = new ArrayList<Rectangle2D>();
                int x = offsetWidth;
                int y = OFFSET_HEIGHT;
                int w = DAY_WIDTH;
                int h = GRAPH_HEIGHT;
                Rectangle2D r;
                for(int i = 0; i < numberOfDays; i++){
                    r = new Rectangle2D.Double(x, y, w, h);
                    dayRectangleList.add(r);
                    x += DAY_WIDTH;
                }
            }
        }
        private void updateDimension(){
            if(dayBalValid){
                panelWidth = numberOfDays * DAY_WIDTH;
            }else{
                panelWidth = GRAPH_WIDTH;
                offsetWidth = OFFSET_WIDTH;
            }
            panelSize = new Dimension(panelWidth + 2 * offsetWidth, PANEL_HEIGHT);
            graphRect = new Rectangle2D.Double(offsetWidth, OFFSET_HEIGHT, panelWidth, GRAPH_HEIGHT);
            setPreferredSize(panelSize);
        }
        private void updateDayReportList(){
            if(dayBalValid){
                dayReportList = DayReport.getDayReportBetween(fromDate, toDate);
                fillDayReportList();
            }
        }
        private void fillDayReportList(){
            if(dayBalValid){
                List<DayReport> tmpList = new ArrayList<DayReport>();
                Calendar fromClone = (Calendar) fromDate.clone();
                while(fromClone.compareTo(toDate) != 1){
                    int index = DayReport.isDayBetween(fromClone, dayReportList);
                    DayReport d;
                    if(index != -1){
                        d = dayReportList.get(index);
                    }else{
                        d = new DayReport((Calendar)fromClone.clone());
                    }
                    tmpList.add(d);
                    fromClone.add(Calendar.DAY_OF_MONTH, 1);
                }
                dayReportList = tmpList;
            }
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g2 = (Graphics2D) g;
            if(!graphicsReady){
                graphicsReady = true;
            }
            setBackground(Color.WHITE);
            drawChart();
            requestFocusInWindow();
        }
        private void drawChart(){
            initChart();
            drawPath();
            drawCircles();
            highlightCircle();
        }
        private void initChart(){
            drawGuideLines();
            drawDays();
            drawDayRectangle();
            g2.draw(graphRect);
        }
        private void drawGuideLines(){
            if(dayBalValid){
                drawPosNegAxis();
                double value = max;
                int steps = 12;
                int step = GRAPH_HEIGHT / steps;
                int x1 = offsetWidth;
                int x2 = offsetWidth + panelWidth;
                int y = OFFSET_HEIGHT;
                int xString = 10;
                int yString = y + 5;
                Line2D l;
                String stringValueOfPercent;
                float[] dash = {5.0f};
                BasicStroke basicStroke = new BasicStroke(1.5f);
                FontMetrics fontMetric = g2.getFontMetrics();
                int stringHeight = fontMetric.getHeight();

                if(xAxis != 0){
                    l = new Line2D.Double(x1, xAxis, x2, xAxis);
                    g2.setStroke(basicStroke);
                    g2.draw(l);
                }
                basicStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0.0f);
                Set<String> guideString = guideLines.keySet();
                for(String s : guideString){
                    y = guideLines.get(s).intValue();
                    l = new Line2D.Double(x1, y, x2, y);
                    g2.setStroke(basicStroke);
                    g2.draw(l);
                    g2.drawString(s, xString, y);
                }
                /*for(int i = 0; i <= steps; i++){
                    l = new Line2D.Double(x1, y, x2, y);
                    g2.setStroke(basicStroke);
                    g2.draw(l);
                    stringValueOfPercent = String.format("%.2f", value);
                    g2.drawString(stringValueOfPercent, xString, yString);
                    value -= increment;
                    y += step;
                    yString = y + 5;
                }*/
                g2.setStroke(new BasicStroke(1.0f));
            }
        }
        private void drawPosNegAxis(){
            if(dayBalValid){
                if(positive || negative){
                    if(positive){
                        g2.setColor(GraphColors.inColors[2]);
                    }
                    if(negative){
                        g2.setColor(GraphColors.outColors[2]);
                    }
                    g2.fill(graphRect);
                }else{
                    Rectangle2D pos = new Rectangle2D.Double(offsetWidth, OFFSET_HEIGHT, panelWidth, xAxis - OFFSET_HEIGHT);
                    Rectangle2D neg = new Rectangle2D.Double(offsetWidth, xAxis, panelWidth, Y_AXIS - xAxis);
                    g2.setColor(GraphColors.inColors[2]);
                    g2.fill(pos);
                    g2.setColor(GraphColors.outColors[2]);
                    g2.fill(neg);
                }
                g2.setColor(Color.BLACK);
            }
        }
        private void drawDays(){
            if(dayBalValid){
                boolean stringUp = false;
                int x = OFFSET_WIDTH;
                for(int i = 0; i < numberOfDays; i++){
                    String stringDate = dayList.get(i);
                    int y = Y_AXIS + DAY_STRING_HEIGHT;
                    if(stringUp){
                        y += DAY_STRING_HEIGHT;
                    }
                    g2.drawString(stringDate, x, y);
                    stringUp = !stringUp;
                    x += DAY_WIDTH;
                }
            }
        }
        private void drawDayRectangle(){
            if(dayBalValid){
                for(Rectangle2D r : dayRectangleList){
                    g2.setColor(Color.BLACK);
                    g2.draw(r);
                }
            }
        }
        private void drawPath(){
            if(dayBalValid){
                g2.setStroke(new BasicStroke(3.0f));
                g2.setColor(GraphColors.BALANCE_COLOR);
                g2.draw(balancePath);
            }
        }
        private void drawCircles(){
            if(dayBalValid){
                for(Ellipse2D e : circleList){
                    g2.setColor(GraphColors.BALANCE_COLOR);
                    g2.fill(e);
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.setColor(Color.BLACK);
                    g2.draw(e);
                }
            }
        }
        private void highlightCircle(){
            if(dayBalValid && dayBalanceIndex != -1){
                Ellipse2D e = circleList.get(dayBalanceIndex);
                g2.setColor(Color.WHITE);
                g2.fill(e);
                g2.setStroke(new BasicStroke(1.0f));
                g2.setColor(Color.BLACK);
                g2.draw(e);
            }
        }
        private double getPercent(double value, double maxVal){
            return ((value * 100) / maxVal);
        }
        //MouseListener Interface
        public void mouseClicked(MouseEvent e){}
        public void mousePressed(MouseEvent e){}
        public void mouseReleased(MouseEvent e){
            Point2D p = new Point2D.Double(e.getX(), e.getY());
            for(int i = 0; i < circleList.size(); i++){
                Ellipse2D tmp = circleList.get(i);
                if(tmp.contains(p)){
                    setDayBalanceIndex(i);
                    repaint();
                    break;
                }
            }
            detailPane.updateFields();
        }
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
        //KeyListener Interface
        public void keyTyped(KeyEvent e){}
        public void keyPressed(KeyEvent e){}
        public void keyReleased(KeyEvent e){
            boolean isPressed = false;
            if(e.getKeyCode() == KeyEvent.VK_A){
                decDayBalanceIndex();
                isPressed = true;
            }else if(e.getKeyCode() == KeyEvent.VK_S){
                incDayBalanceIndex();
                isPressed = true;
            }
            if(isPressed){
                Rectangle r = dayRectangleList.get(dayBalanceIndex).getBounds();
                scrollRectToVisible(r);
                revalidate();
                repaint();
                detailPane.updateFields();
            }
        }
    }//InnerPane
    private class CommandPane extends JPanel implements PropertyChangeListener{
        private JLabel fromLabel;
        private JLabel toLabel;
        private JDateChooser fromDateChooser;
        private JDateChooser toDateChooser;

        public CommandPane(){
            fromLabel = new JLabel("From date:");
            fromDateChooser = new JDateChooser();
            fromDateChooser.setCalendar(innerPane.getFromDate());
            fromDateChooser.addPropertyChangeListener(this);
            toLabel = new JLabel("To date:");
            toDateChooser = new JDateChooser();
            toDateChooser.setCalendar(innerPane.getToDate());
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
                    innerPane.setFromDate(newDate);
                }
                if(evt.getSource() == toDateChooser){
                    innerPane.setToDate(newDate);
                }
                innerPane.updatePane();
            }
        }
    }//CommandPane
    private class DetailPane extends JPanel {

        private JPanel pane;
        private JLabel dateLabel;
        private JLabel dateField;
        private JLabel dayIncomeLabel;
        private JLabel dayIncomeField;
        private JLabel dayOutcomeLabel;
        private JLabel dayOutcomeField;
        private JLabel dayBalanceLabel;
        private JLabel dayBalanceField;
        private JLabel totalBalanceLabel;
        private JLabel totalBalanceField;

        private DetailPane(){
            super();
            setBorder(new TitledBorder("DetailPane"));
            initLabels();
            initFields();
            initLayout();
        }
        private void initLabels(){
            dateLabel = new JLabel("Date: ");
            dayIncomeLabel = new JLabel("Day Income: ");
            dayOutcomeLabel = new JLabel("Day Outcome: ");
            dayBalanceLabel = new JLabel("Day Balance: ");
            totalBalanceLabel = new JLabel("Total Balance: ");
        }
        private void initFields(){
            dateField = new JLabel("");
            dayIncomeField = new JLabel("");
            dayOutcomeField = new JLabel("");
            dayBalanceField = new JLabel("");
            totalBalanceField = new JLabel("");
        }
        private void initLayout(){
            pane = new JPanel();
            GroupLayout layout = new GroupLayout(pane);
            pane.setLayout(layout);
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(dateLabel)
                    .addComponent(dayIncomeLabel)
                    .addComponent(dayOutcomeLabel)
                    .addComponent(dayBalanceLabel)
                    .addComponent(totalBalanceLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(dateField)
                    .addComponent(dayIncomeField)
                    .addComponent(dayOutcomeField)
                    .addComponent(dayBalanceField)
                    .addComponent(totalBalanceField)));
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dateLabel)
                    .addComponent(dateField))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dayIncomeLabel)
                    .addComponent(dayIncomeField))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dayOutcomeLabel)
                    .addComponent(dayOutcomeField))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dayBalanceLabel)
                    .addComponent(dayBalanceField))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(totalBalanceLabel)
                    .addComponent(totalBalanceField)));
            add(pane);
        }
        private void updateFields(){
            if(innerPane.dayBalanceIndex != -1){
                dateField.setText(innerPane.dayList.get(innerPane.dayBalanceIndex));
                double in = innerPane.dayReportList.get(innerPane.dayBalanceIndex).getIncome();
                dayIncomeField.setText("" + in);
                double out = innerPane.dayReportList.get(innerPane.dayBalanceIndex).getOutcome();
                dayOutcomeField.setText("" + out);
                dayBalanceField.setText("" + (in - out));
                double total = innerPane.dayBalanceList.get(innerPane.dayBalanceIndex).getBalance();
                totalBalanceField.setText("" + total);
            }else{
                dateField.setText("");
                dayIncomeField.setText("");
                dayOutcomeField.setText("");
                dayBalanceField.setText("");
                totalBalanceField.setText("");
            }
        }
    }
}//BalanceChart
