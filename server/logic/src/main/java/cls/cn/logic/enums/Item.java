package cls.cn.logic.enums;

import cls.cn.base.exception.GameException;

import java.util.HashMap;
import java.util.Map;

import static cls.cn.base.exception.ErrorCode.PARAM_ERROR;

public enum Item {
    /**
     * 生命
     */
    life(1001),
    /**
     * 星尘
     */
    star(1201),
    /**
     * 金条
     */
    gold(1301),
    /**
     * 关卡步数
     */
    step(2100),
    ;

    private static final Map<Integer, Item> VALUE_MAP = new HashMap<>();

    static {
        for (Item item : Item.values()) {
            VALUE_MAP.put(item.value, item);
        }
    }

    private final int value;

    Item(int value) {
        this.value = value;
    }

    public static Item of(int value) {
        Item item = VALUE_MAP.get(value);
        if (item != null) {
            return item;
        }
        throw new GameException(PARAM_ERROR, "Item没有类型{}", value);
    }

    public int id() {
        return value;
    }
}