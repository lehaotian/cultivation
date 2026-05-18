package cls.cn.base.attr;

import java.util.Map;
import java.util.Optional;

/**
 * 动态属性接口
 */
public interface DynamicAttr {
    Map<AttrName<?>, Object> getAttrs();

    default <T> T getAttr(AttrName<T> name) {
        return Optional.ofNullable(getAttrs().get(name))
                .map(name.type()::cast)
                .orElse(null);
    }

    default <T> void setAttr(AttrName<T> name, T value) {
        getAttrs().put(name, value);
    }
}
