package cls.cn.logic.event;

import cls.cn.base.entity.player.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 初始化玩家数据事件
 */
@Data
@AllArgsConstructor
public class InitPlayerEvent {
    private PlayerData playerData;
}
