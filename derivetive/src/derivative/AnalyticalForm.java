package derivative;

import edu.hws.jcm.data.Cases;
import edu.hws.jcm.data.ParseError;
import edu.hws.jcm.data.StackOfDouble;
import edu.hws.jcm.functions.ExpressionFunction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

/*Panel for a function in analytical form.
 * An analytical function must be set without parameters, only with one variable -- x.
 * Also analytical form computes function`s derivative
 * and draws a graphic with function and derivative.
 * */

public class AnalyticalForm extends Draw {

    private double start, end, step;
    private ArrayList<Double> y, y_deriv;
    private Stack<Double> ar;
    private StackOfDouble stack;
    private static Cases cases=new Cases();
    private boolean hasParam = false;
    private ExpressionFunction expr, expr1;
    private JTextPane txt_area;
    private String str = "";
    private ChartPanel chart_panel;

    AnalyticalForm() {
        y = new ArrayList<>();
        y_deriv = new ArrayList<>();
        ar = new Stack<>();
        stack = new StackOfDouble();
        txt_area = new JTextPane();
        GridLayout layout = new GridLayout(1, 2);
        this.setLayout(layout);
        JScrollPane scroll = new JScrollPane(txt_area);
        txt_area.setDisabledTextColor(Color.BLACK);
        txt_area.setEnabled(false);

        JButton computeButton = new JButton("Compute");
        JButton clearButton = new JButton("Clear");
        JButton clearFile = new JButton("Clear file");
        JButton writeFile = new JButton("Write to file");
        writeFile.setEnabled(false);
        clearButton.setEnabled(false);
        clearFile.setEnabled(false);
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        p1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        p2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        p1.setBackground(Color.LIGHT_GRAY);

        JLabel func = new JLabel("f(x):");
        JLabel start_label = new JLabel("start:");
        JLabel stop_label = new JLabel("stop:");
        JLabel step_label = new JLabel("step:");
        JLabel result = new JLabel("Result:");
        result.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField expr = new JTextField();
        JTextField step = new JTextField();
        JTextField start = new JTextField();
        JTextField end = new JTextField();

        p2.setBackground(Color.GRAY);
        chart_panel = new ChartPanel(createLineChart(), 340, 390,
                100, 100, 1024,
                780, true,
                true, false, true, true, true);
        p2.add(chart_panel);
        this.add(p1);
        this.add(p2);

        expr.setMaximumSize(new Dimension(300, 20));
        start.setMaximumSize(new Dimension(50, 20));
        end.setMaximumSize(new Dimension(50, 20));
        step.setMaximumSize(new Dimension(50, 20));
        
        GroupLayout layout1 = new GroupLayout(p1);
        p1.setLayout(layout1);
        layout1.setAutoCreateGaps(true);
        layout1.setAutoCreateContainerGaps(true);
        layout1.setHorizontalGroup(layout1.createParallelGroup().
                addGroup(layout1.createSequentialGroup().
                        addComponent(func).
                        addComponent(expr)).
                addGroup(layout1.createSequentialGroup().
                        addComponent(start_label).
                        addComponent(start).
                        addComponent(stop_label).
                        addGroup(layout1.createParallelGroup().
                                addComponent(end).
                                addComponent(computeButton)).
                        addComponent(step_label).
                        addComponent(step)).
                addGroup(layout1.createParallelGroup().
                        addComponent(result).
                        addComponent(scroll)).
                addGroup(layout1.createSequentialGroup().
                        addComponent(clearButton).
                        addComponent(clearFile).
                        addComponent(writeFile)));

        layout1.setVerticalGroup(layout1.createSequentialGroup().
                addGroup(layout1.createParallelGroup().
                        addComponent(func).
                        addComponent(expr)).
                addGroup(layout1.createParallelGroup().
                        addComponent(start_label).
                        addComponent(start).
                        addComponent(stop_label).
                        addComponent(end).
                        addComponent(step_label).
                        addComponent(step)).
                addComponent(computeButton).
                addComponent(result).
                addComponent(scroll).
                addGroup(layout1.createParallelGroup().
                addComponent(clearButton).
                addComponent(clearFile).
                        addComponent(writeFile)));

        computeButton.addActionListener(event -> {
            String formula;
            double start_value, stop_value, step_value;
            if ((formula = prepareStr(expr.getText(), this)) != null) {
                if (checkValues(start.getText(), end.getText(), step.getText(), this)) {
                    start_value = cleanStrToDouble(start.getText());
                    stop_value = cleanStrToDouble(end.getText());
                    step_value = cleanStrToDouble(step.getText());
                    if (start_value < stop_value && step_value > 0) {
                        setFormula(formula);
                        setStart(start_value);
                        setEnd(stop_value);
                        setStep(step_value);
                        print();
                        writeFile.setEnabled(true);
                        clearButton.setEnabled(true);
                        chart_panel.setChart(createLineChart());
                        chart_panel.repaint();
                    } else JOptionPane.showMessageDialog(this, """
                            'stop' must be greater than 'start'
                            and 'step' must be greater than 0!
                             Try again""");
                }
            }
        });

        clearButton.addActionListener(event -> {
            if (y.size() != 0 && y_deriv.size() != 0) {
                y.clear();
                y_deriv.clear();
                chart_panel.setChart(createLineChart());
                chart_panel.repaint();
                str = "";
                txt_area.setText(str);
                step.setText("");
                start.setText("");
                end.setText("");
                expr.setText("");
                writeFile.setEnabled(false);
                clearButton.setEnabled(false);
            }
        });

        clearFile.addActionListener(event ->{
            if(JOptionPane.showConfirmDialog
                    (this,"The file will be cleared! Are you sure?")==0){
                if(clearFile()!=0)
                    JOptionPane.showMessageDialog(this, "Can`t clear the file!");
                else {
                    clearFile.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Success!");
                }
            }
        });

        writeFile.addActionListener(event ->{
            if(JOptionPane.showConfirmDialog
                    (this,"Please, confirm adding to the file")==0){
                if(saveToFile()!=0)
                    JOptionPane.showMessageDialog(this, "Can`t save to the file!");
                else {
                    clearFile.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Saved successfully to 'Analytical_form.txt'!");
                }
            }
        });

        expr.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    start.requestFocusInWindow();
                }
            }
        });

        start.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    end.requestFocusInWindow();
                }
            }
        });

        end.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    step.requestFocusInWindow();
                }
            }
        });

        step.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    computeButton.doClick();
                }
            }
        });

        String plaf = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
        try {
            UIManager.setLookAndFeel(plaf);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) { e.printStackTrace (); }
    }

    public String getFunc() {
        return Optional.ofNullable(expr1.toString()).orElse("");
    }

    public String getDeriv() {
        return Optional.ofNullable(expr1.derivative(1).toString()).orElse("");
    }

    public String computeFunc() {
        if (hasParam) return computeFuncWithParam("a");
        return computeFuncWithoutParam();
    }

    public String computeDeriv() {
        if (hasParam) return computeDerivWithParam("a");
        return computeDerivWithoutParam();
    }

    private String computeFuncWithoutParam() {
        ArrayList<Double> res = new ArrayList<>();
        y.clear();
        for (double i = start; i <= end; i += step) stack.push(i);
        while (!stack.isEmpty()) {
            expr.apply(stack, cases);
            ar.push(stack.pop());
        }
        while (!ar.empty()) {
            double buffer = ar.pop();
            res.add(buffer);
            y.add(buffer);
        }
        return res.toString();
    }

    private String computeFuncWithParam(String param) {
        /*_________________!Test section!______________*/

        StringBuilder builder = new StringBuilder();
        for (double i = start; i <= end; i += step) stack.push(i);
        while (!stack.isEmpty()) {
            expr.apply(stack, cases);
            ar.push(stack.pop());
        }
        builder.append("[");
        while (!ar.empty()) builder.append(param + "*" + ar.pop() + "; ");
        builder.append("]");
        return builder.toString();
        /***********************************************/
    }

    private String computeDerivWithoutParam() {
        ArrayList<Double> res = new ArrayList<>();
        y_deriv.clear();
        String buf = expr.derivative(1).toString();
        String[] str = buf.split("by ");
        ExpressionFunction deriv = new ExpressionFunction(null, str[1]);
        for (double i = start; i <= end; i += step) stack.push(i);
        while (!stack.isEmpty()) {
            deriv.apply(stack, cases);
            ar.push(stack.pop());
        }
        while (!ar.empty()) {
            double buffer = ar.pop();
            res.add(buffer);
            y_deriv.add(buffer);
        }
        return res.toString();
    }

    //____________Test Section!___________________//
    private String computeDerivWithParam(String param) {
        StringBuilder builder = new StringBuilder();
        String buf = expr.derivative(1).toString();
        String[] str = buf.split("by ");
        ExpressionFunction deriv = new ExpressionFunction(null, str[1]);
        for (double i = start; i <= end; i += step) stack.push(i);
        while (!stack.isEmpty()) {
            deriv.apply(stack, cases);
            ar.push(stack.pop());
        }
        builder.append("[");
        while (!ar.empty()) builder.append(param + "*" + ar.pop() + "; ");
        builder.append("]");
        return builder.toString();
    }

    /***********************************************/

    public void setStart(double x) {
        start = x;
    }

    public void setStep(double x) {
        step = x;
    }

    public void setEnd(double x) {
        end = x;
    }

    public void setFormula(String str_func) {
        if (str_func.contains("a")) hasParam = true;
        if (!hasParam) {
            expr1 = new ExpressionFunction("f", str_func);
            expr = new ExpressionFunction("f", str_func);
        } else {
            String[] params = {"x", "a"};
            expr1 = new ExpressionFunction("f", params, str_func, null);
            str_func = str_func.replace("a", "1");
            expr = new ExpressionFunction("f", str_func);
        }
    }

    public void print() {
        str += getFunc() + "\n" +
                computeFunc() + "\n\n" +
                getDeriv() + '\n' +
                computeDeriv() + "\n\n\n";
        txt_area.setText(str);
    }

    public int saveToFile(){
        String content=null, line;
        try{
            if(getFunc().isEmpty())
                return 1;
        } catch (NullPointerException e){
            return 1;
        }
        try(BufferedReader r= new BufferedReader(new FileReader("Analytical_form.txt"))){
            while((line=r.readLine())!=null)
                content+=line;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
        try(BufferedWriter wr=new BufferedWriter(new FileWriter("Analytical_form.txt"))){
            if(content==null){
                wr.write(getFunc() + "\n" +
                        computeFunc() + "\n\n" +
                        getDeriv() + '\n' +
                        computeDeriv() + "\n\n\n");
            } else wr.write(content+ getFunc() + "\n" +
                    computeFunc() + "\n\n" +
                    getDeriv() + '\n' +
                    computeDeriv() + "\n\n\n");
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public int clearFile(){
        try(BufferedWriter wr=new BufferedWriter(new FileWriter("Analytical_form.txt"))){
           wr.write("");
           return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }

    private String prepareStr(String formula, JPanel frame) {
        if (formula.contains(",")) formula = formula.replace(",", ".");
        try {
            new ExpressionFunction("f", formula);
            return formula;
        } catch (ParseError e) {
            JOptionPane.showMessageDialog(frame, "There was a mistake in the formula!\n Try again");
        }
        return null;
    }

    private boolean checkValues(String v1, String v2, String v3, JPanel frame) {
        if (v1.contains(",")) v1 = v1.replace(",", ".");
        if (v2.contains(",")) v2 = v2.replace(",", ".");
        if (v3.contains(",")) v3 = v3.replace(",", ".");
        try {
            Double.parseDouble(v1);
            Double.parseDouble(v2);
            Double.parseDouble(v3);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Incorrect value(s)!\n Check values and try again");
        }
        return false;
    }

    private double cleanStrToDouble(String s) {
        if (s.contains(",")) s = s.replace(",", ".");
        return Double.parseDouble(s);
    }

    public XYDataset getXYDataset() {
        new XYSeries("");
        XYSeries func = new XYSeries("Function");
        XYSeries deriv = new XYSeries("Derivative");
        XYSeriesCollection dataset = new XYSeriesCollection();
        int i = 0;
        if (y.size() != 0 && y_deriv.size() != 0) {
            for (double x = start; x <= end; x += step) {
                func.add(x, y.get(i));
                deriv.add(x, y_deriv.get(i));
                i++;
            }
        }
        dataset.addSeries(deriv);
        dataset.addSeries(func);
        return dataset;
    }

    private JFreeChart createLineChart() {
        XYDataset dataset = getXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Graphic",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundAlpha(0.2f);

        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);

        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLineVisible(false);
        axis = plot.getDomainAxis();
        axis.setAxisLineVisible(false);

        plot.setAxisOffset(new RectangleInsets(1.0, 1.0, 1.0, 1.0));

        return chart;
    }
}
