package cls.cn.base.attr;

public record AttrName<T>(Class<T> type) {
    public static final AttrName<String> name = new AttrName<>(String.class);
    public static final AttrName<Long> id = new AttrName<>(Long.class);

}
