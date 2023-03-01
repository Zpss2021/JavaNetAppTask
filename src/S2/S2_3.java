// S2_3-时间窗口
// 张起硕-2125060196-版权所有
// 2023-02-24

package S2;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class S2_3 {
    public static void main(String[] args) {
        TimeShowerWindow window = new TimeShowerWindow();
        window.showWindow();
    }
}

class TimeShowerWindow extends JFrame {
    TimeShowerWindowController controller;

    JPanel timePane, btnPane;

    JLabel timeLbl;

    JButton startBtn, stopBtn;

    public TimeShowerWindow() {
        controller = new TimeShowerWindowController();
        timePane = new JPanel();
        btnPane = new JPanel();
        timeLbl = new JLabel("00:00:00");
        startBtn = new JButton("开始计时");
        stopBtn = new JButton("停止计时");
        initWindow();
        this.add(timePane, BorderLayout.CENTER);
        this.add(btnPane, BorderLayout.SOUTH);
        controller.setView(this);
        startBtn.addActionListener(controller);
        stopBtn.addActionListener(controller);
        this.setTitle("时间");
        this.setBounds(320, 240, 400, 200);
        this.setMinimumSize(new Dimension(400, 200));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void initWindow() {
        timePane.setLayout(new FlowLayout());
        btnPane.setLayout(new FlowLayout());
        timePane.setBorder(new TitledBorder(null, "当前时间", TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION, null, null));
        timePane.setPreferredSize(new Dimension(400, 150));
        btnPane.setPreferredSize(new Dimension(200, 50));
        timePane.add(timeLbl);
        btnPane.add(startBtn);
        btnPane.add(stopBtn);
        timeLbl.setFont(new Font("宋体", Font.BOLD, 72));
    }

    public void showWindow() {
        this.setVisible(true);
        this.validate();
    }
}

class Timer implements Runnable {
    private TimeShowerWindow w;

    private boolean timerContinue;

    public void setView(TimeShowerWindow view) {
        this.w = view;
    }

    private void timeFlusher() throws InterruptedException {
        while (timerContinue) {
            w.timeLbl.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            Thread.sleep(1000);
        }
    }

    @Override
    public void run() {
        this.timerContinue = true;
        try {
            this.timeFlusher();
        } catch (InterruptedException e) {
            System.out.println("[DEBUG]" + e);
            this.timerContinue = false;
        }
    }
}

class TimeShowerWindowController implements ActionListener {
    private TimeShowerWindow w;
    private final Timer timer;
    private Thread timerThread;

    public TimeShowerWindowController() {
        timer = new Timer();
    }

    public void setView(TimeShowerWindow view) {
        this.w = view;
        timer.setView(view);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == w.startBtn) {
            timerThread = new Thread(timer);
            timerThread.start();
        } else if (e.getSource() == w.stopBtn) {
            timerThread.interrupt();
        }
    }
}