package cls.cn.logic.handler;

import cls.cn.base.entity.player.LevelData;
import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.proto.Server;
import cls.cn.logic.event.InitPlayerEvent;
import cls.cn.logic.manager.GameHandler;
import cls.cn.logic.manager.GmCommand;
import cls.cn.logic.manager.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

/**
 * 等级
 */
@GameHandler
@RequiredArgsConstructor
@Slf4j
public class LevelHandler {

    @EventListener
    public void initPlayerData(InitPlayerEvent event) {
        PlayerData playerData = event.getPlayerData();
    }

    /**
     * gm指令
     */
    @GmCommand
    public void toLevel(Player player, Server.CSGMCommand csGmCommand) {
        LevelData levelData = player.getPlayerData().getLevelData();
        int level = Integer.parseInt(csGmCommand.getArgs(0));
        levelData.setLevel(level);
    }
}
