// S1_2-按行读取文本文件
// 张起硕-2125060196-版权所有
package S1;

import java.io.*;

public class S1_2 {
    public static void main(String[] args) {
        String textFile = "./test.txt";
        TextReader textReader = new TextReader(textFile);
        if (textReader.read()) {
            System.out.println("读取成功");
            System.exit(0);
        }
        System.out.println("读取失败");
        System.exit(-1);
    }
}

class TextReader {
    private final String file;
    private String buffer;
    private BufferedReader reader;

    public TextReader(String file) {
        this.file = file;
        this.buffer = null;
    }

    public boolean read() {
        try {
            reader = new BufferedReader(new FileReader(file));
            for (int i = 1; ; i++) {
                buffer = reader.readLine();
                if (buffer == null)
                    break;
                System.out.println("【第" + i + "行】" + buffer);
            }
        } catch (Exception e) {
            System.out.println("[IOException]" + e);
            return false;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                System.out.println("[IOException]" + ex);
            }
        }
        return true;
    }
}