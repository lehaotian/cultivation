package cls.cn.logic.manager;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 游戏处理器 此注释的类的方法
 * 参数1 必须为 Player 类型
 * 参数2 必须为 Message 类型
 * 返回值 Message 会返回客户端
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Service
public @interface GameHandler {
}
