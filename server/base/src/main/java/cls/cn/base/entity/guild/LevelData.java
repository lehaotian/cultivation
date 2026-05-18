package cls.cn.base.entity.guild;

import cls.cn.base.proto.LevelPb;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Transient;

/**
 * 等级数据
 */
@Data
public class LevelData {
    /**
     * 等级
     */
    private int level;
    /**
     * 经验
     */
    private long exp;

    @Transient
    @JsonIgnore
    public LevelPb toPb() {
        LevelPb.Builder builder = LevelPb.newBuilder();
        builder.setLevel(level);
        builder.setExp(exp);
        return builder.build();
    }

    @Transient
    @JsonIgnore
    public void forPb(LevelPb level) {
        this.level = level.getLevel();
        this.exp = level.getExp();
    }
}
