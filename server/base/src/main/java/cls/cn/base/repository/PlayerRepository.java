package cls.cn.base.repository;

import cls.cn.base.entity.player.PlayerData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends MongoRepository<PlayerData, String> {
}
