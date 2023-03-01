// S3_1-局域网扫描器
// 张起硕-2125060196-版权所有
// 2023-03-01

package S3;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class S3_1 {
    public static void main(String[] args) throws Exception {
        LANInterface lanInterface = new LANInterface("192.168.31.66");
        lanInterface.scan();
        ArrayList<InetAddress> hosts = lanInterface.hosts;
        Thread.sleep(20000);
        for (InetAddress host : hosts)
            System.out.println(host.getHostName() + " " + host.getHostAddress());
        System.out.println("creat  and finish is " + lanInterface.createdThreadsCounter+" " + lanInterface.finishedThreadsCounter);
    }
}

class LANScanner implements Runnable {
    InetAddress host;
    final LANInterface lan;

    public LANScanner(InetAddress hostAddress, LANInterface lanInterface) {
        this.lan = lanInterface;
        this.host = hostAddress;
    }

    public boolean isHostReachable(InetAddress hostAddress) throws IOException {
        return hostAddress.isReachable(2000);
    }

    @Override
    public void run() {
        try {
            if (isHostReachable(host)){
                System.out.println("[DEBUG]" + host.getHostName() + " " + host.getHostAddress() +" , creat  and finish is " + lan.createdThreadsCounter+" " + lan.finishedThreadsCounter);
                lan.addHost(host);
            }
        } catch (IOException e) {
            System.out.println("[DEBUG]" + e.getMessage());
        } finally {
            lan.addFinishedThreadsCounter();
        }
    }
}

class LANInterface {
    private final NetworkInterface network;
    private final InetAddress localIPAddr;
    private final String localHostName;
    private final String localMACAddr;
     int createdThreadsCounter;
     int finishedThreadsCounter;
    public final ArrayList<InetAddress> hosts;

    public LANInterface(String localIP) throws UnknownHostException, SocketException {
        localIPAddr = InetAddress.getByName(localIP);
        localHostName = localIPAddr.getHostName();
        network = NetworkInterface.getByInetAddress(localIPAddr);
        hosts = new ArrayList<>();
        byte[] mac_bytes = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac_bytes.length; i++) {
            if (i != 0)
                sb.append("-");
            String s = Integer.toHexString(mac_bytes[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        localMACAddr = sb.toString().toUpperCase();
    }

    public void scan() {
        int[] subNetMask = {255, 255, 255, 0};
        int[] lanAddr = bytesToInts(addrWithMask(localIPAddr.getAddress(), subNetMask));
        try {
            createdThreadsCounter = finishedThreadsCounter = 0;
            for (int i = 1; i < 255; i++) {
                lanAddr[3] = i;
                InetAddress inetAddress = InetAddress.getByAddress(intsToBytes(lanAddr));
                addCreatedThreadsCounter();
                new Thread(new LANScanner(inetAddress, this)).start();
            }
        } catch (Exception e) {
            System.out.println("[DEBUG]" + e.getMessage());
        }
    }

    public synchronized void addHost(InetAddress host) {
        this.hosts.add(host);
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

    private void addCreatedThreadsCounter(){
        createdThreadsCounter++;
    }

    public void addFinishedThreadsCounter() {
        finishedThreadsCounter++;
    }
}