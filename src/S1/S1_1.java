// S1_1-教师类
// 张起硕-2125060196-版权所有
package S1;

public class S1_1 {
    public static void main(String[] args) {
        Teacher javaTeacher = new JavaTeacher("James Gosling", "Canada");
        Teacher pythonTeacher = new PYTeacher("Guido van Rossum", "Netherlands");
        javaTeacher.info();
        javaTeacher.teach();
        pythonTeacher.info();
        pythonTeacher.teach();
    }
}

abstract class Teacher {
    String name;
    String addr;
    public Teacher(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }
    abstract void teach();
    abstract void info();
}

class JavaTeacher extends Teacher {
    public JavaTeacher(String name, String addr) {
        super(name, addr);
    }
    @Override
    void teach() {
        System.out.println("【授课】打开Eclipse");
        System.out.println("【授课】实施理论课授课");
    }
    @Override
    void info() {
        System.out.println("【Java语言教师】姓名：" + name + " 所在地：" + addr);
    }

}

class PYTeacher extends Teacher {
    public PYTeacher(String name, String addr) {
        super(name, addr);
    }
    @Override
    void teach() {
        System.out.println("【授课】打开PyCharm");
        System.out.println("【授课】实施理论课授课");
    }
    @Override
    void info() {
        System.out.println("【Python语言教师】姓名：" + name + " 所在地：" + addr);
    }
}