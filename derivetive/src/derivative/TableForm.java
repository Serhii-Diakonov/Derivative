package derivative;

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
import java.util.*;

/*
 * Panel for a function in table form.
 * Also results of user`s input can be saved to the file
 * and read from it.
 * To remove the point you must enter its values in appropriate fields
 * and click on 'Remove' button
 * To modify the point you must enter fill 'x:' field with appropriate value
 * and enter a new value to 'y:' field
 * */

public class TableForm extends Draw {

    private ArrayList<Point> userInput = new ArrayList<>();
    private ArrayList<Point> xyData;
    private ArrayList<Point> xyDerivData;
    private double precision = 0.1;
    private static final Interpolator interpolator = new Interpolator();
    private ChartPanel chart_panel;
    private TreeMap<Double, Double> map = new TreeMap<>();
    private TreeSet<Point> set = new TreeSet<>();


    public static void main(String[] args) {

        /*
         * Here is illustration of computing
         * function and its derivative using 3 points.
         * It uses console for showing results
         *
         * */
        TableForm table = new TableForm();
        table.precision = 0.1;
        table.userInput.add(new Point(5, 25));
        table.userInput.add(new Point(0, 0));
        table.userInput.add(new Point(-5, 25));
        System.out.println("Input:");
        for (Point p : table.userInput) System.out.println(p.toString());
        table.userInput.sort(null);
        table.xyData = new ArrayList<>();
        for (double i = table.userInput.get(0).getX();
             i <= table.userInput.get(table.userInput.size() - 1).getX(); i += table.precision) {
            Point p = new Point(i, interpolator.interpolate(table.userInput, i));
            table.xyData.add(p);
        }
        table.xyData.sort(null);
        System.out.println("\n\nYour function:");
        for (Point p : table.xyData) System.out.println(p.toString());
        System.out.println("\n\nIts derivative:");
        table.xyDerivData = new ArrayList<>();
        for (int i = 1; i < table.xyData.size() - 1; i++) {
            Point p = new Point(table.xyData.get(i).getX(),
                    (table.xyData.get(i + 1).getY() - table.xyData.get(i - 1).getY()) / (2 * table.precision));
            table.xyDerivData.add(p);
        }
        for (Point p : table.xyDerivData) System.out.println(p.toString());
    }

    TableForm() {
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();

        p1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        p2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        p1.setBackground(Color.LIGHT_GRAY);
        p2.setBackground(Color.GRAY);

        JLabel x_lbl = new JLabel("x:");
        JLabel y_lbl = new JLabel("y:");
        JTextField x_inp = new JTextField(10);
        JTextField y_inp = new JTextField(10);
        x_inp.setMaximumSize(new Dimension(50, 20));
        y_inp.setMaximumSize(new Dimension(50, 20));
        JPanel var2 = new JPanel();
        var2.setBackground(Color.LIGHT_GRAY);
        var2.add(x_lbl);
        var2.add(x_inp);
        var2.add(y_lbl);
        var2.add(y_inp);
        var2.setMaximumSize(new Dimension(700, 30)); //actually for placing in center when full-screen
        // and reducing spacing between the next table

        JLabel table_lbl = new JLabel("Table:");
        table_lbl.setFont(new Font("Arial", Font.BOLD, 14));
        JTextPane table = new JTextPane();
        JScrollPane scroll = new JScrollPane(table);
        table.setDisabledTextColor(Color.BLACK);
        table.setEnabled(false);

        JLabel inp_label = new JLabel("Input area");
        inp_label.setFont(new Font("Arial", Font.BOLD, 14));
        JButton add_btn = new JButton("Add/Modify");
        JPanel var4 = new JPanel();
        var4.setBackground(Color.LIGHT_GRAY);
        GroupLayout l1 = new GroupLayout(var4);
        var4.setLayout(l1);
        l1.setAutoCreateGaps(true);
        l1.setAutoCreateContainerGaps(true);
        l1.setHorizontalGroup(l1.createParallelGroup(GroupLayout.Alignment.CENTER).
                addComponent(inp_label).
                addComponent(var2).
                addComponent(add_btn));
        l1.setVerticalGroup(l1.createSequentialGroup().
                addComponent(inp_label).
                addComponent(var2).
                addComponent(add_btn));

        JButton remove_btn = new JButton("Remove");
        JButton clear_table_btn = new JButton("Clear all");
        JButton read_btn = new JButton("Read from file");
        JButton write_btn = new JButton("Write to file");
        JButton clear_file_btn = new JButton("Clear file");
        write_btn.setEnabled(false);
        clear_table_btn.setEnabled(false);
        remove_btn.setEnabled(false);
        if (fileEmpty()) {
            clear_file_btn.setEnabled(false);
            read_btn.setEnabled(false);
        }

        chart_panel = new ChartPanel(createLineChart(), 340, 390,
                100, 100, 1024,
                780, true,
                true, false, true, true, true);

        GroupLayout l = new GroupLayout(p1);
        p1.setLayout(l);
        l.setAutoCreateGaps(true);
        l.setAutoCreateContainerGaps(true);
        l.setHorizontalGroup(l.createParallelGroup().
                addComponent(var4).
                addComponent(table_lbl).
                addComponent(scroll).
                addGroup(l.createSequentialGroup().
                        addComponent(remove_btn).
                        addComponent(clear_table_btn)).
                addGroup(l.createSequentialGroup().
                        addComponent(read_btn).
                        addComponent(write_btn).
                        addComponent(clear_file_btn)));
        l.setVerticalGroup(l.createSequentialGroup().
                addComponent(var4).
                addComponent(table_lbl).
                addComponent(scroll).
                addGroup(l.createParallelGroup().
                        addComponent(remove_btn).
                        addComponent(clear_table_btn)).
                addGroup(l.createParallelGroup().
                        addComponent(read_btn).
                        addComponent(write_btn).
                        addComponent(clear_file_btn)));
        p2.add(chart_panel);
        this.setLayout(new GridLayout(1, 2));
        this.add(p1);
        this.add(p2);

        add_btn.addActionListener(event -> {
            tryAddPoint(x_inp.getText(), y_inp.getText());
            table.setText(refreshTable());
            interpolate();
            computeDeriv();
            write_btn.setEnabled(true);
            clear_table_btn.setEnabled(true);
            remove_btn.setEnabled(true);
            chart_panel.setChart(createLineChart());
            chart_panel.repaint();
        });

        clear_table_btn.addActionListener(event -> {
            userInput = new ArrayList<>();
            xyDerivData = null;
            xyData = null;
            table.setText(refreshTable());
            x_inp.setText("");
            y_inp.setText("");
            chart_panel.setChart(createLineChart());
            chart_panel.repaint();
            write_btn.setEnabled(false);
            clear_table_btn.setEnabled(false);
            remove_btn.setEnabled(false);
        });

        write_btn.addActionListener(event -> {
            if (JOptionPane.showConfirmDialog
                    (this, "The file will be rewritten! Are you sure?") == 0) {
                int s = saveToFile();
                if (s == 0) {
                    read_btn.setEnabled(true);
                    clear_file_btn.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Saved successfully to 'Table_form.txt'!");
                } else JOptionPane.showMessageDialog(this, "Can`t save to the file!");
            }
        });

        read_btn.addActionListener(event -> {
            if (JOptionPane.showConfirmDialog
                    (this, "Reading will erase current data! Are you sure?") == 0) {
                int r = readFromFile();
                if (r != 0 || userInput.size() < 1)
                    JOptionPane.showMessageDialog(this, "Reading error or empty file!");
                else {
                    table.setText(refreshTable());
                    interpolate();
                    computeDeriv();
                    chart_panel.setChart(createLineChart());
                    chart_panel.repaint();
                    clear_table_btn.setEnabled(true);
                    write_btn.setEnabled(true);
                    remove_btn.setEnabled(true);
                }
            }
        });

        clear_file_btn.addActionListener(event -> {
            if (JOptionPane.showConfirmDialog
                    (this, "The file will be cleared! Are you sure?") == 0) {
                int c = clearFile();
                if (c == 0) {
                    read_btn.setEnabled(false);
                    clear_file_btn.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "The file is cleared!");
                } else JOptionPane.showMessageDialog(this, "Can`t clear the file!");
            }
        });

        remove_btn.addActionListener(event -> {
            try {
                if (JOptionPane.showConfirmDialog
                        (this, "Values will be removed! Are you sure?") == 0) {
                    int r = remove(Double.parseDouble(x_inp.getText()),
                            Double.parseDouble(y_inp.getText()));
                    if (r != 0) JOptionPane.showMessageDialog(this, "Can`t remove!");
                    else {
                        if (userInput.size() == 0) {
                            clear_table_btn.setEnabled(false);
                            write_btn.setEnabled(false);
                            remove_btn.setEnabled(false);
                        }
                        table.setText(refreshTable());
                        interpolate();
                        computeDeriv();
                        chart_panel.setChart(createLineChart());
                        chart_panel.repaint();
                        x_inp.setText("");
                        y_inp.setText("");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Check values!");
            }
        });

        x_inp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    y_inp.requestFocusInWindow();
                }
            }
        });

        y_inp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    add_btn.doClick();
                }
            }
        });

        String plaf = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
        try {
            UIManager.setLookAndFeel(plaf);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public XYDataset getXYDataset() {
        new XYSeries("");
        XYSeries func = new XYSeries("Function");
        XYSeries deriv = new XYSeries("Derivative");
        XYSeriesCollection dataset = new XYSeriesCollection();
        if (xyData != null && xyData.size() != 0)
            for (Point p : xyData)
                func.add(p.getX(), p.getY());
        if (xyDerivData != null && xyDerivData.size() != 0)
            for (Point p : xyDerivData)
                deriv.add(p.getX(), p.getY());
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

    private void addPoint(double x, double y) {
        for (Point p : userInput)
            if (p.getX() == x) {
                p.setY(y);
                return;
            }
        userInput.add(new Point(x, y));
            map.put(x,y);
            set.add(new Point(x,y));
    }

    public void tryAddPoint(String inp1, String inp2) {
        double x, y;
        try {
            x = Double.parseDouble(inp1);
            y = Double.parseDouble(inp2);
            addPoint(x, y);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Incorrect values!");
        }
    }

    public String refreshTable() {
        StringBuilder res = new StringBuilder();
        if (userInput.size() != 0) {
            userInput.sort(null);
            for (Point p : userInput) res.append(p.toString()).append("\n");
            return res.toString();
        }
        return "";
    }

    public void interpolate() {
        xyData = new ArrayList<>();
        if (userInput.size() > 1) {
            for (double i = userInput.get(0).getX();
                 i <= userInput.get(userInput.size() - 1).getX(); i += precision) {
                Point p = new Point(i, interpolator.interpolate(userInput, i));
                xyData.add(p);
            }
            xyData.sort(null);
        } else if (userInput.size() > 0)
            xyData.add(userInput.get(0));
    }

    public void computeDeriv() {
        xyDerivData = new ArrayList<>();
        if (xyData.size() >= 1) {
            for (int i = 1; i < xyData.size() - 1; i++) {
                Point p = new Point(xyData.get(i).getX(),
                        (xyData.get(i + 1).getY() - xyData.get(i - 1).getY()) / (2 * precision));
                xyDerivData.add(p);
            }
            xyDerivData.sort(null);
        }
    }

    public int saveToFile() {
        if (userInput != null && userInput.size() > 0) {
            try (BufferedWriter wr = new BufferedWriter(new FileWriter("Table_form.txt"))) {
                for (Point p : userInput) wr.write(p.toString() + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
            return 0;
        }
        return 1;
    }

    public int readFromFile() {
        userInput = new ArrayList<>();
        String buf;
        try (BufferedReader r = new BufferedReader(new FileReader("Table_form.txt"))) {
            while ((buf = r.readLine()) != null) {
                double x=Double.parseDouble(buf.substring(1, buf.indexOf(";")));
                double y=Double.parseDouble(buf.substring(buf.indexOf(" ") + 1, buf.indexOf("]")));
                userInput.add(new Point(x,y));
                set.add(new Point(x,y));
                map.put(x,y);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public int clearFile() {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter("Table_form.txt"))) {
            wr.write("");
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public boolean fileEmpty() {
        String buf;
        try (BufferedReader r = new BufferedReader(new FileReader("Table_form.txt"))) {
            buf = r.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return buf == null;
    }

    public int remove(double x, double y) {
        try {
            userInput.removeIf(p -> p.getX() == x && p.getY() == y);
            set.removeIf(p -> p.getX() == x && p.getY() == y);
            map.remove(x,y);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
