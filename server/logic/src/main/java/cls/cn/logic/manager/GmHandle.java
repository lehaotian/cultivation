package cls.cn.logic.manager;

import cls.cn.base.proto.Server;

@FunctionalInterface
public interface GmHandle {
    void handle(Player player, Server.CSGMCommand gmCommand) throws Throwable;
}
