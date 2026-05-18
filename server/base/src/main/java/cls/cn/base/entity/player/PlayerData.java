package cls.cn.base.entity.player;

import cls.cn.base.proto.PlayerPb;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 玩家数据
 */
@Data
@Document(collection = "players")
public class PlayerData {
    /**
     * 玩家id 就是平台账号id
     */
    @Id
    private String id;
    /**
     * 玩家昵称
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
     * 公会/联盟 id
     */
    @Indexed
    private String guildId;
    /**
     * 等级数据
     */
    private LevelData levelData = new LevelData();
    /**
     * 背包数据
     */
    private BackpackData backpackData = new BackpackData();

    @Transient
    @JsonIgnore
    public PlayerPb toPb() {
        PlayerPb.Builder builder = PlayerPb.newBuilder();
        builder.setId(id);
        builder.setName(name);
        builder.setLevel(levelData.toPb());
        builder.setBackpack(backpackData.toPb());
        return builder.build();
    }

    @Transient
    @JsonIgnore
    public void forPb(PlayerPb playerPb) {
        this.id = playerPb.getId();
        this.name = playerPb.getName();
        this.levelData.forPb(playerPb.getLevel());
        this.backpackData.forPb(playerPb.getBackpack());
    }

}
