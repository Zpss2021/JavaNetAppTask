// S4_1-TOP250爬虫练习
// 张起硕-2125060196
// 2023-03-10

package S4;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class S4_1 {
    public static void main(String[] args) throws IOException {
        HTMLContent html = new HTMLContent("https://www.baidu.com");
        System.out.println(html.getContent());
    }
}

class HTMLContent {
    private final InputStream stream;

    public HTMLContent(String url) throws IOException {
        URL destURL = new URL(url);
        URLConnection connection = destURL.openConnection();
        stream = connection.getInputStream();
    }

    public String getContent() throws IOException {
        StringBuilder sb = new StringBuilder();
        int len = 0, buffer_len = 1024;
        byte[] buffer = new byte[buffer_len];
        while ((len = stream.read(buffer, 0, buffer_len)) != -1)
            sb.append(new String(buffer, 0, len));
        return sb.toString();
    }
}