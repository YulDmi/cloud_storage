import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    private final String path = "./common/src/main/resources/serverFiles";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;
            if (Files.exists(Paths.get(path + "/"+ fr.getFilename()))) {
                FileMessage fm = new FileMessage(Paths.get(path + "/" + fr.getFilename()));
                ctx.writeAndFlush(fm);
            }else {
                System.out.println("Такого файла на сервере нет");
            }
        } else if (msg instanceof FileMessage) {
            FileMessage fsm = (FileMessage) msg;
            System.out.println("Файл msg принят");
            Files.write(Paths.get(path +"/" + fsm.getFilename()), fsm.getData(), StandardOpenOption.CREATE);
        }else if (msg instanceof FileDelete){
            FileDelete fd = (FileDelete) msg;
            System.out.println("удаление файла : " + fd.getFilename() );
            Files.delete(Paths.get(path + "/" + fd.getFilename()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
     //   cause.printStackTrace();
        ctx.close();
    }
}
