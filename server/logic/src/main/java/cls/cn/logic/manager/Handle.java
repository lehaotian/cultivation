package cls.cn.logic.manager;

import com.google.protobuf.Message;

@FunctionalInterface
public interface Handle {
    Message handle(Player player, Message message) throws Throwable;
}
