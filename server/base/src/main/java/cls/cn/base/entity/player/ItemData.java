package cls.cn.base.entity.player;

import cls.cn.base.proto.ItemPb;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Transient;

/**
 * 道具
 */
@Data
public class ItemData {
    /**
     * 道具id
     */
    private int id;
    /**
     * 道具数量
     */
    private long count;

    @Transient
    @JsonIgnore
    public ItemPb toPb() {
        ItemPb.Builder builder = ItemPb.newBuilder();
        builder.setId(id);
        builder.setCount(count);
        return builder.build();
    }
}
