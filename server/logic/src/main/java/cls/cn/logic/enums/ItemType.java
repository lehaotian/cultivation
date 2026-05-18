package cls.cn.logic.enums;

import cls.cn.base.exception.GameException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static cls.cn.base.exception.ErrorCode.PARAM_ERROR;

@Getter
public enum ItemType {
    common(1),
    skill(20),
    step(21, (playerData, item, count) -> {
    }),
    ;
    private static final Map<Integer, ItemType> VALUE_MAP = new HashMap<>();

    static {
        for (ItemType itemType : ItemType.values()) {
            VALUE_MAP.put(itemType.value, itemType);
        }
    }

    private final int value;
    private final ItemUse use;

    ItemType(int value, ItemUse use) {
        this.value = value;
        this.use = use;
    }

    ItemType(int value) {
        this.value = value;
        this.use = null;
    }

    public static ItemType of(int value) {
        ItemType itemType = VALUE_MAP.get(value);
        if (itemType != null) {
            return itemType;
        }
        throw new GameException(PARAM_ERROR, "ItemType没有类型{}", value);
    }
}
