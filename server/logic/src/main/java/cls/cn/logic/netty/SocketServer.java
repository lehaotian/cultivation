package cls.cn.logic.netty;

import cls.cn.base.netty.Decoder;
import cls.cn.base.netty.Encoder;
import cls.cn.logic.manager.PlayerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
@Slf4j
@Order(10)
public class SocketServer implements SmartInitializingSingleton {
    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "nettyStart");
        t.setDaemon(true);
        return t;
    });
    private final PlayerManager playerManager;
    @Value("${socket-port}")
    private int port;
    private volatile EventLoopGroup bossGroup;
    private volatile EventLoopGroup workerGroup;
    private volatile Channel serverChannel;

    @Override
    public void afterSingletonsInstantiated() {
        // 线程池用于异步启动
        serverExecutor.execute(this::serverStart);
    }

    @PreDestroy
    public void destroy() {
        // 先关闭 channel，确保 serverStart 线程上的 closeFuture().sync() 能退出
        Channel ch = serverChannel;
        if (ch != null) {
            ch.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        serverExecutor.shutdown();
        try {
            serverExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void serverStart() {
        bossGroup = new NioEventLoopGroup(1);
        int workerGroupThreads = Runtime.getRuntime().availableProcessors() * 2;
        workerGroup = new NioEventLoopGroup(workerGroupThreads);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 禁用 Nagle 算法减延迟
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 优化写缓冲区水位线
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(256 * 1024, 512 * 1024))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new Decoder());
                            ch.pipeline().addLast(new Encoder());
                            ch.pipeline().addLast(new ServerHandler(playerManager));
                        }
                    });
            ChannelFuture bindFuture = serverBootstrap.bind(port);
            bindFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("逻辑服启动成功，绑定端口{}", port);
                } else {
                    log.error("逻辑服启动失败 绑定端口{}", port, future.cause());
                }
            });
            serverChannel = bindFuture.sync().channel();
            serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Server start interrupted", e);
        } catch (Exception e) {
            log.error("Failed to start server", e);
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            serverExecutor.shutdown();
        }
    }
}