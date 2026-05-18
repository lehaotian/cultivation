package cls.cn.logic.netty;

import cls.cn.base.netty.Decoder;
import cls.cn.base.netty.Encoder;
import cls.cn.logic.robot.SendPb;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class SocketClient implements SmartInitializingSingleton {
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private final List<Channel> channels = new CopyOnWriteArrayList<>();
    @Value("${socket-server-host}")
    private String host;

    @Value("${socket-server-port}")
    private int port;
    @Value("${client-count}")
    private int clientCount;

    @Override
    public void afterSingletonsInstantiated() {
        for (int i = 0; i < clientCount; i++) {
            int index = i;
            clientExecutor.execute(() -> clientStart(index));
        }
    }

    @PreDestroy
    public void destroy() {
        clientExecutor.shutdown();
        channels.forEach(Channel::close);
    }

    public void clientStart(int index) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) // 使用内存池
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new Decoder());
                            ch.pipeline().addLast(new Encoder());
                            ch.pipeline().addLast(new SocketClientHandler());
                        }
                    });

            // 启动客户端
            ChannelFuture f = bootstrap.connect(host, port).sync();
            Channel channel = f.channel();
            f.addListener(
                    (ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            log.info("SocketClient connected to {}:{}", host, port);
                            channels.add(channel);
                            new SendPb(channel).send();
                        } else {
                            log.error("SocketClient failed to connect to {}:{}", host, port);
                        }
                    });
            // 等待连接关闭
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            // 优雅地关闭线程组
            group.shutdownGracefully();
            clientExecutor.shutdown();
        }
    }
}