package cls.cn.logic.handler;

import cls.cn.base.entity.player.PlayerData;
import cls.cn.logic.event.InitPlayerEvent;
import cls.cn.logic.manager.GameHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

/**
 * 任务
 */
@GameHandler
@RequiredArgsConstructor
public class TaskHandler {

    @EventListener
    public void initPlayerData(InitPlayerEvent event) {
        PlayerData playerData = event.getPlayerData();
    }
}
