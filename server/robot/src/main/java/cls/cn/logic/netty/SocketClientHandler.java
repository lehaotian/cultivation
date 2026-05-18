package cls.cn.logic.netty;

import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketClientHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        log.info("接收消息 channel:{} {}:{}",
                ctx.channel().id(),
                msg.getClass().getSimpleName(),
                TextFormat.shortDebugString(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 处理异常
        log.error("Exception caught: ", cause);
        ctx.close();
    }
}