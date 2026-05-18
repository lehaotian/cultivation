package cls.cn.logic.manager;

import cls.cn.base.entity.player.PlayerData;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Player {
    private static final TextFormat.Printer PROTO_PRINTER = TextFormat.printer().emittingSingleLine(true);
    private Channel channel;
    private PlayerData playerData;
    private String userId;

    public Player(Channel channel, String userId) {
        this.channel = channel;
        this.userId = userId;
    }

    public void onDisconnect() {
        channel.close();
    }

    public void send(Message scMessage) {
        if (!channel.isActive()) {
            return;
        }
        channel.writeAndFlush(scMessage);
        if (log.isDebugEnabled()) {
            String msg = PROTO_PRINTER.printToString(scMessage);
            log.debug("发送消息 {}:{}", scMessage.getClass().getSimpleName(), msg);
        }
    }
}