package cls.cn.logic.manager;

import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.exception.ErrorCode;
import cls.cn.base.exception.GameException;
import cls.cn.base.proto.Server;
import cls.cn.logic.handler.LoginHandler;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlayerManager implements SmartInitializingSingleton {
    private static final int THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final List<ExecutorService> executors = new ArrayList<>();
    private static final TextFormat.Printer PROTO_PRINTER = TextFormat.printer().emittingSingleLine(true);
    private final Map<ChannelId, Player> onlinePlayers = new ConcurrentHashMap<>();
    private final Map<String, Player> userIdToPlayers = new ConcurrentHashMap<>();
    private final PlayerDataManager playerDataManager;
    private final LoginHandler loginHandler;

    public void registerPlayer(String userId, Channel channel, Player player) {
        onlinePlayers.put(channel.id(), player);
        userIdToPlayers.put(userId, player);
    }

    // 移除
    public void removePlayer(ChannelId channelId) {
        Player player = onlinePlayers.remove(channelId);
        if (player != null) {
            player.onDisconnect();
            userIdToPlayers.remove(player.getUserId());
            playerDataManager.savePlayerNow(player.getPlayerData());
        }
    }

    // 获取玩家对象
    public Player getPlayer(String userId) {
        return userIdToPlayers.get(userId);
    }

    public Player getPlayer(Channel channel) {
        return onlinePlayers.get(channel.id());
    }

    /**
     * 处理消息
     */
    public void handleMsg(Channel channel, Message csMessage) {
        Player player = getPlayer(channel);
        if (player != null) {
            handle(player, csMessage);
            return;
        }
        if (!(csMessage instanceof Server.CSLogin csLogin)) {
            log.warn("channel {} not login", channel.id());
            return;
        }
        submit(csLogin.getId(), () -> {
            if (!loginHandler.auth(csLogin.getToken())) {
                log.warn("登录认证失败userId:{} token:{}", csLogin.getId(), csLogin.getToken());
                return;
            }
            Player newPlayer = userIdToPlayers.get(csLogin.getId());
            if (newPlayer != null) {
                newPlayer.setChannel(channel);
            } else {
                newPlayer = new Player(channel, csLogin.getId());
            }
            registerPlayer(csLogin.getId(), channel, newPlayer);
            handle(newPlayer, csLogin);
        });
    }

    public void handle(Player player, Message csMessage) {
        submit(player.getUserId(), () -> {
            try {
                String csName = csMessage.getClass().getSimpleName();
                if (log.isDebugEnabled()) {
                    String msg = PROTO_PRINTER.printToString(csMessage);
                    log.debug("接收消息 {}:{}", csName, msg);
                }
                long startTime = System.currentTimeMillis();
                PlayerData playerData = playerDataManager.getPlayer(player.getUserId());
                player.setPlayerData(playerData);
                MDC.put("id", player.getUserId());
                Handle handle = HandlerRegister.getHandler(csMessage);
                if (handle == null) {
                    throw new GameException(ErrorCode.INVALID_REQUEST, "未注册的消息处理器: " + csMessage.getClass().getName());
                }
                Message scMessage = handle.handle(player, csMessage);
                if (log.isDebugEnabled()) {
                    log.debug("处理耗时 {} costTime:{}", csName, System.currentTimeMillis() - startTime);
                }
                player.send(scMessage);
                playerDataManager.savePlayerAsync(player);
            } catch (GameException e) {
                Server.SCError scServerError = Server.SCError.newBuilder()
                        .setCode(e.getCode().getCode())
                        .setInfo(e.getMessage())
                        .build();
                player.send(scServerError);
                log.warn("业务异常", e);
            } catch (Throwable e) {
                Server.SCError scServerError = Server.SCError.newBuilder()
                        .setCode(-1)
                        .setInfo("server error")
                        .build();
                player.send(scServerError);
                log.error("player {} handle message error", player.getUserId(), e);
            } finally {
                player.setPlayerData(null);
                MDC.clear();
            }
        });
    }


    /**
     * 提交任务 根据userId选择线程池
     */
    private void submit(String userId, Runnable task) {
        int idx = Math.floorMod(userId == null ? 0 : userId.hashCode(), THREADS);
        executors.get(idx).submit(task);
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            executors.add(Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "player-" + threadId);
                t.setDaemon(true);
                return t;
            }));
        }
    }

    @PreDestroy
    public void shutdown() {
        for (ExecutorService executor : executors) {
            executor.shutdown();
        }
        for (ExecutorService executor : executors) {
            try {
                executor.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}