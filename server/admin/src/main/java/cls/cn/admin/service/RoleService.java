package cls.cn.admin.service;

import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.redis.RedisKey;
import cls.cn.base.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {
    private final MongoTemplate mongoTemplate;
    private final PlayerRepository playerRepository;
    private final RedisTemplate<String, PlayerData> redisTemplate;

    /**
     * 获取玩家数据
     */
    public PlayerData getPlayer(String userId) {
        String key = RedisKey.PlayerData.build(userId);
        PlayerData playerData = redisTemplate.opsForValue().get(key);
        if (playerData != null) {
            // 缓存命中，直接返回
            return playerData;
        }
        playerData = playerRepository.findById(userId).orElseThrow();
        // 更新缓存
        redisTemplate.opsForValue().set(key, playerData, 3, TimeUnit.DAYS);
        return playerData;
    }

    /**
     * 保存玩家数据
     */
    public void savePlayer(PlayerData playerData) {
        if (playerData == null) {
            return;
        }
        String key = RedisKey.PlayerData.build(playerData.getId());
        // 更新缓存 8小时过期
        redisTemplate.opsForValue().set(key, playerData, 8, TimeUnit.HOURS);
        // 将修改写入mongo
        playerRepository.save(playerData);
    }
}
