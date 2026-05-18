package cls.cn.logic.manager;

import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.redis.RedisKey;
import cls.cn.base.repository.PlayerRepository;
import cls.cn.logic.event.InitPlayerEvent;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlayerDataManager {
    // 存储待持久化数据 key openId value playerData
    private final AtomicReference<ConcurrentHashMap<String, PlayerData>> playersRef
            = new AtomicReference<>(new ConcurrentHashMap<>());
    private final ApplicationContext applicationContext;
    private final PlayerRepository playerRepository;
    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, PlayerData> redisTemplate;

    /**
     * 获取玩家数据
     */
    public PlayerData getPlayer(String id) {
        String key = RedisKey.PlayerData.build(id);
        PlayerData playerData = redisTemplate.opsForValue().get(key);
        if (playerData != null) {
            // 缓存命中，直接返回
            return playerData;
        }
        playerData = playerRepository.findById(id)
                // 玩家数据不存在，创建玩家数据
                .orElseGet(() -> createPlayerData(id));
        // 更新缓存
        redisTemplate.opsForValue().set(key, playerData, 3, TimeUnit.DAYS);
        return playerData;
    }

    private PlayerData createPlayerData(String id) {
        PlayerData playerData = new PlayerData();
        playerData.setId(id);
        playerData.setName(id);
        long now = System.currentTimeMillis();
        playerData.setCreatedTime(now);
        applicationContext.publishEvent(new InitPlayerEvent(playerData));
        playerData = playerRepository.save(playerData);
        return playerData;
    }

    /**
     * 立即持久化玩家数据
     */
    public void savePlayerNow(PlayerData playerData) {
        if (playerData == null) {
            return;
        }
        String key = RedisKey.PlayerData.build(playerData.getId());
        // 更新缓存 8小时过期
        redisTemplate.opsForValue().set(key, playerData, 8, TimeUnit.HOURS);
        // 移除缓存
        playersRef.get().remove(playerData.getId());
        // 将修改写入mongo
        playerRepository.save(playerData);
    }

    /**
     * 异步持久化玩家数据（先更新缓存，异步持久化）
     */
    public void savePlayerAsync(Player player) {
        PlayerData playerData = player.getPlayerData();
        if (playerData == null) {
            return;
        }
        String key = RedisKey.PlayerData.build(playerData.getId());
        // 更新缓存
        redisTemplate.opsForValue().set(key, playerData, 3, TimeUnit.DAYS);
        // 将修改加入批量队列
        playersRef.get().put(playerData.getId(), playerData);
    }

    // 定时批量持久化（每分钟触发）
    @Scheduled(cron = "0 * * * * ?")
    // 异步取dbSave线程池执行
    @Async("dbSave")
    public void batchPersistToMongo() {
        if (playersRef.get().isEmpty()) return;
        // 创建副本并清空原队列
        ConcurrentHashMap<String, PlayerData> snapshot = playersRef.getAndSet(new ConcurrentHashMap<>());
        batchSavePlayerData(snapshot.values());
    }

    private void batchSavePlayerData(Collection<PlayerData> snapshot) {
        try {
            // BulkOperations方式（批量处理）
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, PlayerData.class);
            for (PlayerData playerData : snapshot) {
                Query query = new Query(Criteria.where("id").is(playerData.getId()));
                bulkOps.replaceOne(query, playerData);
            }
            BulkWriteResult result = bulkOps.execute();
            log.info("批量持久化{}条数据，成功{}条，失败{}条", snapshot.size(), result.getModifiedCount(), snapshot.size() - result.getModifiedCount());
        } catch (MongoBulkWriteException ex) {
            // 获取所有失败项
            List<BulkWriteError> errors = ex.getWriteErrors();
            // 打印每个失败项的具体信息
            for (BulkWriteError error : errors) {
                log.warn("批量持久化错误代码: {} 错误信息: {} 涉及文档: {}", error.getCode(), error.getMessage(), error.getDetails());          // MongoDB 错误码
            }
        } catch (Exception e) {
            log.error("批量持久化失败", e);
        }
    }

    @PreDestroy
    public void onShutdown() {
        Collection<PlayerData> playerDataCollection = playersRef.get().values();
        if (!playerDataCollection.isEmpty()) {
            log.info("应用关闭前强制持久化剩余{}条数据", playerDataCollection.size());
            batchSavePlayerData(playerDataCollection);
        }
    }
}