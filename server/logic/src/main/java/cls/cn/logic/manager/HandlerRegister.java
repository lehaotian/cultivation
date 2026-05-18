package cls.cn.logic.manager;

import cls.cn.base.proto.Server;
import cls.cn.base.reflection.ProtoRegister;
import com.google.protobuf.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class HandlerRegister implements SmartInitializingSingleton {
    private static final Map<Integer, Handle> msgIdToHandler = new ConcurrentHashMap<>();
    private static final Map<String, GmHandle> commandToGm = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;

    public static Handle getHandler(Message message) {
        int msgId = ProtoRegister.getProtoMsgId(message);
        return msgIdToHandler.get(msgId);
    }

    public static GmHandle getGmCommand(String command) {
        return commandToGm.get(command);
    }

    private static void registerGmCommand(Object bean, Method method) throws Exception {
        // 方法参数必须是 Player 和 Server.CSGmCommand
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 2) {
            return;
        }
        if (!paramTypes[0].equals(Player.class)) {
            return;
        }
        if (!Server.CSGMCommand.class.isAssignableFrom(paramTypes[1])) {
            return;
        }
        // 返回值必须是void
        if (!method.getReturnType().equals(void.class)) {
            return;
        }
        method.setAccessible(true);
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.unreflect(method).bindTo(bean);
        commandToGm.put(method.getName(), methodHandle::invoke);
        log.info("注册GM命令处理器: command={}", method.getName());
    }

    private static void registerHandler(Object bean, Method method) throws Exception {
        // 方法参数必须是 Player 和 Message
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 2) {
            return;
        }
        if (!paramTypes[0].equals(Player.class)) {
            return;
        }
        if (!Message.class.isAssignableFrom(paramTypes[1])) {
            return;
        }
        // 返回值必须是Message
        if (!Message.class.isAssignableFrom(method.getReturnType())) {
            return;
        }
        Class<?> protoClass = paramTypes[1];
        // 获取协议消息号
        int msgId = ProtoRegister.getProtoMsgId(protoClass);
        // 通过 LambdaMetafactory 生成更快的调用路径，避免每条消息都走 invoke(...) 的额外开销
        // 但为了确保兼容性，这里做一次失败回退（避免启动阶段注册直接挂掉）。
        Handle handle;
        try {
            handle = createHandle(bean, method);
        } catch (Throwable e) {
            log.warn("LambdaMetafactory 注册失败，回退到 MethodHandle.invoke，method={}", method.getName(), e);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle methodHandle = lookup.unreflect(method).bindTo(bean);
            handle = (player, message) -> (Message) methodHandle.invoke(player, message);
        }
        msgIdToHandler.put(msgId, handle);
        log.info(
                "注册消息处理器: msgId={}, protoClass={}, method={}",
                msgId,
                protoClass.getSimpleName(),
                method.getName()
        );
    }

    public static Handle createHandle(Object bean, Method method) throws Throwable {
        // 1. 获取目标类和方法
        method.setAccessible(true);

        // 2. 创建已绑定实例的 MethodHandle
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(bean.getClass(), MethodHandles.lookup());

        MethodHandle methodHandle = lookup.unreflect(method).bindTo(bean);

        // 3. 配置 LambdaMetafactory 参数
        MethodType interfaceMethodType = MethodType.methodType(Message.class, Player.class, Message.class);
        // 类型完全一致

        CallSite callSite = LambdaMetafactory.metafactory(
                lookup,
                "handle",
                MethodType.methodType(Handle.class),
                interfaceMethodType,
                methodHandle,
                interfaceMethodType
        );

        // 4. 生成 Lambda 实例
        return (Handle) callSite.getTarget().invokeExact();
    }

    public void registerHandler(Class<?> protoClass, Handle handle) {
        int msgId = ProtoRegister.getProtoMsgId(protoClass);
        msgIdToHandler.put(msgId, handle);
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            try {
                Object bean = applicationContext.getBean(beanName);
                Class<?> targetClass = AopUtils.getTargetClass(bean);
                if (!targetClass.isAnnotationPresent(GameHandler.class)) {
                    continue;
                }
                for (Method method : targetClass.getDeclaredMethods()) {
                    // 过滤所有静态方法
                    if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                        continue;
                    }
                    //过滤所有私有方法
                    if (java.lang.reflect.Modifier.isPrivate(method.getModifiers())) {
                        continue;
                    }
                    // 过滤桥接方法和合成方法
                    if (method.isBridge() || method.isSynthetic()) {
                        continue;
                    }
                    if (method.isAnnotationPresent(GmCommand.class)) {
                        registerGmCommand(bean, method);
                        continue;
                    }
                    registerHandler(bean, method);
                }
            } catch (Throwable e) {
                // 处理异常，例如Bean初始化失败或类型转换错误
                log.error("处理Bean [{}] 时发生错误", beanName, e);
            }
        }
    }
}