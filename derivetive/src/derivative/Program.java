
package derivative;

import javax.swing.*;
import java.awt.*;

public class Program {

    /*This method creates frame which can process
    * a simple analytical function and
    * a function, which is set in table form.
    * An analytical function must be set without parameters, only with one variable -- x.
     * Also analytical form computes function`s derivative
     * and draws a graphic with function and derivative.
    * */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Derivative");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            Draw analit=new AnalyticalForm();
            Draw table = new TableForm();

            JPanel panel = new JPanel();
            JRadioButton btn1 = new JRadioButton("Analytical Form");
            JRadioButton btn2 = new JRadioButton("Table Form");
            ButtonGroup btn_group = new ButtonGroup();
            btn1.setSelected(true);
            btn_group.add(btn1);
            btn_group.add(btn2);

            JLabel label = new JLabel("Choose the form: ");
            panel.add(label);
            panel.add(btn1);
            panel.add(btn2);
            frame.add(panel, BorderLayout.NORTH);
            frame.add(analit);

            btn1.addActionListener(event -> {
                frame.remove(table);
                analit.setVisible(true);
                frame.repaint();
            });

            btn2.addActionListener(event -> {
                analit.setVisible(false);
                frame.add(table);
                frame.repaint();
            });
            String plaf = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
            try {
                UIManager.setLookAndFeel(plaf);
                SwingUtilities.updateComponentTreeUI(frame);
            } catch (Exception e) { e.printStackTrace (); }
            frame.setSize(720, 480);
            frame.setVisible(true);
        });
    }
}
