// S2_4-模拟取款机
// 张起硕-2125060196-版权所有
// 2023-02-24

package S2;

import java.util.Random;

public class S2_4 {
    public static void main(String[] args) {
        ATM atm = new ATM(800);
        Client[] clients = new Client[10];
        for (int i = 0; i < 10; i++) {
            clients[i] = new Client(atm);
            new Thread(clients[i]).start();
        }
    }
}

class ATM {
    private int balance;

    public ATM(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int takeBalance(int count) {
        if (balance - count < 0)
            return -1;
        balance -= count;
        return count;
    }
}

class Client implements Runnable {
    final ATM atm;
    Random rand;

    public Client(ATM atm) {
        this.atm = atm;
        this.rand = new Random(System.currentTimeMillis() * this.hashCode());
    }

    @Override
    public void run() {
        synchronized (atm) {
            System.out.println("取款" + atm.takeBalance(rand.nextInt(31) + 50)
                    + "元，余额" + atm.getBalance() + "元");
        }
    }
}