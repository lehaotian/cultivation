package cls.cn.logic;

import cls.cn.base.entity.player.BackpackData;
import cls.cn.base.entity.player.PlayerData;
import org.junit.jupiter.api.Test;

class DiffTests {

    @Test
    void testComparePlayerData() {
        // 创建旧的 PlayerData 对象
        PlayerData oldData = new PlayerData();
        oldData.setId("1");
        oldData.setName("Player1");
        BackpackData backpackData = oldData.getBackpackData();
        for (int i = 0; i < 300; i++) {
            backpackData.getCurrency().put(i, i * 100L);
        }

        // 创建新的 PlayerData 对象
        PlayerData newData = new PlayerData();
        newData.setId("1");
        newData.setName("Player2"); // 修改了 name 字段
        BackpackData backpackData2 = newData.getBackpackData();
        for (int i = 0; i < 100; i++) {
            backpackData2.getCurrency().put(i, i * 100L);
        }
        for (int i = 10; i < 200; i++) {
            backpackData2.getCurrency().put(i, i * 200L);
        }
//        DiffUtil.comparePlayerData(oldData, newData);
        long currentTimeMillis = System.currentTimeMillis();
        // 调用 DiffUtil 比较
        for (int j = 0; j < 1; j++) {

//            Diff diff = DiffUtil.comparePlayerData(oldData, newData);
//            System.out.println(diff.prettyPrint());
            // 验证 diff 是否包含 name 字段的变化
//            assertTrue(diff.getChangesByType(org.javers.core.diff.changetype.ValueChange.class)
//                    .stream()
//                    .anyMatch(change -> "name".equals(change.getPropertyName())));
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - currentTimeMillis) + "ms");

    }
}