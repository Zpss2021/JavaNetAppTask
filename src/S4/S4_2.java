// S4_2-网站图片下载
// 张起硕-2125060196
// 2023-03-10

package S4;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class S4_2 {
    public static void main(String[] args) {
        FileDownloadService service = new FileDownloadService();
        try {
            service.setURL("请输入文件URL：");
            service.setDestFile("请输入文件保存地址：");
            service.download();
        } catch (IOException e) {
            System.err.println("[DEBUG]" + e);
        }
    }
}

class HTMLStream {
    private final InputStream stream;
    private final URLConnection connection;

    public HTMLStream(String url) throws IOException {
        URL destURL = new URL(url);
        connection = destURL.openConnection();
        stream = connection.getInputStream();
    }

    public InputStream getStream() {
        return stream;
    }

    public String getDownloadFileName() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) connection;
        conn.connect();
        conn.getResponseCode();
        String fileNameHeaderField = conn.getHeaderField("Content-Disposition");
        if (fileNameHeaderField == null)
            throw new IOException("java.net.HttpURLConnection.getHeaderField(String) is null");
        String fileName = new String(fileNameHeaderField.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        if (fileName.contains("="))
            fileName = fileName.substring(fileName.indexOf("=") + 1).trim().replace("\"", "");
        System.out.println("[DEBUG]" + fileName);
        return fileName;
    }
}

class FileDownloadService {
    private HTMLStream htmlStream;
    private String destFile;
    private final byte[] buffer;
    public final Scanner scan;

    public FileDownloadService() {
        buffer = new byte[1024];
        scan = new Scanner(System.in);
    }

    public void setURL(String hint) throws IOException {
        System.out.print(hint);
        this.htmlStream = new HTMLStream(scan.nextLine());
    }

    public void setDestFile(String hint) {
        System.out.print(hint);
        String destFile = scan.next();
        if (!destFile.endsWith("\\"))
            destFile = destFile + "\\";
        try {
            this.destFile = destFile + htmlStream.getDownloadFileName();
        } catch (IOException e) {
            System.out.print("文件名获取失败，请手动指定文件名：");
            this.destFile = destFile + scan.next();
        }
    }

    public void download() throws IOException {
        int len;
        BufferedInputStream in = new BufferedInputStream(htmlStream.getStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
        while ((len = in.read(buffer)) != -1)
            out.write(buffer, 0, len);
        System.out.println("下载完成");
        in.close();
        out.close();
    }
}