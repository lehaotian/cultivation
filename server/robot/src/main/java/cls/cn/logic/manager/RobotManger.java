package cls.cn.logic.manager;

import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class RobotManger {
    private final Map<ChannelId, Robot> robotMap = new ConcurrentHashMap<>();
}
