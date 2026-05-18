package cls.cn.logic.handler;

import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.exception.ErrorCode;
import cls.cn.base.exception.GameException;
import cls.cn.base.proto.Server;
import cls.cn.logic.enums.Env;
import cls.cn.logic.manager.GameHandler;
import cls.cn.logic.manager.Player;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

/**
 * 登录
 */
@GameHandler
@RequiredArgsConstructor
@Slf4j
public class LoginHandler {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.profiles.active}")
    private Env env;

    public boolean auth(String token) {
        if (env == Env.local) {
            return true;
        }
        //TODO等客户单接入SDK后改为return Objects.equals(authResp.getStatus(), "0");
        return true;
    }

    /**
     * 登录
     */
    public Server.SCLogin login(Player player, Server.CSLogin csLogin) {
        Server.SCLogin.Builder scLogin = Server.SCLogin.newBuilder();
        String id = csLogin.getId();
        if (Strings.isNullOrEmpty(id)) {
            throw new GameException(ErrorCode.PARAM_ERROR, "userId不能为空");
        }
        PlayerData playerData = player.getPlayerData();
        scLogin.setPlayer(playerData.toPb());
        player.setPlayerData(playerData);
        log.info("登录成功 id:{} channel:{}", id, player.getChannel().id());
        return scLogin.build();
    }
}
