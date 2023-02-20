// S1_5-简单计算器
// 张起硕-2125060196-版权所有
package S1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class S1_5 {
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.showWindow();
    }
}

class Calculation {
    double num1;
    double num2;
    String opt;
    public void setNum1(double num1) {
        this.num1 = num1;
    }
    public void setNum2(double num2) {
        this.num2 = num2;
    }
    public void setOpt(String opt) {
        this.opt = opt;
    }
    public String getAns() {
        return switch (opt) {
            case "+" -> Double.toString(num1 + num2);
            case "-" -> Double.toString(num1 - num2);
            case "*" -> Double.toString(num1 * num2);
            case "/" -> Double.toString(num1 / num2);
            default -> "NaN";
        };
    }
}

class MainWindow extends JFrame {
    Calculation calculation;
    MainWindowController controller;
    JLabel label1, label2, label3, label_result;
    JPanel mainPanel;
    JTextField num1, num2;
    JComboBox<String> opt;
    JButton calc;
    MainWindow() {
        calculation = new Calculation();
        controller = new MainWindowController();
        mainPanel = new JPanel();
        label1 = new JLabel("操作数1：");
        label2 = new JLabel("操作数2：");
        label3 = new JLabel("计算类型：");
        label_result = new JLabel("结果为：");
        num1 = new JTextField(10);
        num2 = new JTextField(10);
        opt = new JComboBox<>();
        calc = new JButton("计算");
        initPanel();
        controller.setView(this);
        calc.addActionListener(controller);
        this.setTitle("简单计算器");
        this.setBounds(300, 300, 300, 200);
        this.setResizable(false);
        this.add(mainPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void initPanel() {
        mainPanel.setLayout(null);
        opt.addItem("+");
        opt.addItem("-");
        opt.addItem("*");
        opt.addItem("/");
        label1.setBounds(50, 10, 100, 20);
        num1.setBounds(120, 10, 100, 20);
        label2.setBounds(50, 35, 100, 20);
        num2.setBounds(120, 35, 100, 20);
        label3.setBounds(50, 60, 100, 20);
        opt.setBounds(150, 60, 35, 20);
        calc.setBounds(50, 100, 75, 20);
        label_result.setBounds(150, 100, 100, 20);
        mainPanel.add(label1);
        mainPanel.add(num1);
        mainPanel.add(label2);
        mainPanel.add(num2);
        mainPanel.add(label3);
        mainPanel.add(opt);
        mainPanel.add(calc);
        mainPanel.add(label_result);
    }
    public void showWindow() {
        this.setVisible(true);
        this.validate();
    }
}

class MainWindowController implements ActionListener {
    MainWindow mainWindow;
    public void setView(MainWindow mainWindowView) {
        this.mainWindow = mainWindowView;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            mainWindow.calculation.setNum1(Double.parseDouble(mainWindow.num1.getText().trim()));
            mainWindow.calculation.setNum2(Double.parseDouble(mainWindow.num2.getText().trim()));
            mainWindow.calculation.setOpt((String) mainWindow.opt.getSelectedItem());
            String ans = mainWindow.calculation.getAns();
            mainWindow.setSize(300, 200);
            mainWindow.label_result.setSize(100, 20);
            mainWindow.label_result.setText("结果为：" + ans);
        } catch (Exception ex) {
            mainWindow.setSize(600, 200);
            mainWindow.label_result.setSize(500, 20);
            mainWindow.label_result.setText("[Exception]" + ex);
        }
    }
}