package cls.cn.base.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    /**
     * 将 long 时间戳按照 UTC+0 时区格式化为 yyyy-MM-dd_HH-mm-ss 字符串
     *
     * @param timestamp 时间戳
     * @return 格式化后的字符串
     */
    public static String formatTimestampToUTC(long timestamp) {
        // 根据时间戳创建 Instant 对象
        Instant instant = Instant.ofEpochMilli(timestamp);
        // 将 Instant 转换为 UTC+0 时区的 OffsetDateTime
        OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC);
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        // 格式化日期时间
        return offsetDateTime.format(formatter);
    }
}
