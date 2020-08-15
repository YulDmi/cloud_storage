package IO;

import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private boolean isRunning = true;
    public String path = "C:\\Users\\ASUS\\IdeaProjects\\08\\untitled1\\common\\src\\main\\resources\\serverFiles";
    private static int count = 1;

    public ServerHandler(Socket socket) throws IOException {
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        String user = "user" + count;
        path = path + "\\" + user;
        count++;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String command = is.readUTF();
                if (command.equals("./download")) {
                    String fileName = is.readUTF();
                    File file = new File(path + "\\" + fileName);
                    if (file.exists()) {
                        os.writeUTF("OK");
                        sendFile(file);
                    } else {
                        os.writeUTF("File not exist");
                    }
                } else if (command.equals("./upload")){
                    readFile();
                } else {
                    os.writeUTF("Не верная команда");
                }
            } catch (IOException e) {
                stop();
                e.printStackTrace();
            }
        }
    }

    private void sendFile(File file) {
        long len = file.length();
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            os.writeLong(len);
            while (fis.available() > 0) {
                int count = fis.read(buffer);
                os.write(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        try {
            String fileName = is.readUTF();
            File file = new File(path + "\\" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                while (is.available() > 0) {
                    int r = is.read(buffer);
                    os.write(buffer, 0, r);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("file not correct");
        }
        try {
            os.writeUTF("File uploaded!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
