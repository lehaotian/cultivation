package cls.cn.logic.service;

import cls.cn.base.entity.player.BackpackData;
import cls.cn.base.entity.player.ItemData;
import cls.cn.base.entity.player.PlayerData;
import cls.cn.base.exception.ErrorCode;
import cls.cn.base.exception.GameException;
import cls.cn.logic.enums.ItemType;
import cls.cn.logic.enums.ItemUse;
import cls.cn.logic.manager.Player;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
@AllArgsConstructor
public class BackPackService {
    /**
     * 添加道具
     */
    public ItemData addItem(PlayerData playerData, int itemId, long count) {
        if (count < 0) {
            throw new GameException(ErrorCode.PARAM_ERROR, "道具增加{}数量不合法{}", itemId, count);
        }
        BackpackData backpackData = playerData.getBackpackData();
        Map<Integer, ItemData> items = backpackData.getItems();
        ItemData itemData = items.get(itemId);
        if (count == 0) {
            return itemData;
        }
//        ItemMeta itemMeta = ItemMeta.get(itemId);
//        if (itemMeta == null) {
//            throw new GameException(ErrorCode.PARAM_ERROR, "道具{}不存在", itemId);
//        }
        ItemType itemType = ItemType.of(1);
        ItemUse itemUse = itemType.getUse();
        //自动使用 不进入背包
        if (itemUse != null) {
            itemUse.use(playerData, itemId, count);
            return null;
        }
        long oldCount = 0;
        if (itemData == null) {
            itemData = new ItemData();
            itemData.setId(itemId);
            items.put(itemId, itemData);
        } else {
            oldCount = itemData.getCount();
            if (oldCount > Long.MAX_VALUE - count) {
                throw new GameException(ErrorCode.INVALID_REQUEST, "道具{}数量溢出", itemId);
            }
        }
        long newCount = oldCount + count;
        itemData.setCount(newCount);
        return itemData;
    }


    /**
     * 移除道具
     */
    public ItemData removeItem(Player player, int itemId, long count) {
        if (count < 0) {
            throw new GameException(ErrorCode.PARAM_ERROR, "道具减少{}数量不合法{}", itemId, count);
        }
        Map<Integer, ItemData> items = player.getPlayerData().getBackpackData().getItems();
        ItemData itemData = items.get(itemId);
        if (count == 0) {
            return itemData;
        }
        if (itemData == null || itemData.getCount() < count) {
            throw new GameException(ErrorCode.ITEM_NOT_ENOUGH, "道具{}不足{}", itemId, count);
        }
        long oldCount = itemData.getCount();
        long newCount = oldCount - count;
        itemData.setCount(newCount);
        return itemData;
    }

    /**
     * 添加道具
     */
    public List<ItemData> addItem(Player player, Map<Integer, Integer> items) {
        return items.entrySet().stream()
                .map((entry) ->
                        addItem(player.getPlayerData(), entry.getKey(), entry.getValue())
                )
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 移除道具
     */
    public List<ItemData> removeItem(Player player, Map<Integer, Integer> items) {
        // 因为异常会丢弃数据 所有不用先判断库存再扣除
        return items.entrySet().stream()
                .map((entry) ->
                        removeItem(player, entry.getKey(), entry.getValue())
                )
                .toList();
    }
}
