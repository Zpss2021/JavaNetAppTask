// S1_4-多线程演示过山洞
// 张起硕-2125060196-版权所有
package S1;

import java.text.SimpleDateFormat;

public class S1_4 {
    public static void main(String[] args) {
        Cave cave = new Cave("A");
        String[] nameList = {"Alice", "Bob", "Charlie", "Dave", "Eve", "Frank", "George", "Henry", "Ivan", "Jenny"};
        for (String name : nameList)
            new Thread(new Person(name, cave)).start();
    }
}

class Cave {
    String name;
    public Cave(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}

class Person implements Runnable {
    private final String name;
    private final Cave cave;
    private final SimpleDateFormat dateFormat;

    public Person(String name, Cave cave) {
        this.name = name;
        this.cave = cave;
        this.dateFormat = new SimpleDateFormat("【HH:mm:ss】");
    }

    public void pass() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(dateFormat.format(System.currentTimeMillis()) + name + "通过了山洞" + cave.getName());
    }

    @Override
    public void run() {
        synchronized (cave) {
            this.pass();
        }
    }
}