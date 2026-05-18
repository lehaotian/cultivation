package cls.cn.base.reflection;

import cls.cn.base.exception.ErrorCode;
import cls.cn.base.exception.GameException;
import cls.cn.base.proto.Def;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ProtoRegister {
    private static final Map<Integer, Parser<Message>> msgIdToProto = new HashMap<>();
    private static final Map<Class<?>, Integer> protoToMsgId = new HashMap<>();

    public static Message parseFromProto(int msgId, byte[] bodyBytes) {
        try {
            Parser<Message> parser = msgIdToProto.get(msgId);
            if (parser == null) {
                log.error("msgId:{} bodyBytes:{}", msgId, bodyBytes);
                throw new GameException(ErrorCode.SERVER_ERROR, "msgId:{}不存在", msgId);
            }
            return parser.parseFrom(bodyBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int getProtoMsgId(Class<?> protoClass) {
        return protoToMsgId.get(protoClass);
    }

    public static int getProtoMsgId(Message message) {
        return getProtoMsgId(message.getClass());
    }

    public static void register() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        // 找到所有类，如果你需要找到特定父类/接口的子类，这里可以替换
        TypeFilter tf = new AssignableTypeFilter(Message.class);
        provider.addIncludeFilter(tf);
        Set<BeanDefinition> protoSet = provider.findCandidateComponents("cls.cn.base.proto");
        for (BeanDefinition proto : protoSet) {
            String className = proto.getBeanClassName();
            // 反射获取类
            try {
                Class<?> protoClass = Class.forName(className);
                Descriptors.Descriptor descriptor = (Descriptors.Descriptor) protoClass.getMethod("getDescriptor").invoke(null);
                Integer msgId = descriptor.getOptions().getExtension(Def.msgId);
                Field field = protoClass.getDeclaredField("PARSER");
                field.setAccessible(true);
                Object fieldObject = field.get("");
                Parser<Message> parser = (Parser<Message>) fieldObject;
                msgIdToProto.put(msgId, parser);
                protoToMsgId.put(protoClass, msgId);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}