package IO;

import java.io.*;
import java.net.Socket;

public class Client {

    public Client()  {
        try {
             Socket socket = new Socket("localhost", 8189);
             new Controller(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
