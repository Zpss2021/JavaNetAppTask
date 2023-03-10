// S4_1-TOP250影片名称 URL爬虫练习
// 张起硕-2125060196
// 2023-03-10

package S4;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Vector;

public class S4_1 {
    public static void main(String[] args) {
        String urlHeader = "file:///D:/Documents/IdeaProjects/NetAppTask/res/";
        MovieListWindow window = new MovieListWindow();
        window.setDefaultURL(urlHeader + "ext/douban/Top250.html");
        window.showWindow();
    }
}

class MovieListWindow extends JFrame {
    MovieListWindowController controller;
    JPanel upPane;
    JScrollPane listPane;
    JTextField urlText;
    JButton startBtn;
    JList<String> movieList;

    public MovieListWindow() {
        controller = new MovieListWindowController();
        upPane = new JPanel();
        urlText = new JTextField("请输入URL");
        startBtn = new JButton("获取影片名");
        movieList = new JList<>();
        initWindow();
        controller.setView(this);
        urlText.addFocusListener(controller);
        startBtn.addActionListener(controller);
        this.setTitle("爬虫练习-Top250电影爬取");
        this.setBounds(650, 350, 420, 360);
        this.setMinimumSize(new Dimension(350, 240));
        this.setMaximumSize(new Dimension(1080, 1080));
        this.add(upPane, BorderLayout.NORTH);
        this.add(listPane, BorderLayout.CENTER);
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
        listPane = new JScrollPane(movieList);
        listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        upPane.setPreferredSize(new Dimension(320, 35));
        urlText.setPreferredSize(new Dimension(200, 25));
        startBtn.setPreferredSize(new Dimension(100, 25));
        listPane.setPreferredSize(new Dimension(400, 320));

        upPane.add(urlText);
        upPane.add(startBtn);

        urlText.setForeground(new Color(200, 200, 200));
    }

    public void setDefaultURL(String url) {
        urlText.setText(url);
        urlText.setForeground(Color.BLACK);
    }

    public void showWindow() {
        this.setVisible(true);
        this.validate();
    }
}

class MovieListWindowController implements ActionListener, FocusListener {
    ElementCrawler crawler;
    MovieListWindow win;

    public void setView(MovieListWindow view) {
        this.win = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == win.startBtn){
            win.movieList.removeAll();
            try {
                crawler = new ElementCrawler(win.urlText.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "错误", JOptionPane.INFORMATION_MESSAGE);
            }
            crawler.setCSSQuery("li .info .title:first-child");
            win.movieList.setListData(crawler.getElementList());
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (win.urlText.getText().equals("请输入URL")) {
            win.urlText.setText("");
            win.urlText.setForeground(Color.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (win.urlText.getText().equals("")) {
                win.urlText.setText("请输入URL");
                win.urlText.setForeground(Color.GRAY);
            }
    }
}

class ElementCrawler {
    private final HTMLDocument document;
    private String cssQuery;

    public ElementCrawler(String url) throws IOException {
        document = new HTMLDocument(url);
    }

    public void setCSSQuery(String cssQuery) {
        this.cssQuery = cssQuery;
    }

    public Vector<String> getElementList() {
        Vector<String> elementList = new Vector<>();
        Elements elements = document.querySelectorAll(cssQuery);
        for (Element element : elements)
            elementList.add(element.text());
        return elementList;
    }
}

class HTMLDocument {
    private final Document doc;

    public HTMLDocument(String url) throws IOException {
        int len, buffer_len = 1024;
        byte[] buffer = new byte[buffer_len];
        StringBuilder sb = new StringBuilder();
        InputStream stream = new URL(url).openConnection().getInputStream();
        while ((len = stream.read(buffer, 0, buffer_len)) != -1)
            sb.append(new String(buffer, 0, len));
        doc = Jsoup.parse(sb.toString());
    }

    public Element querySelector(String cssQuery) {
        return doc.selectFirst(cssQuery);
    }

    public Elements querySelectorAll(String cssQuery) {
        return doc.select(cssQuery);
    }
}