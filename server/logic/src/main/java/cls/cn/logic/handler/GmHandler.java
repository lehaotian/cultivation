package cls.cn.logic.handler;

import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.exception.ErrorCode;
import cls.cn.base.exception.GameException;
import cls.cn.base.proto.Server;
import cls.cn.logic.enums.Env;
import cls.cn.logic.event.InitPlayerEvent;
import cls.cn.logic.manager.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

/**
 * gm指令
 */
@GameHandler
@RequiredArgsConstructor
public class GmHandler {
    private final ApplicationContext applicationContext;
    private final PlayerDataManager playerDataManager;
    @Value("${spring.profiles.active}")
    private Env env;

    /**
     * 处理gm指令
     */
    public Server.SCGMCommand gmCommand(Player player, Server.CSGMCommand csGmCommand) throws Throwable {
        if (env == Env.prod) {
            throw new GameException(ErrorCode.INVALID_REQUEST, "prod环境不允许使用gm指令");
        }
        GmHandle gmHandle = HandlerRegister.getGmCommand(csGmCommand.getCommand());
        if (gmHandle == null) {
            throw new GameException(ErrorCode.INVALID_REQUEST, "gm指令{}不存在", csGmCommand.getCommand());
        }
        gmHandle.handle(player, csGmCommand);
        Server.SCGMCommand.Builder scGmCommand = Server.SCGMCommand.newBuilder();
        scGmCommand.setPlayer(player.getPlayerData().toPb());
        return scGmCommand.build();
    }

    /**
     * 重置玩家数据
     */
    @GmCommand
    public void reset(Player player, Server.CSGMCommand csGmCommand) {
        PlayerData playerData = player.getPlayerData();
        PlayerData newPlayerData = new PlayerData();
        newPlayerData.setId(playerData.getId());
        newPlayerData.setName(playerData.getName());
        newPlayerData.setCreatedTime(playerData.getCreatedTime());
        applicationContext.publishEvent(new InitPlayerEvent(newPlayerData));
        playerDataManager.savePlayerNow(newPlayerData);
        player.setPlayerData(newPlayerData);
    }
}
