import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeShower {
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
                TitledBorder.DEFAULT_POSITION,null,null));
        timePane.setPreferredSize(new Dimension(400, 150));
        btnPane.setPreferredSize(new Dimension(200, 50));
        timePane.add(timeLbl);
        btnPane.add(startBtn);
        btnPane.add(stopBtn);
        timeLbl.setFont(new Font("宋体", Font.PLAIN, 72));
    }

    public void showWindow() {
        this.setVisible(true);
        this.validate();
    }
}

class Timer implements Runnable {
    private TimeShowerWindow w;
    public void setView(TimeShowerWindow view) {
        this.w = view;
    }
    private void timeFlusher() {
        w.timeLbl.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
    @Override
    public void run() {
        this.timeFlusher();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "错误", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

class TimeShowerWindowController implements ActionListener {
    private TimeShowerWindow w;
    private Timer timer;
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
        if(e.getSource() == w.startBtn) {
            timerThread = new Thread(timer);
            timerThread.start();
        }
        else if(e.getSource()==w.stopBtn){
            try {
                timerThread.wait();
            } catch (InterruptedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "错误", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}