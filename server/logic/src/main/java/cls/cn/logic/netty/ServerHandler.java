package cls.cn.logic.netty;

import cls.cn.logic.manager.PlayerManager;
import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ServerHandler extends SimpleChannelInboundHandler<Message> {

    private final PlayerManager playerManager;

    /**
     * 接收消息
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel channel = ctx.channel();
        playerManager.handleMsg(channel, msg);
    }

    /**
     * 建立新连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        log.info("建立连接 channel {}", channel.id());
    }

    /**
     * 断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        // 清理玩家数据
        playerManager.removePlayer(channel.id());
        log.info("断开连接 channel {}", channel.id());
    }

    /**
     * 有异常发生
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.error("异常发生 channel {} cause {}", channel.id(), cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * 通道可写状态改变
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        Channel channel = ctx.channel();
        log.warn("状态改变 channel {}", channel.id());
    }
}
