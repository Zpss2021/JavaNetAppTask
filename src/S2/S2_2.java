// S2_2-文件拷贝
// 张起硕-2125060196-版权所有
// 2023-02-22

package S2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class S2_2 {
    public static void main(String[] args) {
        FileCopyWindow window = new FileCopyWindow();
        window.showWindow();
    }
}

class FileCopyWindow extends JFrame {
    FileCopyWindowController controller;
    JLabel srcLbl, destLbl;
    JTextField srcText, destText;
    JProgressBar progressBar;
    JButton startBtn;

    public FileCopyWindow() {
        controller = new FileCopyWindowController();
        srcLbl = new JLabel("源文件路径：");
        destLbl = new JLabel("目标文件路径：");
        srcText = new JTextField();
        destText = new JTextField();
        progressBar = new JProgressBar();
        startBtn = new JButton("开始复制");
        initWindow();
        controller.setView(this);
        startBtn.addActionListener(controller);
        this.setTitle("文件复制");
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
        this.setLayout(null);
        this.add(srcLbl);
        this.add(srcText);
        this.add(destLbl);
        this.add(destText);
        this.add(progressBar);
        this.add(startBtn);
        srcLbl.setBounds(30, 25, 100, 15);
        srcText.setBounds(120, 20, 250, 25);
        destLbl.setBounds(30, 55, 100, 15);
        destText.setBounds(120, 50, 250, 25);
        progressBar.setBounds(35, 85, 320, 20);
        startBtn.setBounds(40, 120, 100, 20);
    }

    public void showWindow() {
        this.setVisible(true);
        this.validate();
    }
}

class FileCopyWindowController implements ActionListener {
    private FileCopyWindow w;
    private final FileCopyService s;

    public FileCopyWindowController() {
        s = new FileCopyService();
    }

    public void setView(FileCopyWindow view) {
        this.w = view;
        s.setView(w);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == w.startBtn) {
            s.setSourceFile(w.srcText.getText());
            s.setDestFile(w.destText.getText());
            new Thread(s).start();
        }
    }
}

class FileCopyService implements Runnable {
    private FileCopyWindow w;
    private String sourceFile;
    private String destFile;
    private final byte[] buffer;

    public FileCopyService() {
        buffer = new byte[1024];
    }

    public void setView(FileCopyWindow view) {
        this.w = view;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setDestFile(String destFile) {
        this.destFile = destFile;
    }

    public void copy() throws IOException {
        int len = 0, written = 0;
        long size = new File(sourceFile).length();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            written += len;
            w.progressBar.setValue((written / (int) (size / 100)));
        }
        JOptionPane.showMessageDialog(null, "复制完成",
                "复制", JOptionPane.INFORMATION_MESSAGE);
        in.close();
        out.close();
    }

    @Override
    public void run() {
        try {
            this.copy();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "错误", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}