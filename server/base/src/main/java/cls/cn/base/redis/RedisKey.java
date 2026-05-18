package cls.cn.base.redis;

import com.google.common.base.Joiner;

public enum RedisKey {
    PlayerData,
    PlayerStatus,
    ;

    public String build(String... args) {
        String base = PlayerData.name().toLowerCase();
        if (args == null || args.length == 0) {
            return base;
        }
        Joiner joiner = Joiner.on(":").skipNulls();
        if (args.length == 1) {
            return joiner.join(base, args[0]);
        }
        return base + ":" + joiner.join(args);
    }
}

