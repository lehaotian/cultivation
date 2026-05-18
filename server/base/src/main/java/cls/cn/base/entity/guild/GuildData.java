package cls.cn.base.entity.guild;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 公会/联盟数据
 */
@Data
@Document(collection = "guilds")
public class GuildData {
    /**
     * 公会/联盟 id
     */
    @Id
    private String id;
    /**
     * 昵称
     */
    @Indexed
    private String name;
    /**
     * 创建时间
     */
    private long createdTime;
    /**
     * 服务器id
     */
    @Indexed
    private String serverId;
    /**
     * 等级数据
     */
    private LevelData levelData = new LevelData();
}
