package cls.cn.logic.enums;

import cls.cn.base.entity.player.PlayerData;

@FunctionalInterface
public interface ItemUse {
    void use(PlayerData playerData, int item, long count);
}
