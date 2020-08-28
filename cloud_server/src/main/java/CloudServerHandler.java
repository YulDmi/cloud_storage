import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    private final String serverPath = "./common/src/main/resources/serverFiles/";
    private static HashMap<String, String> map = new HashMap<>();
    private String clientName;

    static {
        map.put("login1", "pass1");
        map.put("login2", "pass2");
        map.put("login3", "pass3");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileAut) {
            sendFileAuth(ctx, (FileAut) msg);
        }
        if (msg instanceof FileList) {
            sendFileList(ctx, (FileList) msg);
        }
        if (msg instanceof FileRequest) {
            sendFileRequest(ctx, (FileRequest) msg);
        }
        if (msg instanceof FileMessage) {
            sendFileMessage((FileMessage) msg);
        }
        if (msg instanceof FileDelete) {
            FileDelete fd = (FileDelete) msg;
            Files.delete(Paths.get(serverPath , clientName , fd.getFilename()));
        }
    }

    private void sendFileAuth(ChannelHandlerContext ctx, FileAut fileAut) throws IOException {
        System.out.println("Проверка аутентификации");
        if (fileAut.isExist()) {
            if (map.containsKey(fileAut.getLogin()) && map.get(fileAut.getLogin()).equals(fileAut.getPass())) {
                ctx.writeAndFlush(new FileAnswer(true, ""));
                clientName = fileAut.getLogin();
                System.out.println( clientName + " It is OK");
            } else {
                ctx.writeAndFlush(new FileAnswer(false, "Логин или пароль не верные"));
                System.out.println("Логин или пароль не верные");
            }
        } else {
            if (map.containsKey(fileAut.getLogin())) {
                ctx.writeAndFlush(new FileAnswer(false, "Данный логин уже занят"));
                System.out.println("Логин уже занят");
            } else {
                map.put(fileAut.getLogin(), fileAut.getPass());
                System.out.println("Регистрация прошла успешно");
                ctx.writeAndFlush(new FileAnswer(true, "Регистрация прошла успешно"));
                clientName = fileAut.getLogin();
                Path path = Paths.get(serverPath + clientName + "/");

                if (!Files.exists(path)) {
                    Files.createDirectory(path);
                }
            }
        }
    }

    private void sendFileList(ChannelHandlerContext ctx, FileList fl) {
        File dir = new File(serverPath + clientName);
        for (String file : dir.list()) {
            Path path = Paths.get(serverPath, clientName, file).normalize().toAbsolutePath();
            fl.getList().add(new FileInfo(path));
        }
        ctx.writeAndFlush(fl);
    }

    private void sendFileRequest(ChannelHandlerContext ctx, FileRequest fr) {
        new Thread(() -> {
            if (Files.exists(Paths.get(serverPath, clientName, fr.getFilename()))) {
                File file = new File(serverPath + clientName + "/" + fr.getFilename());
                int bufferSize = 1024 * 1024 * 10;
                int count = (int) (file.length() / bufferSize);
                if (file.length() % bufferSize != 0) {
                    count++;
                }
                byte[] b = new byte[bufferSize];
                FileMessage fm = new FileMessage(file.getName(), b, count);

                try (FileInputStream fis = new FileInputStream(file)) {
                    for (int i = 1; i <= count; i++) {
                        int read = fis.read(b);
                        fm.setPart(i);
                        if (read < bufferSize) {
                            fm.setData(Arrays.copyOfRange(b, 0, read));
                        } else {
                            fm.setData(b);
                        }
                        ctx.writeAndFlush(fm);
                        Thread.sleep(100);
                        System.out.println("Отправлена часть : " + fm.getPart());
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Такого файла на сервере нет");
            }
        }).start();
    }

    private void sendFileMessage(FileMessage fm) {
        try {
            Path path = Paths.get(serverPath,clientName, fm.getFilename());
            if (fm.getPart() == 1) {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                Files.write(path, fm.getData(), StandardOpenOption.CREATE);
               // System.out.println("Создана часть 1 из : " + fm.getCount());
            }
            if (fm.getPart() > 1) {
                Files.write(path, fm.getData(), StandardOpenOption.APPEND);
             //   System.out.println("Создана часть " + fm.getPart() + " из : " + fm.getCount());
            }
            if (fm.getPart() == fm.getCount()) {
             //   System.out.println("Файл принят. Размер файла " + Files.size(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //   cause.printStackTrace();
        ctx.close();
    }
}
