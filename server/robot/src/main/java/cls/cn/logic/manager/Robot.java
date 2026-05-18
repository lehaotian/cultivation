package cls.cn.logic.manager;

import cls.cn.base.entity.player.PlayerData;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Robot {
    private Channel channel;
    private PlayerData playerData;
    private int index;

    public Robot(Channel channel, int index) {
        this.channel = channel;
        this.index = index;
    }


}
