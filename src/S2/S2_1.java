// S2_1-聊天客户端
// 张起硕-2125060196-版权所有
// 2023-02-22

package S2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class S2_1 {
    public static void main(String[] args) {
        ChatClientWindow window = new ChatClientWindow();
        window.showWindow();
    }
}

class User {
    private final InetSocketAddress host;
    private final String username;

    public User(String address, int port, String username) {
        this.host = new InetSocketAddress(address, port);
        this.username = username;
    }

    public InetSocketAddress getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }
}

class UserService {
    private final List<User> users;

    public UserService() {
        users = new LinkedList<>();
    }

    public boolean addUser(User user) {
        for (User u : users)
            if (u.getUsername().equals(user.getUsername()))
                return false;
        users.add(user);
        return true;
    }

    public boolean delUser(String username) {
        Iterator<User> it = users.iterator();
        while (it.hasNext())
            if (it.next().getUsername().equals(username)) {
                it.remove();
                return true;
            }
        return false;
    }
}

class ChatClientWindow extends JFrame {
    ChatClientWindowController controller;
    User loginUser;
    UserService onlineUsers;
    JPanel cfgPane, sendPane;
    JScrollPane recordPane, userPane;
    JLabel hostIPLb, portLb, usernameLb;
    JButton loginBtn, logoutBtn, sendBtn;
    JTextArea chatRecordArea;
    JList<String> onlineUserList;
    DefaultListModel<String> onlineUserListModel;
    JTextField hostIPText, portText, usernameText, sendText;

    ChatClientWindow() {
        controller = new ChatClientWindowController();
        onlineUsers = new UserService();
        cfgPane = new JPanel();
        sendPane = new JPanel();
        hostIPLb = new JLabel("主机IP：");
        portLb = new JLabel("端口：");
        usernameLb = new JLabel("用户名：");
        hostIPText = new JTextField(9);
        portText = new JTextField(4);
        usernameText = new JTextField(8);
        sendText = new JTextField(40);
        loginBtn = new JButton("登录");
        logoutBtn = new JButton("退出");
        sendBtn = new JButton("发送");
        chatRecordArea = new JTextArea();
        onlineUserList = new JList<>();
        onlineUserListModel = new DefaultListModel<>();
        initWindow();
        controller.setView(this);
        loginBtn.addActionListener(controller);
        logoutBtn.addActionListener(controller);
        sendBtn.addActionListener(controller);
        sendText.addActionListener(controller);
        this.setTitle("聊天客户端");
        this.setBounds(320, 240, 640, 720);
        this.setMinimumSize(new Dimension(600, 300));
        this.add(cfgPane, BorderLayout.NORTH);
        this.add(recordPane, BorderLayout.CENTER);
        this.add(userPane, BorderLayout.EAST);
        this.add(sendPane, BorderLayout.SOUTH);
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
        recordPane = new JScrollPane(chatRecordArea);
        userPane = new JScrollPane(onlineUserList);

        cfgPane.setBorder(BorderFactory.createTitledBorder("配置"));
        recordPane.setBorder(BorderFactory.createTitledBorder("聊天记录"));
        userPane.setBorder(BorderFactory.createTitledBorder("在线用户"));
        sendPane.setBorder(BorderFactory.createTitledBorder("发送"));

        recordPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        userPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        recordPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        userPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        cfgPane.setPreferredSize(new Dimension(640, 72));
        recordPane.setPreferredSize(new Dimension(384, 576));
        userPane.setPreferredSize(new Dimension(256, 576));
        sendPane.setPreferredSize(new Dimension(640, 72));

        cfgPane.add(hostIPLb);
        cfgPane.add(hostIPText);
        cfgPane.add(portLb);
        cfgPane.add(portText);
        cfgPane.add(usernameLb);
        cfgPane.add(usernameText);
        cfgPane.add(loginBtn);
        cfgPane.add(logoutBtn);

        sendPane.add(sendText);
        sendPane.add(sendBtn);

        chatRecordArea.setEditable(false);
        sendText.setEditable(false);
        sendBtn.setEnabled(false);

        chatRecordArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        onlineUserList.setFont(new Font("微软雅黑", Font.BOLD, 20));

        onlineUserList.setModel(onlineUserListModel);
    }

    public void showWindow() {
        this.setVisible(true);
        this.validate();
    }
}

class ChatClientWindowController implements ActionListener {
    private ChatClientWindow w;

    public void setView(ChatClientWindow view) {
        this.w = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == w.sendText) {
            w.chatRecordArea.append("\n【" + w.loginUser.getUsername() + "】" +
                    new SimpleDateFormat("yyyy-MM-dd EEEE HH:mm:ss").format(new Date())
                    + "\n " + w.sendText.getText() + "\n");
            w.sendText.setText("");
        }
        else
            switch (e.getActionCommand()) {
                case "登录" -> {
                    String hostIP = w.hostIPText.getText();
                    String hostPort = w.portText.getText();
                    String username = w.usernameText.getText();
                    if (hostIP.isBlank()) {
                        JOptionPane.showMessageDialog(null, "请输入主机IP！",
                                "登录", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    if (hostPort.isBlank()) {
                        JOptionPane.showMessageDialog(null, "请输入端口号！",
                                "登录", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    if (username.isBlank()) {
                        JOptionPane.showMessageDialog(null, "请输入用户名！",
                                "登录", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    w.loginUser = new User(
                            w.hostIPText.getText(),
                            Integer.parseInt(w.portText.getText()),
                            w.usernameText.getText()
                    );
                    if (w.onlineUsers.addUser(w.loginUser)) {
                        JOptionPane.showMessageDialog(null, "登录成功，" + w.loginUser.getUsername() + "！",
                                "登录", JOptionPane.INFORMATION_MESSAGE);
                        w.onlineUserListModel.addElement(w.loginUser.getUsername());
                        w.onlineUserList.setModel(w.onlineUserListModel);
                        w.hostIPText.setEditable(false);
                        w.portText.setEditable(false);
                        w.usernameText.setEditable(false);
                        w.sendText.setEditable(true);
                        w.sendBtn.setEnabled(true);
                    } else
                        JOptionPane.showMessageDialog(null, "登录失败，该用户已在线！",
                                "登录", JOptionPane.INFORMATION_MESSAGE);
                }
                case "退出" -> {
                    if (w.loginUser == null) {
                        JOptionPane.showMessageDialog(null, "退出登录失败，用户未登录！",
                                "退出登录", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    if (w.onlineUsers.delUser(w.loginUser.getUsername())) {
                        JOptionPane.showMessageDialog(null, "退出登录成功，" + w.loginUser.getUsername() + "！",
                                "退出登录", JOptionPane.INFORMATION_MESSAGE);
                        w.onlineUserListModel.removeElement(w.loginUser.getUsername());
                        w.onlineUserList.setModel(w.onlineUserListModel);
                        w.sendText.setText("");
                        w.sendText.setEditable(false);
                        w.sendBtn.setEnabled(false);
                    } else
                        JOptionPane.showMessageDialog(null, "退出登录失败，该用户尚未登录！",
                                "退出登录", JOptionPane.INFORMATION_MESSAGE);
                    w.hostIPText.setEditable(true);
                    w.portText.setEditable(true);
                    w.usernameText.setEditable(true);
                }
                case "发送" -> {
                    w.chatRecordArea.append("\n【" + w.loginUser.getUsername() + "】" +
                            new SimpleDateFormat("yyyy-MM-dd EEEE HH:mm:ss").format(new Date())
                            + "\n " + w.sendText.getText() + "\n");
                    w.sendText.setText("");
                }
            }
    }
}