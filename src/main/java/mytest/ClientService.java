package mytest;

import mytest.utils.MyUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Properties;

public class ClientService {
    public static  final String SERVER_IP;
    public static  final int SERVER_PORT;
    public static Socket socket;
    public static Properties properties = MyUtils.getProperties();
    static {
        SERVER_IP = (String) properties.get("ip");
        SERVER_PORT = (Integer) properties.get("port");
        try {
            socket = new Socket(SERVER_IP,SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void sendMsg(String data) throws IOException {
        BufferedWriter outputStreamWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        outputStreamWriter.write(data);
        outputStreamWriter.close();
    }

}
