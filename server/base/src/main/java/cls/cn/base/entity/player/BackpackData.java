package cls.cn.base.entity.player;

import cls.cn.base.proto.BackpackPb;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.util.HashMap;
import java.util.Map;

/**
 * 背包数据
 */
@Data
public class BackpackData {
    /**
     * 货币
     */
    private Map<Integer, Long> currency = new HashMap<>();
    /**
     * 道具
     */
    private Map<Integer, ItemData> items = new HashMap<>();

    @Transient
    @JsonIgnore
    public BackpackPb toPb() {
        BackpackPb.Builder builder = BackpackPb.newBuilder();
        builder.putAllCurrency(currency);
        items.forEach((id, itemData) -> {
            builder.putItems(id, itemData.toPb());
        });
        return builder.build();
    }

    @Transient
    @JsonIgnore
    public void forPb(BackpackPb backpack) {
        currency.clear();
        currency.putAll(backpack.getCurrencyMap());
        items.clear();
        backpack.getItemsMap().forEach((id, itemPb) -> {
            ItemData itemData = new ItemData();
            itemData.setId(itemPb.getId());
            itemData.setCount(itemPb.getCount());
            items.put(id, itemData);
        });
    }
}
