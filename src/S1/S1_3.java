// S1_3-文件类型无限制拷贝
// 张起硕-2125060196-版权所有
package S1;

import java.io.*;
import java.util.Scanner;

public class S1_3 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("请输入源文件路径：");
        String source = scan.next();
        System.out.print("请输入目标文件路径：");
        String dest = scan.next();
        FileCopy copier = new FileCopy(source, dest);
        if (copier.copy()) {
            System.out.println("拷贝成功");
            System.exit(0);
        }
        System.out.println("拷贝失败");
        System.exit(-1);
    }
}

class FileCopy {
    String sourceFile;
    String destFile;
    byte[] buffer;

    public FileCopy(String sourceFile, String destFile) {
        this.sourceFile = sourceFile;
        this.destFile = destFile;
        buffer = new byte[1024];
    }

    public boolean copy() {
        int len;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
            while ((len = in.read(buffer)) != -1)
                out.write(buffer, 0, len);
            in.close();
            out.close();
        } catch (Exception e) {
            System.out.println("[IOException]" + e);
            return false;
        }
        return true;
    }
}