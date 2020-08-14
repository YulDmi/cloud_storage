import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;
            if (Files.exists(Paths.get("folder/" + fr.getFilename()))) {
                ctx.writeAndFlush(fr);
            }
        } else if (msg instanceof FileMessage) {
            FileMessage fsm = (FileMessage) msg;
            Files.write(Paths.get("folder/" + fsm.getFilename()), fsm.getData(), StandardOpenOption.CREATE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
