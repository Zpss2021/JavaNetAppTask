// S3_1-局域网扫描器
// 张起硕-2125060196
// 2023-03-01

package S3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

public class S3_1 {
    public static void main(String[] args) throws Exception {
        LANScannerWindow window = new LANScannerWindow();
        window.showWindow();
    }
}

class LANInterface {
    NetworkInterface network;
    InetAddress localIPAddr;
    String lanHostAddr;
    String localHostName;
    String localMACAddr;

    public LANInterface() {
        try {
            this.initLANInterface(LANInterface.tryGetRealLANAddress());
        } catch (UnknownHostException | SocketException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "初始化", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public LANInterface(String localIP) {
        try {
            this.initLANInterface(localIP);
        } catch (UnknownHostException | SocketException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "初始化", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initLANInterface(String localIP) throws UnknownHostException, SocketException {
        localIPAddr = InetAddress.getByName(localIP);
        localHostName = localIPAddr.getHostName();
        network = NetworkInterface.getByInetAddress(localIPAddr);
        byte[] mac_bytes = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac_bytes.length; i++) {
            if (i != 0)
                sb.append(":");
            String s = Integer.toHexString(mac_bytes[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        localMACAddr = sb.toString().toUpperCase();
        String lanHost = localIPAddr.getHostAddress();
        int m = 0, n = 0;
        sb = new StringBuilder();
        while (n != 3) {
            sb.append(lanHost.charAt(m++));
            if (lanHost.charAt(m) == '.')
                n++;
        }
        lanHostAddr = sb.toString();
    }

    public static String tryGetRealLANAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isLoopback() && ni.isUp() && !ni.isVirtual()) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address && !addr.isLinkLocalAddress()) {
                            String displayName = NetworkInterface.getByInetAddress(addr).getDisplayName().toLowerCase();
                            if (!displayName.contains("virtual") && !displayName.contains("vmnet") && !displayName.contains("vbox")
                                    && !displayName.contains("docker") && !displayName.contains("vethernet")) {
                                System.out.println("[DEBUG]Real Local IP Address: " + addr.getHostAddress());
                                return addr.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "初始化", JOptionPane.INFORMATION_MESSAGE);
            System.exit(-1);
        }
        return null;
    }
}

class LANScanner implements Runnable {
    InetAddress host;
    final LANScannerWindowController ctrl;

    public LANScanner(InetAddress hostAddress, LANScannerWindowController ctrl) {
        this.ctrl = ctrl;
        this.host = hostAddress;
    }

    public boolean isHostReachable(InetAddress hostAddress) throws IOException {
        return hostAddress.isReachable(2000);
    }

    @Override
    public void run() {
        try {
            if (isHostReachable(host)) {
                System.out.println("[DEBUG]" + host.getHostName() + " " + host.getHostAddress() + " , creat & finish : " + ctrl.createdThreadsCounter + " " + ctrl.finishedThreadsCounter);
                ctrl.addHost(host);
            }
        } catch (IOException e) {
            System.out.println("[DEBUG]" + e.getMessage());
        } finally {
            ctrl.addFinishedThreadsCounter();
            if (ctrl.createdThreadsCounter == ctrl.end - ctrl.start)
                if (ctrl.finishedThreadsCounter == ctrl.end - ctrl.start) {
                    ctrl.win.progressBar.setValue(100);
                    JOptionPane.showMessageDialog(null, "扫描完成！",
                            "扫描", JOptionPane.INFORMATION_MESSAGE);
                    ctrl.win.progressBar.setValue(0);
                    ctrl.win.scanStatusLbl.setText("就绪");
                    ctrl.win.startBtn.setText("高速扫描");
                }
        }
    }
}

class LANScannerWindow extends JFrame {
    LANScannerWindowController controller;
    LANInterface lan;
    JPanel infoPane, localInfoPane, destInfoPane, progressPane;
    JScrollPane hostsTblPane;
    JLabel localIPLbl, localNameLbl, localMACLbl, beginIPLbl, endIPLbl, scanStatusLbl;
    JTextField beginIPText, endIPText, beginHostText, endHostText;
    JButton startBtn;
    JProgressBar progressBar;
    JTable hostsTbl;
    Vector<String> hostsTblColName;
    Vector<Vector<String>> hostsTblInfo;
    HostsTableModel hostsTblModel;

    public LANScannerWindow() {
        controller = new LANScannerWindowController();
        infoPane = new JPanel();
        localInfoPane = new JPanel();
        destInfoPane = new JPanel();
        progressPane = new JPanel();
        localIPLbl = new JLabel("本机IP：000.000.000.000");
        localNameLbl = new JLabel("本机名称：DESKTOP-0000");
        localMACLbl = new JLabel("本机MAC地址：00:00:00:00:00:00");
        beginIPLbl = new JLabel("扫描开始IP");
        endIPLbl = new JLabel("扫描终止IP");
        scanStatusLbl = new JLabel("初始化中...");
        beginIPText = new JTextField("0.0.0", 8);
        endIPText = new JTextField("0.0.0", 8);
        beginHostText = new JTextField(2);
        endHostText = new JTextField(2);
        startBtn = new JButton("高速扫描");
        progressBar = new JProgressBar();
        hostsTbl = new JTable();
        hostsTblColName = new Vector<>();
        hostsTblInfo = new Vector<>();
        hostsTblColName.add("IP");
        hostsTblColName.add("机器名");
        hostsTblModel = new HostsTableModel(hostsTblInfo, hostsTblColName);
        hostsTbl.setModel(hostsTblModel);
        initWindow();
        controller.setView(this);
        startBtn.addActionListener(controller);
        this.setTitle("局域网扫描器");
        this.setBounds(640, 340, 640, 400);
        this.setMinimumSize(new Dimension(600, 240));
        this.setMaximumSize(new Dimension(1080, 1080));
        this.add(infoPane, BorderLayout.NORTH);
        this.add(hostsTblPane, BorderLayout.CENTER);
        this.add(progressPane, BorderLayout.SOUTH);
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
        hostsTblPane = new JScrollPane(hostsTbl);
        hostsTblPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        infoPane.setPreferredSize(new Dimension(600, 80));
        progressPane.setPreferredSize(new Dimension(600, 30));
        localIPLbl.setPreferredSize(new Dimension(165, 20));
        localNameLbl.setPreferredSize(new Dimension(180, 20));
        startBtn.setPreferredSize(new Dimension(135, 30));
        scanStatusLbl.setPreferredSize(new Dimension(60, 20));
        progressBar.setPreferredSize(new Dimension(480, 20));

        localInfoPane.add(localIPLbl);
        localInfoPane.add(localNameLbl);
        localInfoPane.add(localMACLbl);
        destInfoPane.add(beginIPLbl);
        destInfoPane.add(beginIPText);
        destInfoPane.add(new JLabel("."));
        destInfoPane.add(beginHostText);
        destInfoPane.add(endIPLbl);
        destInfoPane.add(endIPText);
        destInfoPane.add(new JLabel("."));
        destInfoPane.add(endHostText);
        destInfoPane.add(startBtn);
        progressPane.add(scanStatusLbl);
        progressPane.add(progressBar);
        infoPane.add(localInfoPane);
        infoPane.add(destInfoPane);

        beginIPText.setEditable(false);
        endIPText.setEditable(false);
        hostsTblPane.setEnabled(false);
    }

    private void initLANInterface() {
        lan = new LANInterface();
        String lanHost = lan.localIPAddr.getHostAddress();
        localIPLbl.setText("本机IP：" + lanHost);
        localNameLbl.setText("本机名称：" + lan.localHostName);
        localMACLbl.setText("本机MAC地址：" + lan.localMACAddr);
        scanStatusLbl.setText("就绪");
        beginIPText.setText(lan.lanHostAddr);
        endIPText.setText(lan.lanHostAddr);
        beginHostText.setText("1");
        endHostText.setText("255");
    }

    public void showWindow() {
        this.initLANInterface();
        this.setVisible(true);
        this.validate();
    }
}

class LANScannerWindowController implements ActionListener {
    LANScannerWindow win;
    int start;
    int end;
    int createdThreadsCounter;
    int finishedThreadsCounter;

    public void setView(LANScannerWindow view) {
        this.win = view;
    }

    private static byte[] addrWithMask(byte[] addr, int[] mask) {
        int[] trans = new int[4];
        for (int i = 0; i < 4; i++)
            trans[i] = addr[i] & mask[i];
        return intsToBytes(trans);
    }

    private static int[] bytesToInts(byte[] bytes) {
        int[] ints = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            ints[i] = bytes[i] & 0xFF;
        return ints;
    }

    private static byte[] intsToBytes(int[] ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; i++)
            bytes[i] = (byte) ints[i];
        return bytes;
    }

    private void addCreatedThreadsCounter() {
        createdThreadsCounter++;
    }

    public void addFinishedThreadsCounter() {
        finishedThreadsCounter++;
        switch (finishedThreadsCounter % 4) {
            case 0 -> win.scanStatusLbl.setText("扫描中");
            case 1 -> win.scanStatusLbl.setText("扫描中.");
            case 2 -> win.scanStatusLbl.setText("扫描中..");
            case 3 -> win.scanStatusLbl.setText("扫描中...");
        }
    }

    public void scan() {
        int[] subNetMask = {255, 255, 255, 0};
        int[] lanAddr = bytesToInts(addrWithMask(win.lan.localIPAddr.getAddress(), subNetMask));
        try {
            createdThreadsCounter = finishedThreadsCounter = 0;
            for (int i = start; i < end; i++) {
                lanAddr[3] = i;
                InetAddress inetAddress = InetAddress.getByAddress(intsToBytes(lanAddr));
                addCreatedThreadsCounter();
                new Thread(new LANScanner(inetAddress, this)).start();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "扫描", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public synchronized void addHost(InetAddress host) {
        Vector<String> vec_info = new Vector<>();
        vec_info.add(host.getHostAddress());
        vec_info.add(host.getHostName());
        win.hostsTblModel.addRow(vec_info);
        win.hostsTbl.setModel(win.hostsTblModel);
        win.progressBar.setValue(getProgress());
    }

    private int getProgress() {
        return (int) Math.ceil((finishedThreadsCounter * 0.8 + createdThreadsCounter * 0.2) / (end - start + 1) * 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        win.startBtn.setText("扫描中");
        win.scanStatusLbl.setText("扫描中...");
        win.progressBar.setValue(0);
        win.hostsTblInfo.clear();
        win.hostsTblModel = new HostsTableModel(win.hostsTblInfo, win.hostsTblColName);
        win.hostsTbl.setModel(win.hostsTblModel);
        start = Integer.parseInt(win.beginHostText.getText());
        end = Integer.parseInt(win.endHostText.getText());
        this.scan();
    }
}

class HostsTableModel extends DefaultTableModel {
    public HostsTableModel(Vector<? extends Vector> data, Vector<?> columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}