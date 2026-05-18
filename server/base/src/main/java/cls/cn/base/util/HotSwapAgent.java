package cls.cn.base.util;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public class HotSwapAgent {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static void reload(Class<?> clazz, byte[] newBytes) {
        ClassDefinition definition = new ClassDefinition(clazz, newBytes);
        try {
            instrumentation.redefineClasses(definition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}