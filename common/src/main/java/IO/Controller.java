package IO;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Controller {

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    public String path = "C:\\Users\\ASUS\\IdeaProjects\\08\\untitled1\\common\\src\\main\\resources\\clientFiles";
    private String[] commands;

    public Controller(Socket socket) {
        this.socket = socket;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();

    }

    private void start() {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            String command;
            while (true) {
                command = br.readLine();
                commands = command.trim().split(" ");
                if (commands[0].equals("./download")) {
                    fileRead();
                } else if (commands[0].equals("./upload")) {
                    fileSend();
                } else if (commands[0].equals("./exit")){
                    socket.close();
                    System.exit(0);
                }else {
                    System.out.println("Не верная команда");
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(" ошибка соединения или не верная команда");
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fileRead() {
        try {
            dos.writeUTF(commands[0]);
            dos.writeUTF(commands[1]);
            String respond = dis.readUTF();
            System.out.println(respond);
            if (respond.equals("OK")) {
                File file = new File(path + "\\" + commands[1]);
                if (!file.exists()) {
                    file.createNewFile();
                }
                write(file);
            } else {
                System.out.println("Такой файл не существует");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(File file) {

        try (FileOutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            long len = dis.readLong();
            int count = dis.read(buffer);
            if (len < 1024) {
                os.write(buffer, 0, count);
                System.out.println("файл записан");
            } else {
                for (long i = 0; i <= len / 1024; i++) {
                    os.write(buffer, 0, count);
                    System.out.println("файл записан");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fileSend() {
        byte[] buffer = new byte[8192];
        File file = new File(path + "\\" + commands[1]);
        try (FileInputStream is = new FileInputStream(file)) {
            dos.writeUTF(commands[0]);
            dos.writeUTF(commands[1]);
            while (is.available() > 0) {
                int readBytes = is.read(buffer);
                dos.write(buffer, 0, readBytes);
            }
            String respond = dis.readUTF();
            System.out.println(respond);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


