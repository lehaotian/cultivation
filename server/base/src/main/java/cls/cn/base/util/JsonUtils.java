package cls.cn.base.util;

import cls.cn.base.exception.ErrorCode;
import cls.cn.base.exception.GameException;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // 忽略未知字段
        mapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JacksonException e) {
            throw new GameException(ErrorCode.SERVER_ERROR, e, "JsonUtils toJson obj:{}", obj);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JacksonException e) {
            throw new GameException(ErrorCode.SERVER_ERROR, e, "JsonUtils fromJson json:{} clazz:{}", json, clazz);
        }
    }
}