package IO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    private boolean isRunning = true;

    public Server() {
        try (ServerSocket server = new ServerSocket(8189)) {
            while (isRunning) {
                Socket socket = server.accept();
                System.out.println("Client accepted!");
               new Thread(new ServerHandler(socket)).start();
            }
        } catch (IOException e) {
            stopServer();
            e.printStackTrace();
        }
    }

    private void stopServer() {
        isRunning = false;
    }



    public static void main(String[] args) throws IOException {
        new Server();
    }
}

