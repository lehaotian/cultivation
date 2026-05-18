package cls.cn.logic.handler;

import cls.cn.base.entity.player.BackpackData;
import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.proto.Server;
import cls.cn.logic.event.InitPlayerEvent;
import cls.cn.logic.manager.GameHandler;
import cls.cn.logic.manager.GmCommand;
import cls.cn.logic.manager.Player;
import cls.cn.logic.service.BackPackService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

/**
 * 背包
 */
@GameHandler
@RequiredArgsConstructor
public class BackpackHandler {
    private final BackPackService backPackService;

    @EventListener
    public void initPlayerData(InitPlayerEvent event) {
        PlayerData playerData = event.getPlayerData();
    }

    /**
     * gm指令：添加道具
     */
    @GmCommand
    public void addItem(Player player, Server.CSGMCommand csGmCommand) {
        BackpackData backpackData = player.getPlayerData().getBackpackData();
        int itemId = Integer.parseInt(csGmCommand.getArgs(0));
        int count = Integer.parseInt(csGmCommand.getArgs(1));
        backPackService.addItem(player.getPlayerData(), itemId, count);
    }
}
